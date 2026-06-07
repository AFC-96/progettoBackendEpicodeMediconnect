package com.mediconnect.security;

import com.mediconnect.exceptions.NotFoundException;
import com.mediconnect.users.entity.User;
import com.mediconnect.users.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/*
 Servizio personalizzato utilizzato da Spring Security per recuperare
 i dati dell'utente dal database in fase di login o validazione token.
 Implementa l'interfaccia core UserDetailsService.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    // Cerca l'utente nel DB tramite email e lo impacchetta nella classe AuthUser
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Trova l'utente o lancia un'eccezione se non esiste
        User user = userRepo.findByEmail(username)
                .orElseThrow(()-> new NotFoundException("Email Not Found"));

        // Restituisce l'Adapter AuthUser per compatibilità con Spring Security
        return AuthUser.builder()
                .user(user)
                .build();
    }
}
