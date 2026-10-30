package com.kiyo.istoremanager.repository;

import com.kiyo.istoremanager.model.ExtraExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExtraExpenseRepository extends JpaRepository<ExtraExpense, Long> {

    List<ExtraExpense> findByPhoneId(Long phoneId);

    Optional<ExtraExpense> findByIdAndPhoneUserId(Long id, Long userId);

    @Query("SELECT e.phone.id, SUM(e.amount) FROM ExtraExpense e " +
            "WHERE e.phone.user.id = :userId GROUP BY e.phone.id")
    List<Object[]> sumByUser(@Param("userId") Long userId);

    @Query("SELECT SUM(e.amount) FROM ExtraExpense e WHERE e.phone.id = :phoneId")
    BigDecimal sumAmountByPhoneId(@Param("phoneId") Long phoneId);

    default BigDecimal totalByPhoneId(Long phoneId) {
        BigDecimal sum = sumAmountByPhoneId(phoneId);
        return sum != null ? sum : BigDecimal.ZERO;
    }
}