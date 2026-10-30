package com.kiyo.istoremanager.controller;

import com.kiyo.istoremanager.dto.SaleRequest;
import com.kiyo.istoremanager.dto.SaleResponse;
import com.kiyo.istoremanager.model.User;
import com.kiyo.istoremanager.service.SaleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping("/phone/{phoneId}")
    public ResponseEntity<SaleResponse> registerSale(@PathVariable Long phoneId,
                                                     @RequestBody SaleRequest request,
                                                     @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(saleService.registerSale(phoneId, request, user));
    }

    @GetMapping
    public ResponseEntity<List<SaleResponse>> getSales(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(saleService.getSalesWithProfit(user));
    }
}