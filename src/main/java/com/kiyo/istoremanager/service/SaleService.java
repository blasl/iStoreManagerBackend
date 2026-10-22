package com.kiyo.istoremanager.service;

import com.kiyo.istoremanager.model.Phone;
import com.kiyo.istoremanager.model.PhoneStatus;
import com.kiyo.istoremanager.model.Sale;
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

    public SaleService(SaleRepository saleRepository, PhoneRepository phoneRepository) {
        this.saleRepository = saleRepository;
        this.phoneRepository = phoneRepository;
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

    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }
}