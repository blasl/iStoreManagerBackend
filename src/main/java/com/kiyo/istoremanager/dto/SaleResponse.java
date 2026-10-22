package com.kiyo.istoremanager.dto;

import com.kiyo.istoremanager.model.Phone;
import java.math.BigDecimal;
import java.time.LocalDate;

public record SaleResponse(
        Long id,
        BigDecimal salePrice,
        LocalDate saleDate,
        Phone phone,
        BigDecimal totalExpenses,
        BigDecimal netProfit
) {}
