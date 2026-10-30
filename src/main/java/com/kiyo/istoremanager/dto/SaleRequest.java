package com.kiyo.istoremanager.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SaleRequest(BigDecimal salePrice, LocalDate saleDate) {}