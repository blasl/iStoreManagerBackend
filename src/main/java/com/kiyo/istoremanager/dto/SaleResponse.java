package com.kiyo.istoremanager.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SaleResponse(
        Long id,
        BigDecimal salePrice,
        LocalDate saleDate,
        PhoneResponse phone,
        BigDecimal totalExpenses,
        BigDecimal totalCost,
        BigDecimal netProfit
) {}