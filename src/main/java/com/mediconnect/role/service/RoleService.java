package com.mediconnect.role.service;

import com.mediconnect.res.Response;
import com.mediconnect.role.entity.Role;

import java.util.List;

/*
 Interfaccia che definisce il contratto CRUD per la gestione dei ruoli di sistema.
 A differenza degli altri servizi, qui non si usano DTO ma direttamente l'entità Role,
 essendo una struttura dati estremamente semplice.
 */
public interface RoleService {

    Response<Role> createRole(Role roleRequest);

    Response<Role> updateRole(Role roleRequest);

    Response<List<Role>> getAllRoles();

    Response<?> deleteRole(Long id);
}
