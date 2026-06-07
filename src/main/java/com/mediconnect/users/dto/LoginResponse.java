package com.mediconnect.users.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

// DTO di risposta login: token JWT, tipo e scadenza
@Data
@Builder
public class LoginResponse {

    private String token;
    private List<String> roles;
}