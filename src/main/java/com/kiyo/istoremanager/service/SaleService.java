package com.kiyo.istoremanager.service;

import com.kiyo.istoremanager.dto.PhoneResponse;
import com.kiyo.istoremanager.dto.SaleRequest;
import com.kiyo.istoremanager.dto.SaleResponse;
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
    public SaleResponse registerSale(Long phoneId, SaleRequest request, User user) {
        if (request.salePrice() == null || request.salePrice().signum() < 0) {
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

        LocalDate saleDate = request.saleDate() != null ? request.saleDate() : LocalDate.now();
        // +1 day of tolerance for client/server timezone differences
        if (saleDate.isAfter(LocalDate.now().plusDays(1))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de venta no puede ser futura");
        }
        if (phone.getPurchaseDate() != null && saleDate.isBefore(phone.getPurchaseDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La fecha de venta no puede ser anterior a la fecha de compra");
        }

        phone.setStatus(PhoneStatus.VENDIDO);
        Phone updatedPhone = phoneRepository.save(phone);

        Sale sale = new Sale();
        sale.setPhone(updatedPhone);
        sale.setSalePrice(request.salePrice());
        sale.setSaleDate(saleDate);

        return toResponse(saleRepository.save(sale));
    }

    @Transactional(readOnly = true)
    public List<SaleResponse> getSalesWithProfit(User user) {
        return saleRepository.findByPhoneUserId(user.getId()).stream().map(this::toResponse).toList();
    }

    private SaleResponse toResponse(Sale sale) {
        Phone phone = sale.getPhone();
        BigDecimal totalExpenses = extraExpenseRepository.totalByPhoneId(phone.getId());
        BigDecimal totalCost = phone.getPurchasePrice().add(totalExpenses);
        BigDecimal netProfit = sale.getSalePrice().subtract(totalCost);

        return new SaleResponse(sale.getId(), sale.getSalePrice(), sale.getSaleDate(),
                PhoneResponse.from(phone, totalExpenses), totalExpenses, totalCost, netProfit);
    }
}