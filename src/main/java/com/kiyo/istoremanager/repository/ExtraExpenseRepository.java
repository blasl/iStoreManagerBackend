package com.kiyo.istoremanager.repository;

import com.kiyo.istoremanager.model.ExtraExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExtraExpenseRepository extends JpaRepository<ExtraExpense, Long> {
    List<ExtraExpense> findByPhoneId(Long phoneId);
}