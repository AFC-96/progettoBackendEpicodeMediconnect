package com.mediconnect.security;

import com.mediconnect.exceptions.CustomAccessDenialHandler;
import com.mediconnect.exceptions.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/*
 Classe di configurazione centrale per Spring Security.
 Definisce le regole di accesso all'API, disabilita le sessioni di stato (usa JWT stateless),
 configura il CORS, il gestore della crittografia password e inietta i custom handler
 per le eccezioni di sicurezza (401 Unauthorized e 403 Forbidden).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Permette l'uso di @PreAuthorize nei Controller
@RequiredArgsConstructor
public class SecurityFilter {

    // Filtro JWT personalizzato
    private final AuthFilter authFilter;
    // Gestore errori 401 (non autenticato)
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    // Gestore errori 403 (accesso negato)
    private final CustomAccessDenialHandler customAccessDenialHandler;

    // Configura la catena di sicurezza: CSRF disabilitato, CORS, rotte
    // pubbliche/protette e filtro JWT
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                // 1. Disabilita CSRF (non serve per API REST stateless con JWT)
                .csrf(AbstractHttpConfigurer::disable)
                // 2. Abilita la configurazione CORS definita in CorsConfig
                .cors(Customizer.withDefaults())
                // 3. Imposta i gestori custom per le eccezioni di sicurezza
                .exceptionHandling(ex -> ex.accessDeniedHandler(customAccessDenialHandler)
                        .authenticationEntryPoint(customAuthenticationEntryPoint))
                // 4. Regole di autorizzazione per gli URL
                .authorizeHttpRequests(
                        req -> req.requestMatchers("/api/auth/**", "/api/doctors/**", "/api/drugs/**").permitAll() // Rotte
                                                                                                                   // pubbliche
                                .anyRequest().authenticated()) // Tutto il resto richiede token JWT
                // 5. Configura l'applicazione come STATELESS (niente JSESSIONID nei cookie)
                .sessionManagement(mag -> mag.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 6. Inserisce il filtro custom AuthFilter PRIMA del filtro standard di Spring
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    // Configura BCrypt come algoritmo di hashing per le password
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Espone l'AuthenticationManager come Bean per poterlo usare nel servizio
    // AuthServiceImpl
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
