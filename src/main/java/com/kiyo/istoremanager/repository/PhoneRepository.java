package com.kiyo.istoremanager.repository;

import com.kiyo.istoremanager.model.Phone;
import com.kiyo.istoremanager.model.PhoneStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhoneRepository extends JpaRepository<Phone, Long> {
    List<Phone> findByUserId(Long userId);

    List<Phone> findByUserIdAndStatus(Long userId, PhoneStatus status);

    Optional<Phone> findByImei(String imei);

    Optional<Phone> findByIdAndUserId(Long id, Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Phone p WHERE p.id = :id AND p.user.id = :userId")
    Optional<Phone> findByIdAndUserIdForUpdate(@Param("id") Long id, @Param("userId") Long userId);
}
