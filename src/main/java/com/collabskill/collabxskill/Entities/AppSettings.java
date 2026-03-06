package com.collabskill.collabxskill.Entities;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppSettings {

    @Id
    private String id = "singleton"; // Single row
    private boolean maintenanceMode;

}