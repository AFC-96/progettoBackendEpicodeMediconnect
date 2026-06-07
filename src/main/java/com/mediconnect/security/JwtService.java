package com.mediconnect.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

/*
 Servizio core per la gestione dei token JWT.
 Si occupa di generare nuovi token durante il login (firma HMAC-SHA256)
 e di validare/decriptare i token ricevuti nelle richieste HTTP per estrarne l'email (Subject).
 Le chiavi crittografiche sono lette dal file application.properties.
 */
@Service
public class JwtService {

    // Stringa segreta (almeno 256 bit) definita nelle properties
    @Value("${jwt.secret.string}")
    private String JWT_SECRETE;

    // Durata del token in millisecondi (es. 24 ore)
    @Value("${jwt.expiration.time}")
    private long EXPIRATION_TIME;

    private SecretKey key; // Chiave crittografica HMAC-SHA256 usata per firmare e verificare i token

    @PostConstruct // Eseguito DOPO l'iniezione delle dipendenze
    private void init() {
        byte[] keyByte = JWT_SECRETE.getBytes(StandardCharsets.UTF_8);
        this.key = new SecretKeySpec(keyByte, "HmacSHA256");
    }

    // Genera un nuovo token JWT associato all'email utente
    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email) // Imposta il soggetto (chi è autenticato)
                .issuedAt(new Date(System.currentTimeMillis())) // Timestamp di emissione
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Timestamp di scadenza
                .signWith(key) // Firma il token con l'algoritmo HMAC e la chiave segreta
                .compact(); // Serializza nel formato header.payload.signature
    }

    // Estrae l'email (Subject) da un token precedentemente emesso
    public String getUsernameFromToken(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    // Metodo generico che decodifica il token, ne verifica la firma e restituisce
    // un Claim specifico
    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction) {
        return claimsTFunction.apply(Jwts.parser().verifyWith(key) // Verifica la firma crittografica
                .build()
                .parseSignedClaims(token) // Decodifica Base64
                .getPayload()); // Estrae i dati
    }

    // Valida completamente il token: verifica che appartenga all'utente e non sia
    // scaduto
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Verifica se il token ha superato la sua data di scadenza
    private boolean isTokenExpired(String token) {
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }
}
