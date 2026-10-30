package com.kiyo.istoremanager.service;

import com.kiyo.istoremanager.dto.ExpenseResponse;
import com.kiyo.istoremanager.model.ExtraExpense;
import com.kiyo.istoremanager.model.Phone;
import com.kiyo.istoremanager.model.PhoneStatus;
import com.kiyo.istoremanager.model.User;
import com.kiyo.istoremanager.repository.ExtraExpenseRepository;
import com.kiyo.istoremanager.repository.PhoneRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    public ExpenseResponse addExpenseToPhone(Long phoneId, ExtraExpense expense, User user) {
        Phone phone = findOwnedPhone(phoneId, user);

        if (phone.getStatus() == PhoneStatus.VENDIDO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se pueden agregar gastos a un teléfono vendido");
        }
        if (expense.getConcept() == null || expense.getConcept().isBlank()
                || expense.getAmount() == null || expense.getAmount().signum() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El concepto y un monto mayor a 0 son obligatorios");
        }

        expense.setId(null);
        expense.setConcept(expense.getConcept().trim());
        expense.setPhone(phone);
        return ExpenseResponse.from(expenseRepository.save(expense));
    }

    public List<ExpenseResponse> getExpensesByPhone(Long phoneId, User user) {
        findOwnedPhone(phoneId, user);
        return expenseRepository.findByPhoneId(phoneId).stream().map(ExpenseResponse::from).toList();
    }

    @Transactional
    public void deleteExpense(Long expenseId, User user) {
        ExtraExpense expense = expenseRepository.findByIdAndPhoneUserId(expenseId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gasto no encontrado"));

        if (expense.getPhone().getStatus() == PhoneStatus.VENDIDO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se pueden eliminar gastos de un teléfono vendido");
        }
        expenseRepository.delete(expense);
    }

    private Phone findOwnedPhone(Long phoneId, User user) {
        return phoneRepository.findByIdAndUserId(phoneId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Teléfono no encontrado"));
    }
}