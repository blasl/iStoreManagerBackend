package com.kiyo.istoremanager.controller;

import com.kiyo.istoremanager.dto.ExpenseResponse;
import com.kiyo.istoremanager.model.ExtraExpense;
import com.kiyo.istoremanager.model.User;
import com.kiyo.istoremanager.service.ExtraExpenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExtraExpenseController {

    private final ExtraExpenseService expenseService;

    public ExtraExpenseController(ExtraExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping("/phone/{phoneId}")
    public ResponseEntity<ExpenseResponse> addExpense(@PathVariable Long phoneId,
                                                      @RequestBody ExtraExpense expense,
                                                      @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(expenseService.addExpenseToPhone(phoneId, expense, user));
    }

    @GetMapping("/phone/{phoneId}")
    public ResponseEntity<List<ExpenseResponse>> getExpensesByPhone(@PathVariable Long phoneId,
                                                                    @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(expenseService.getExpensesByPhone(phoneId, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id, @AuthenticationPrincipal User user) {
        expenseService.deleteExpense(id, user);
        return ResponseEntity.noContent().build();
    }
}