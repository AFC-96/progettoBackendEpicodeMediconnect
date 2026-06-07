package com.mediconnect.security;

import com.mediconnect.exceptions.CustomAuthenticationEntryPoint;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*
 Filtro di sicurezza JWT personalizzato.
 Intercetta ogni singola richiesta HTTP in ingresso (OncePerRequestFilter).
 Verifica la presenza di un token JWT valido nell'header "Authorization" e, se valido,
 costruisce il contesto di sicurezza (SecurityContext) di Spring per quella specifica richiesta.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    // Servizio per generazione e validazione token JWT
    private final JwtService tokenService;
    // Gestore errori 401 (non autenticato)
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    // Carica i dati utente dal database
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // STEP 1: Estrae il token JWT dall'header "Authorization: Bearer ..."
        String token = getTokenFromRequest(request);

        // Se il token è null (rotta pubblica), salta direttamente allo STEP 5
        if (token != null) {
            String email;
            try {
                // STEP 2: Estrae l'email (subject) dal token JWT verificandone la firma
                email = tokenService.getUsernameFromToken(token);
            } catch (Exception e) {
                // Se il token è manomesso, scaduto o non valido, gestisce l'eccezione e
                // risponde con 401 UNAUTHORIZED
                log.error("Exception occurred while extracting username from token: {}", e.getMessage());
                AuthenticationException authException = new BadCredentialsException(e.getMessage());
                customAuthenticationEntryPoint.commence(request, response, authException);
                return;
            }

            // STEP 3: Carica i dettagli completi dell'utente dal DB tramite la sua email
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

            // STEP 4: Validazione finale e impostazione del SecurityContext
            if (StringUtils.hasText(email) && tokenService.isTokenValid(token, userDetails)) {
                // Genera il token di autenticazione per Spring Security contenente i ruoli
                // (Authorities)
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Salva l'autenticazione nel contesto: da qui in poi @PreAuthorize conosce
                // l'utente autenticato
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        try {
            // STEP 5: Passa la richiesta al prossimo filtro nella catena (o al Controller)
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    // Metodo helper per estrarre la stringa token eliminando il prefisso "Bearer "
    private String getTokenFromRequest(HttpServletRequest request) {
        String tokenWithBearer = request.getHeader("Authorization");
        if (tokenWithBearer != null && tokenWithBearer.startsWith("Bearer ")) {
            return tokenWithBearer.substring(7);
        }
        return null;
    }
}
