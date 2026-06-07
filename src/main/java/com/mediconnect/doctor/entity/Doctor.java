package com.mediconnect.doctor.entity;

import com.mediconnect.appointment.entity.Appointment;
import com.mediconnect.enums.Specialization;
import com.mediconnect.users.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/*
 Entità JPA che rappresenta la tabella "doctors" nel database.
 Mappa il profilo professionale di un medico con specializzazione e numero di licenza.
 Collegato in relazione OneToOne con User (account di login) e OneToMany con gli appuntamenti.
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "doctors")
public class Doctor {

    // Chiave primaria auto-generata
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nome e cognome del medico
    private String firstName;
    private String lastName;

    // Specializzazione medica (CARDIOLOGY, DERMATOLOGY, ecc.) salvata come stringa
    // nel DB
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(100)")
    private Specialization specialization;

    // Numero di licenza/abilitazione professionale
    private String licenseNumber;

    // Relazione OneToOne con User: ogni medico è collegato a UN account utente
    // unique = true impedisce che due profili medici puntino allo stesso User
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    // Relazione OneToMany: un medico può avere MOLTI appuntamenti
    // mappedBy = "doctor" indica che il lato proprietario è in Appointment
    // orphanRemoval = true: se rimuovo un appuntamento dalla lista, viene
    // cancellato anche dal DB
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments;
}