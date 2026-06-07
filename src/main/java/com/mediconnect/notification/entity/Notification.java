package com.mediconnect.notification.entity;

import com.mediconnect.enums.NotificationType;
import com.mediconnect.users.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
// Entità notifica: registra nel database ogni notifica inviata con destinatario e contenuto

@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;
    private String recipient;

    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType type; // Tipo di notifica

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Dipendenza: LocalDateTime
    private final LocalDateTime createdAt = LocalDateTime.now();
}
