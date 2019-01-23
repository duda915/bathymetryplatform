package com.mdud.bathymetryplatform.repository;

import com.mdud.bathymetryplatform.user.authority.Authority;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Authority, Long> {
    Authority findDistinctByAuthorityName(String roleName);
}
