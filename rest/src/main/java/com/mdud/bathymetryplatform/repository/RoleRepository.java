package com.mdud.bathymetryplatform.repository;

import com.mdud.bathymetryplatform.datamodel.Role;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role, Long> {
    Role findDistinctByRoleName(String roleName);
}
