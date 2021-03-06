package com.mdud.bathymetryplatform.user.registration;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RegistrationRepository extends CrudRepository<RegistrationToken, Long> {
    Optional<RegistrationToken> findByToken(String token);
}
