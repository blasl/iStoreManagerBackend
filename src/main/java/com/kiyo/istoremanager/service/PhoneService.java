package com.kiyo.istoremanager.service;

import com.kiyo.istoremanager.model.Phone;
import com.kiyo.istoremanager.model.PhoneStatus;
import com.kiyo.istoremanager.model.User;
import com.kiyo.istoremanager.repository.PhoneRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class PhoneService {

    private final PhoneRepository phoneRepository;

    public PhoneService(PhoneRepository phoneRepository) {
        this.phoneRepository = phoneRepository;
    }

    public Phone savePhone(Phone phone, User owner) {
        validateRequired(phone);

        if (phoneRepository.findByImei(phone.getImei()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El IMEI ya está registrado en el sistema");
        }

        PhoneStatus status = phone.getStatus() != null ? phone.getStatus() : PhoneStatus.DISPONIBLE;
        requireEditableStatus(status);

        phone.setId(null);                 // never trust an id from the body
        phone.setUser(owner);              // always the authenticated user
        phone.setStatus(status);
        if (phone.getPurchaseDate() == null) {
            phone.setPurchaseDate(LocalDate.now());
        }
        return phoneRepository.save(phone);
    }

    public List<Phone> getPhones(User user) {
        return phoneRepository.findByUserId(user.getId());
    }

    public List<Phone> getAvailablePhones(User user) {
        return phoneRepository.findByUserIdAndStatus(user.getId(), PhoneStatus.DISPONIBLE);
    }

    public List<Phone> getActivePhones(User user) {
        return phoneRepository.findByUserId(user.getId()).stream()
                .filter(p -> p.getStatus() != PhoneStatus.ELIMINADO)
                .toList();
    }

    public Phone updatePhone(Long id, Phone details, User user) {
        Phone phone = findOwned(id, user);
        validateRequired(details);

        if (!details.getImei().equals(phone.getImei())
                && phoneRepository.findByImei(details.getImei())
                .filter(other -> !other.getId().equals(id)).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El IMEI ya está registrado en el sistema");
        }

        if (details.getStatus() != null && details.getStatus() != phone.getStatus()) {
            if (phone.getStatus() == PhoneStatus.VENDIDO || phone.getStatus() == PhoneStatus.ELIMINADO) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "No se puede cambiar el estado de un teléfono vendido o eliminado");
            }
            requireEditableStatus(details.getStatus());
            phone.setStatus(details.getStatus());
        }

        phone.setImei(details.getImei());
        phone.setModel(details.getModel());
        phone.setStorage(details.getStorage());
        phone.setBatteryHealth(details.getBatteryHealth());
        phone.setPurchasePrice(details.getPurchasePrice());
        phone.setTargetPrice(details.getTargetPrice());
        phone.setNotes(details.getNotes());
        if (details.getPurchaseDate() != null) {
            phone.setPurchaseDate(details.getPurchaseDate());
        }
        return phoneRepository.save(phone);
    }

    public void deletePhone(Long id, User user) {
        Phone phone = findOwned(id, user);
        if (phone.getStatus() == PhoneStatus.VENDIDO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar un teléfono ya vendido");
        }
        phone.setStatus(PhoneStatus.ELIMINADO);
        phoneRepository.save(phone);
    }

    // Returns 404 (not 403) for other users' phones so IDs can't be probed
    private Phone findOwned(Long id, User user) {
        return phoneRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Teléfono no encontrado"));
    }

    private void validateRequired(Phone p) {
        if (p.getImei() == null || p.getImei().isBlank()
                || p.getModel() == null || p.getModel().isBlank()
                || p.getPurchasePrice() == null || p.getPurchasePrice().signum() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "IMEI, modelo y precio de compra son obligatorios");
        }
    }

    // VENDIDO is only set by registering a sale; ELIMINADO only by DELETE
    private void requireEditableStatus(PhoneStatus status) {
        if (status != PhoneStatus.DISPONIBLE
                && status != PhoneStatus.EN_REPARACION
                && status != PhoneStatus.RESERVADO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado no permitido: " + status);
        }
    }
}