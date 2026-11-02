package com.kiyo.istoremanager.repository;

import com.kiyo.istoremanager.model.PhonePhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhonePhotoRepository extends JpaRepository<PhonePhoto, Long> {

    List<PhonePhoto> findByPhoneIdOrderByPositionAscIdAsc(Long phoneId);

    long countByPhoneId(Long phoneId);

    Optional<PhonePhoto> findByIdAndPhoneId(Long id, Long phoneId);

    @Query("SELECT p FROM PhonePhoto p JOIN FETCH p.phone ph WHERE ph.user.id = :userId " +
            "ORDER BY p.position ASC, p.id ASC")
    List<PhonePhoto> findAllByUserId(@Param("userId") Long userId);
}