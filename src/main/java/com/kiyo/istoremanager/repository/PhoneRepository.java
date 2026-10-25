package com.kiyo.istoremanager.repository;

import com.kiyo.istoremanager.model.Phone;
import com.kiyo.istoremanager.model.PhoneStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhoneRepository extends JpaRepository<Phone, Long> {
    List<Phone> findByUserId(Long userId);

    List<Phone> findByUserIdAndStatus(Long userId, PhoneStatus status);

    Optional<Phone> findByImei(String imei);

    Optional<Phone> findByIdAndUserId(Long id, Long userId);
}
