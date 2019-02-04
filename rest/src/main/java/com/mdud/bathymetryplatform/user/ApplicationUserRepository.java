package com.mdud.bathymetryplatform.user;

import org.springframework.data.repository.CrudRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface ApplicationUserRepository extends CrudRepository<ApplicationUser, Long> {
    Optional<ApplicationUser> findByUsername(String username);
    Optional<ApplicationUser> findByEmail(String email);
}
