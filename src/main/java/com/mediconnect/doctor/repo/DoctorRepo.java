package com.mediconnect.doctor.repo;

import com.mediconnect.doctor.entity.Doctor;
import com.mediconnect.enums.Specialization;
import com.mediconnect.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/*
 Interfaccia che estende JpaRepository per gestire automaticamente le operazioni CRUD
 (salvataggio, ricerca, cancellazione) sulla tabella doctors.
 Include metodi personalizzati per cercare un medico per account utente o per specializzazione.
 */
public interface DoctorRepo extends JpaRepository<Doctor, Long> {

    // Cerca il profilo medico collegato a un account User specifico (relazione
    // OneToOne)
    Optional<Doctor> findByUser(User user);

    // Cerca tutti i medici con una determinata specializzazione (es. CARDIOLOGY,
    // NEUROLOGY)
    List<Doctor> findBySpecialization(Specialization specialization);
}
