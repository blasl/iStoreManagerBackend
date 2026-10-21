package com.kiyo.istoremanager.controller;

import com.kiyo.istoremanager.model.Phone;
import com.kiyo.istoremanager.service.PhoneService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/phones")
@CrossOrigin(origins = "*")
public class PhoneController {
    private final PhoneService phoneService;

    public PhoneController(PhoneService phoneService) {
        this.phoneService = phoneService;
    }

    @PostMapping
    public ResponseEntity<Phone> createPhone(@RequestBody Phone phone) {
        return ResponseEntity.ok(phoneService.savePhone(phone));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Phone>> getPhonesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(phoneService.getPhonesByUser(userId));
    }

    @GetMapping("/user/{userId}/stock")
    public ResponseEntity<List<Phone>> getStockByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(phoneService.getAvailablePhones(userId));
    }

    @PatchMapping("/{phoneId}/sell")
    public ResponseEntity<Phone> sellPhone(@PathVariable Long phoneId, @RequestParam BigDecimal salePrice) {
        return ResponseEntity.ok(phoneService.sellPhone(phoneId, salePrice));
    }
}
