package com.mediconnect.patient.entity;

import com.mediconnect.appointment.entity.Appointment;
import com.mediconnect.enums.BloodGroup;
import com.mediconnect.enums.Genotype;
import com.mediconnect.users.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/*
 Entità JPA che rappresenta la tabella "patients" nel database.
 Mappa il profilo clinico e anagrafico di un paziente registrato nel sistema.
 È collegata in relazione One-to-One con l'entità User (per le credenziali di accesso)
 e in relazione One-to-Many con gli appuntamenti prenotati.
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Dati anagrafici base
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String phone;

    // Dati clinici (salvati come stringa di testo)
    @Column(columnDefinition = "TEXT")
    private String knownAllergies;

    // Enum per gruppo sanguigno (A_POS, B_NEG, ecc.) salvato come stringa (max 10 char)
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(10)")
    private BloodGroup bloodGroup;

    // Enum per genotipo (AA, AS, SS, ecc.) salvato come stringa
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(10)")
    private Genotype genotype;

    // Relazione One-to-One con l'account utente (caricamento LAZY per ottimizzare le query)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    // Relazione One-to-Many: un paziente può avere più appuntamenti.
    // orphanRemoval = true: se un appuntamento viene scollegato, viene eliminato dal DB.
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments;
}