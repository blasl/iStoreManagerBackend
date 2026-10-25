package com.kiyo.istoremanager.controller;

import com.kiyo.istoremanager.model.Phone;
import com.kiyo.istoremanager.model.User;
import com.kiyo.istoremanager.service.PhoneService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/phones")
public class PhoneController {

    private final PhoneService phoneService;

    public PhoneController(PhoneService phoneService) {
        this.phoneService = phoneService;
    }

    @PostMapping
    public ResponseEntity<Phone> createPhone(@RequestBody Phone phone, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(phoneService.savePhone(phone, user));
    }

    @GetMapping
    public ResponseEntity<List<Phone>> getPhones(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(phoneService.getPhones(user));
    }

    @GetMapping("/stock")
    public ResponseEntity<List<Phone>> getStock(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(phoneService.getAvailablePhones(user));
    }

    @GetMapping("/active")
    public ResponseEntity<List<Phone>> getActive(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(phoneService.getActivePhones(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Phone> updatePhone(@PathVariable Long id, @RequestBody Phone phone,
                                             @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(phoneService.updatePhone(id, phone, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePhone(@PathVariable Long id, @AuthenticationPrincipal User user) {
        phoneService.deletePhone(id, user);
        return ResponseEntity.noContent().build();
    }
}