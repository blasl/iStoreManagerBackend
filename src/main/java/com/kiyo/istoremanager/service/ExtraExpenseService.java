package com.kiyo.istoremanager.service;

import com.kiyo.istoremanager.model.ExtraExpense;
import com.kiyo.istoremanager.model.Phone;
import com.kiyo.istoremanager.repository.ExtraExpenseRepository;
import com.kiyo.istoremanager.repository.PhoneRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExtraExpenseService {

    private final ExtraExpenseRepository expenseRepository;
    private final PhoneRepository phoneRepository;

    public ExtraExpenseService(ExtraExpenseRepository expenseRepository, PhoneRepository phoneRepository) {
        this.expenseRepository = expenseRepository;
        this.phoneRepository = phoneRepository;
    }

    public ExtraExpense addExpenseToPhone(Long phoneId, ExtraExpense expense) {
        Phone phone = phoneRepository.findById(phoneId)
                .orElseThrow(() -> new RuntimeException("Teléfono no encontrado"));

        expense.setPhone(phone);
        return expenseRepository.save(expense);
    }

    public List<ExtraExpense> getExpensesByPhone(Long phoneId) {
        return expenseRepository.findByPhoneId(phoneId);
    }
}