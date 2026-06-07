package com.mediconnect.users.entity;

import com.mediconnect.role.entity.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/*
 Entità JPA che mappa la tabella "Users" nel database.
 Rappresenta il fulcro del sistema di identità di MediConnect: contiene le credenziali (email, password cifrata)
 e l'anagrafica di base. È la classe radice che, tramite chiavi esterne, viene estesa 
 in profili specifici (Patient o Doctor) e si collega ai ruoli tramite una tabella di join.
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Dati base
    private String name;
    private String lastName;

    // Email usata come username per il login, deve essere univoca
    @Column(unique = true)
    private String email;

    // Password cifrata con BCrypt
    @Column(nullable = false)
    private String password;

    // URL o percorso relativo per l'avatar utente
    private String profilePictureUrl;

    // Gestione automatica timestamp di creazione
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = true)
    private LocalDateTime createdAt;

    // Gestione automatica timestamp di ultima modifica
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    // Relazione Many-to-Many con i ruoli (es. un utente può essere sia DOCTOR che ADMIN).
    // Caricamento EAGER perché i ruoli servono immediatamente durante l'autenticazione.
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", 
               joinColumns = @JoinColumn(name = "user_id"), 
               inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;
}
