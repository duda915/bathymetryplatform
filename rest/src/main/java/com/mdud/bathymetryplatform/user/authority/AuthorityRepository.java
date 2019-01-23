package com.mdud.bathymetryplatform.user.authority;

import org.springframework.data.repository.CrudRepository;

public interface AuthorityRepository extends CrudRepository<Authority, Long> {
    Authority findAuthorityByAuthorityName(Authorities authorities);
}

