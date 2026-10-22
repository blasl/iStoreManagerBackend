package com.kiyo.istoremanager.service;

import com.kiyo.istoremanager.model.Phone;
import com.kiyo.istoremanager.model.PhoneStatus;
import com.kiyo.istoremanager.model.User;
import com.kiyo.istoremanager.repository.PhoneRepository;
import com.kiyo.istoremanager.repository.SaleRepository;
import com.kiyo.istoremanager.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhoneService {
    private final PhoneRepository phoneRepository;
    private final UserRepository userRepository;

    public PhoneService(PhoneRepository phoneRepository, UserRepository userRepository) {
        this.phoneRepository = phoneRepository;
        this.userRepository = userRepository;
    }

    public Phone savePhone(Phone phone) {
        if (phoneRepository.findByImei(phone.getImei()).isPresent()) {
            throw new RuntimeException("El IMEI ya está registrado en el sistema");
        }

        if (phone.getStatus() == null) {
            phone.setStatus(PhoneStatus.DISPONIBLE);
        }

        if (phone.getUser() == null || phone.getUser().getId() == null) {
            throw new RuntimeException("El teléfono debe estar asociado a un usuario válido");
        }

        Long userId = phone.getUser().getId();
        User fullUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario con ID " + userId + " no encontrado"));

        phone.setUser(fullUser);

        return phoneRepository.save(phone);
    }

    public List<Phone> getPhonesByUser(Long userId) {
        return phoneRepository.findByUserId(userId);
    }

    public List<Phone> getAvailablePhones(Long userId) {
        return phoneRepository.findByUserIdAndStatus(userId, PhoneStatus.DISPONIBLE);
    }
}
