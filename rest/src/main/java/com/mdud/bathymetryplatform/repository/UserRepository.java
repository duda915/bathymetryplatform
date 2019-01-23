package com.mdud.bathymetryplatform.repository;

import com.mdud.bathymetryplatform.user.ApplicationUser;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<ApplicationUser, Long> {
    ApplicationUser findDistinctByUsername(String username);
}
