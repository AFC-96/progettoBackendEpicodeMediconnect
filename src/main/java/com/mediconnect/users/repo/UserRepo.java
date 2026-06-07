package com.mediconnect.users.repo;

import com.mediconnect.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/*
 Interfaccia repository per l'accesso ai dati della tabella "Users".
 Oltre ai metodi CRUD standard, fornisce un metodo per la ricerca tramite email,
 cruciale per l'autenticazione (login) e la verifica dei duplicati in registrazione.
 */
public interface UserRepo extends JpaRepository<User, Long> {

    // Cerca un utente per email. Ritorna Optional per gestire elegantemente il caso in cui non esista.
    Optional<User> findByEmail(String email);
}
