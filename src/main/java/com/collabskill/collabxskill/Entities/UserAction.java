package com.collabskill.collabxskill.Entities;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.collabskill.collabxskill.extra.ActionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.Data;

@Entity
@Data
public class UserAction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // User who performs the action
    @ManyToOne
    private User fromUser;

    // User who receives the action
    @ManyToOne
    private User toUser;

    // Action type: Request,Wait,Nope
    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private boolean isRejected;

    private boolean isAccepted; // Indicates if the action was accepted by the receiver

    @PrePersist
    public void prePersist() {
        this.createdAt = ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime();
    }

}