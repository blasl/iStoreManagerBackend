package com.kiyo.istoremanager.dto;

import com.kiyo.istoremanager.model.Phone;
import com.kiyo.istoremanager.model.PhoneStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PhoneResponse(
        Long id,
        String imei,
        String model,
        String storage,
        Integer batteryHealth,
        BigDecimal purchasePrice,
        BigDecimal targetPrice,
        PhoneStatus status,
        LocalDate purchaseDate,
        String notes,
        BigDecimal totalExpenses
) {
    public static PhoneResponse from(Phone p, BigDecimal totalExpenses) {
        return new PhoneResponse(p.getId(), p.getImei(), p.getModel(), p.getStorage(),
                p.getBatteryHealth(), p.getPurchasePrice(), p.getTargetPrice(), p.getStatus(),
                p.getPurchaseDate(), p.getNotes(), totalExpenses);
    }
}