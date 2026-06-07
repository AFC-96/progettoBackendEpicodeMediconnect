package com.mediconnect.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/*
 Configurazione globale del CORS (Cross-Origin Resource Sharing).
 Necessaria per consentire al frontend (es. applicazione React su porta 3000)
 di effettuare chiamate HTTP al backend Spring Boot (su porta 8080) senza
 essere bloccato dalle policy di sicurezza dei browser.
 */
@Configuration
public class CorsConfig {

    // Definisce e registra il filtro CORS nel contesto di Spring
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // In sviluppo si accetta tutto; in produzione andrebbero ristretti i domini
        config.addAllowedOrigin("*"); // Accetta richieste da qualsiasi dominio
        config.addAllowedHeader("*"); // Accetta qualsiasi header HTTP
        config.addAllowedMethod("*"); // Accetta metodi GET, POST, PUT, DELETE, ecc.

        // Cache per evitare continue chiamate di preflight (OPTIONS)
        config.setMaxAge(3600L);

        // Applica queste regole a tutti gli endpoint del server
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
