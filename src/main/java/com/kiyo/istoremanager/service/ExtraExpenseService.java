package com.kiyo.istoremanager.service;

import com.kiyo.istoremanager.model.ExtraExpense;
import com.kiyo.istoremanager.model.Phone;
import com.kiyo.istoremanager.model.User;
import com.kiyo.istoremanager.repository.ExtraExpenseRepository;
import com.kiyo.istoremanager.repository.PhoneRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ExtraExpenseService {

    private final ExtraExpenseRepository expenseRepository;
    private final PhoneRepository phoneRepository;

    public ExtraExpenseService(ExtraExpenseRepository expenseRepository, PhoneRepository phoneRepository) {
        this.expenseRepository = expenseRepository;
        this.phoneRepository = phoneRepository;
    }

    public ExtraExpense addExpenseToPhone(Long phoneId, ExtraExpense expense, User user) {
        Phone phone = findOwnedPhone(phoneId, user);

        if (expense.getConcept() == null || expense.getConcept().isBlank()
                || expense.getAmount() == null || expense.getAmount().signum() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El concepto y un monto mayor a 0 son obligatorios");
        }

        expense.setId(null);
        expense.setPhone(phone);
        return expenseRepository.save(expense);
    }

    public List<ExtraExpense> getExpensesByPhone(Long phoneId, User user) {
        findOwnedPhone(phoneId, user);   // ownership check
        return expenseRepository.findByPhoneId(phoneId);
    }

    private Phone findOwnedPhone(Long phoneId, User user) {
        return phoneRepository.findByIdAndUserId(phoneId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Teléfono no encontrado"));
    }
}