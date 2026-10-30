package com.kiyo.istoremanager.dto;

import com.kiyo.istoremanager.model.ExtraExpense;

import java.math.BigDecimal;

public record ExpenseResponse(Long id, String concept, BigDecimal amount) {
    public static ExpenseResponse from(ExtraExpense e) {
        return new ExpenseResponse(e.getId(), e.getConcept(), e.getAmount());
    }
}