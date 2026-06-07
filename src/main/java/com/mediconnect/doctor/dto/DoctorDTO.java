package com.mediconnect.doctor.dto;

import com.mediconnect.enums.Specialization;
import com.mediconnect.users.dto.UserDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
// DTO per il trasferimento dati dei profili medici

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DoctorDTO {

    private Long id;

    private String firstName;
    private String lastName;

    private Specialization specialization;

    private String licenseNumber;

    private UserDTO user;

}
