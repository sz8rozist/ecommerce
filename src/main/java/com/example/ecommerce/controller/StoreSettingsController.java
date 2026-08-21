package com.example.ecommerce.controller;

import com.example.ecommerce.model.StoreSettings;
import com.example.ecommerce.service.StoreSettingsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/settings")
public class StoreSettingsController {
    private final StoreSettingsService storeSettingsService;

    public StoreSettingsController(StoreSettingsService storeSettingsService) {
        this.storeSettingsService = storeSettingsService;
    }

    @GetMapping
    public ResponseEntity<StoreSettings> getSettings() {
        return ResponseEntity.ok(storeSettingsService.get());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping
    public ResponseEntity<StoreSettings> updateSettings(@Valid @RequestBody StoreSettings settings) {
        return ResponseEntity.ok(storeSettingsService.update(settings));
    }
}
