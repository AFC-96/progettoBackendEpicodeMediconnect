package com.mediconnect.role.repo;

import com.mediconnect.role.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/*
 Interfaccia che estende JpaRepository per la gestione della tabella "roles".
 Fondamentale per l'assegnazione dei ruoli durante la registrazione di nuovi utenti,
 per cui fornisce un metodo personalizzato di ricerca per nome.
 */
public interface RoleRepo extends JpaRepository<Role, Long> {

    // Recupera un ruolo conoscendo esattamente il suo nome testuale (es. "PATIENT" o "DOCTOR").
    // Usato dal servizio di autenticazione per assegnare i permessi corretti in fase di signup.
    Optional<Role> findByName(String name);
}