package com.kiyo.istoremanager.service;

import com.kiyo.istoremanager.model.Phone;
import com.kiyo.istoremanager.model.PhoneStatus;
import com.kiyo.istoremanager.repository.PhoneRepository;
import com.kiyo.istoremanager.repository.SaleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhoneService {
    private final PhoneRepository phoneRepository;

    public PhoneService(PhoneRepository phoneRepository, SaleRepository saleRepository) {
        this.phoneRepository = phoneRepository;
    }

    public Phone savePhone(Phone phone) {
        if (phoneRepository.findByImei(phone.getImei()).isPresent()) {
            throw new RuntimeException("El IMEI ya está registrado en el sistema");
        }

        if (phone.getStatus() == null) {
            phone.setStatus(PhoneStatus.DISPONIBLE);
        }

        return phoneRepository.save(phone);
    }

    public List<Phone> getPhonesByUser(Long userId) {
        return phoneRepository.findByUserId(userId);
    }

    public List<Phone> getAvailablePhones(Long userId) {
        return phoneRepository.findByUserIdAndStatus(userId, PhoneStatus.DISPONIBLE);
    }
}
