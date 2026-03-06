package com.collabskill.collabxskill.repo;

import com.collabskill.collabxskill.Entities.AppSettings;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AppSettingsRepository extends JpaRepository<AppSettings, String> {
}