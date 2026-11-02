package com.kiyo.istoremanager.controller;

import com.kiyo.istoremanager.dto.PhotoOrderRequest;
import com.kiyo.istoremanager.dto.PhotoResponse;
import com.kiyo.istoremanager.model.User;
import com.kiyo.istoremanager.service.PhonePhotoService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/phones/{phoneId}/photos")
public class PhonePhotoController {

    private final PhonePhotoService photoService;

    public PhonePhotoController(PhonePhotoService photoService) {
        this.photoService = photoService;
    }

    @GetMapping
    public ResponseEntity<List<PhotoResponse>> list(@PathVariable Long phoneId,
                                                    @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(photoService.list(phoneId, user));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PhotoResponse> upload(@PathVariable Long phoneId,
                                                @RequestParam("file") MultipartFile file,
                                                @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(photoService.upload(phoneId, file, user));
    }

    @PutMapping("/order")
    public ResponseEntity<List<PhotoResponse>> reorder(@PathVariable Long phoneId,
                                                       @RequestBody PhotoOrderRequest request,
                                                       @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(photoService.reorder(phoneId, request.photoIds(), user));
    }

    @DeleteMapping("/{photoId}")
    public ResponseEntity<Void> delete(@PathVariable Long phoneId,
                                       @PathVariable Long photoId,
                                       @AuthenticationPrincipal User user) {
        photoService.delete(phoneId, photoId, user);
        return ResponseEntity.noContent().build();
    }
}