package com.example.ecommerce.service;

import com.example.ecommerce.model.StoreSettings;
import com.example.ecommerce.repository.StoreSettingsRepository;
import org.springframework.stereotype.Service;

@Service
public class StoreSettingsService {
    private static final Long SETTINGS_ID = 1L;

    private final StoreSettingsRepository storeSettingsRepository;

    public StoreSettingsService(StoreSettingsRepository storeSettingsRepository) {
        this.storeSettingsRepository = storeSettingsRepository;
    }

    public StoreSettings get() {
        return storeSettingsRepository.findById(SETTINGS_ID).orElseGet(() -> {
            StoreSettings settings = new StoreSettings();
            settings.setId(SETTINGS_ID);
            settings.setStoreName("Ecommerce");
            settings.setContactEmail("");
            settings.setCurrency("HUF");
            return storeSettingsRepository.save(settings);
        });
    }

    public StoreSettings update(StoreSettings request) {
        StoreSettings settings = get();
        settings.setStoreName(request.getStoreName());
        settings.setContactEmail(request.getContactEmail());
        settings.setCurrency(request.getCurrency());
        return storeSettingsRepository.save(settings);
    }
}
