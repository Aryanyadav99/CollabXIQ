package com.collabskill.collabxskill.Service.ServiceImpl;

import com.collabskill.collabxskill.Entities.AppSettings;
import com.collabskill.collabxskill.Service.AppSettingsService;
import com.collabskill.collabxskill.repo.AppSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppSettingsServiceImpl implements AppSettingsService {

    private final AppSettingsRepository settingsRepo;

    @Override
    public boolean isMaintenanceMode() {
        return settingsRepo.findById("singleton").map(AppSettings::isMaintenanceMode).orElse(false);
    }

    @Override
    public void setMaintenanceMode(boolean enabled) {
        AppSettings settings = settingsRepo.findById("singleton").orElse(new AppSettings());
        settings.setMaintenanceMode(enabled);
        settingsRepo.save(settings);
    }
}
