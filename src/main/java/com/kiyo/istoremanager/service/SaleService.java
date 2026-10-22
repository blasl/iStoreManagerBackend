package com.kiyo.istoremanager.service;

import com.kiyo.istoremanager.dto.SaleResponse;
import com.kiyo.istoremanager.model.ExtraExpense;
import com.kiyo.istoremanager.model.Phone;
import com.kiyo.istoremanager.model.PhoneStatus;
import com.kiyo.istoremanager.model.Sale;
import com.kiyo.istoremanager.repository.ExtraExpenseRepository;
import com.kiyo.istoremanager.repository.PhoneRepository;
import com.kiyo.istoremanager.repository.SaleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final PhoneRepository phoneRepository;
    private final ExtraExpenseRepository extraExpenseRepository;

    public SaleService(
            SaleRepository saleRepository,
            PhoneRepository phoneRepository,
            ExtraExpenseRepository extraExpenseRepository
    ) {
        this.saleRepository = saleRepository;
        this.phoneRepository = phoneRepository;
        this.extraExpenseRepository = extraExpenseRepository;
    }

    public Sale registerSale(Long phoneId, BigDecimal actualSalePrice) {
        Phone phone = phoneRepository.findById(phoneId)
                .orElseThrow(() -> new RuntimeException("Teléfono no encontrado"));

        if (phone.getStatus() == PhoneStatus.VENDIDO) {
            throw new RuntimeException("El teléfono ya figura como vendido");
        }

        phone.setStatus(PhoneStatus.VENDIDO);
        Phone updatedPhone = phoneRepository.save(phone);

        Sale sale = new Sale();
        sale.setPhone(updatedPhone);
        sale.setSalePrice(actualSalePrice);
        sale.setSaleDate(LocalDate.now());

        return saleRepository.save(sale);
    }

    public List<SaleResponse> getAllSalesWithProfit() {
        List<Sale> sales = saleRepository.findAll();

        return sales.stream().map(sale -> {
            Phone phone = sale.getPhone();

            List<ExtraExpense> expenses = extraExpenseRepository.findByPhoneId(phone.getId());
            BigDecimal totalExpenses = expenses.stream()
                    .map(ExtraExpense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalCost = phone.getPurchasePrice().add(totalExpenses);

            BigDecimal netProfit = sale.getSalePrice().subtract(totalCost);

            return new SaleResponse(
                    sale.getId(),
                    sale.getSalePrice(),
                    sale.getSaleDate(),
                    phone,
                    totalExpenses,
                    netProfit
            );
        }).toList();
    }
}