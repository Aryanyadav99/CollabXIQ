package com.collabskill.collabxskill.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private String id;
    private String senderId;
    private String receiverId;
    private String content;
    private LocalDateTime timestamp;

    @PrePersist
    public void prePersist() {
        timestamp = ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime();
    }
}
