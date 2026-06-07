package com.mediconnect.security;

import com.mediconnect.users.entity.User;
import lombok.Builder;
import lombok.Data;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/*
 Wrapper (Adapter) che converte l'entità User del nostro database
 nell'interfaccia UserDetails richiesta da Spring Security.
 Serve per fornire le informazioni di login (email, password cifrata)
 e i ruoli autorizzativi (Authorities) necessari per il controllo accessi.
 */
@Builder
@Data
public class AuthUser implements UserDetails {

    // L'entità User reale caricata dal database
    private User user;

    // Converte la lista di Role in una lista di SimpleGrantedAuthority usata da Spring
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .toList();
    }

    // Restituisce la password crittografata (BCrypt) dell'utente
    @Override
    public @Nullable String getPassword() {
        return user.getPassword();
    }

    // Nel nostro sistema l'username di login è l'indirizzo email
    @Override
    public String getUsername() {
        return user.getEmail();
    }
}
