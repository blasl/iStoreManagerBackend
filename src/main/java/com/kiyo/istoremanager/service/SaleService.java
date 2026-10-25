package com.kiyo.istoremanager.service;

import com.kiyo.istoremanager.dto.SaleResponse;
import com.kiyo.istoremanager.model.ExtraExpense;
import com.kiyo.istoremanager.model.Phone;
import com.kiyo.istoremanager.model.PhoneStatus;
import com.kiyo.istoremanager.model.Sale;
import com.kiyo.istoremanager.model.User;
import com.kiyo.istoremanager.repository.ExtraExpenseRepository;
import com.kiyo.istoremanager.repository.PhoneRepository;
import com.kiyo.istoremanager.repository.SaleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final PhoneRepository phoneRepository;
    private final ExtraExpenseRepository extraExpenseRepository;

    public SaleService(SaleRepository saleRepository,
                       PhoneRepository phoneRepository,
                       ExtraExpenseRepository extraExpenseRepository) {
        this.saleRepository = saleRepository;
        this.phoneRepository = phoneRepository;
        this.extraExpenseRepository = extraExpenseRepository;
    }

    @Transactional
    public Sale registerSale(Long phoneId, BigDecimal salePrice, User user) {
        if (salePrice == null || salePrice.signum() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El precio de venta es obligatorio");
        }

        Phone phone = phoneRepository.findByIdAndUserId(phoneId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Teléfono no encontrado"));

        if (phone.getStatus() == PhoneStatus.VENDIDO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El teléfono ya figura como vendido");
        }
        if (phone.getStatus() == PhoneStatus.ELIMINADO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede vender un teléfono eliminado");
        }

        phone.setStatus(PhoneStatus.VENDIDO);
        Phone updatedPhone = phoneRepository.save(phone);

        Sale sale = new Sale();
        sale.setPhone(updatedPhone);
        sale.setSalePrice(salePrice);
        sale.setSaleDate(LocalDate.now());
        return saleRepository.save(sale);
    }

    public List<SaleResponse> getSalesWithProfit(User user) {
        return saleRepository.findByPhoneUserId(user.getId()).stream().map(sale -> {
            Phone phone = sale.getPhone();

            BigDecimal totalExpenses = extraExpenseRepository.findByPhoneId(phone.getId()).stream()
                    .map(ExtraExpense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal netProfit = sale.getSalePrice()
                    .subtract(phone.getPurchasePrice().add(totalExpenses));

            return new SaleResponse(sale.getId(), sale.getSalePrice(), sale.getSaleDate(),
                    phone, totalExpenses, netProfit);
        }).toList();
    }
}