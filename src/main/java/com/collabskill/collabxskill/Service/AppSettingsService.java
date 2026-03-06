package com.collabskill.collabxskill.Service;


public interface AppSettingsService {

    boolean isMaintenanceMode();

    void setMaintenanceMode(boolean enabled);
}
