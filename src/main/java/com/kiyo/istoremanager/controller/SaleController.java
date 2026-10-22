package com.kiyo.istoremanager.controller;

import com.kiyo.istoremanager.dto.SaleResponse;
import com.kiyo.istoremanager.model.Sale;
import com.kiyo.istoremanager.service.SaleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
@CrossOrigin(origins = "*")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping("/phone/{phoneId}")
    public ResponseEntity<Sale> registerSale(@PathVariable Long phoneId, @RequestBody Sale saleRequest) {
        return ResponseEntity.ok(saleService.registerSale(phoneId, saleRequest.getSalePrice()));
    }

    @GetMapping
    public ResponseEntity<List<SaleResponse>> getAllSales() {
        return ResponseEntity.ok(saleService.getAllSalesWithProfit());
    }
}