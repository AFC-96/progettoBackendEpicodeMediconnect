package com.mediconnect.role.service;

import com.mediconnect.exceptions.NotFoundException;
import com.mediconnect.res.Response;
import com.mediconnect.role.entity.Role;
import com.mediconnect.role.repo.RoleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 Implementazione del servizio per la gestione dei ruoli.
 Esegue operazioni CRUD di base (Create, Read, Update, Delete) sulla tabella "roles".
 Questa classe lavora direttamente con le entità JPA in quanto la struttura dei ruoli
 è composta da soli 2 campi (id, name) e l'uso di un DTO non offrirebbe vantaggi reali.
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    // Repository per i ruoli
    private final RoleRepo roleRepo;

    // Crea e salva un nuovo record nel database
    @Override
    public Response<Role> createRole(Role roleRequest) {
        // Salva direttamente l'entità passata dal Controller
        Role savedRole = roleRepo.save(roleRequest);

        return Response.<Role>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Role Saved Successfully")
                .data(savedRole)
                .build();
    }

    // Aggiorna i dati di un record esistente
    @Override
    public Response<Role> updateRole(Role roleRequest) {
        // 1. Verifica che il ruolo da modificare esista
        Role role = roleRepo.findById(roleRequest.getId())
                .orElseThrow(() -> new NotFoundException("Role not found"));

        // 2. Modifica il campo nome
        role.setName(roleRequest.getName());

        // 3. Salva l'entità aggiornata nel DB
        Role updatedRole = roleRepo.save(role);

        return Response.<Role>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Role updated successfully")
                .data(updatedRole)
                .build();
    }

    // Recupera l'elenco completo di tutti i record
    @Override
    public Response<List<Role>> getAllRoles() {
        List<Role> roles = roleRepo.findAll();
        return Response.<List<Role>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Roles retreived successfully")
                .data(roles)
                .build();
    }

    // Elimina un ruolo dal sistema in base all'ID fornito
    @Override
    public Response<?> deleteRole(Long id) {
        // Controlla l'esistenza prima di eliminare per restituire un errore coerente
        // (404)
        if (!roleRepo.existsById(id)) {
            throw new NotFoundException("Role Not Found");
        }

        roleRepo.deleteById(id);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Role deleted successfully")
                .build();
    }
}
