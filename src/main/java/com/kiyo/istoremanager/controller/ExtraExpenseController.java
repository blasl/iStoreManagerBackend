package com.kiyo.istoremanager.controller;

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
    public ResponseEntity<ExtraExpense> addExpense(@PathVariable Long phoneId,
                                                   @RequestBody ExtraExpense expense,
                                                   @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(expenseService.addExpenseToPhone(phoneId, expense, user));
    }

    @GetMapping("/phone/{phoneId}")
    public ResponseEntity<List<ExtraExpense>> getExpensesByPhone(@PathVariable Long phoneId,
                                                                 @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(expenseService.getExpensesByPhone(phoneId, user));
    }
}