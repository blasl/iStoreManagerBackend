package com.kiyo.istoremanager.repository;

import com.kiyo.istoremanager.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    Optional<Sale> findByPhoneId(Long phoneId);
}