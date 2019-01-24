package com.mdud.bathymetryplatform.user.userauthority;

import com.mdud.bathymetryplatform.user.authority.Authorities;
import com.mdud.bathymetryplatform.user.authority.Authority;
import com.mdud.bathymetryplatform.user.authority.AuthorityRepository;

public class UserAuthorityProvider {
    private AuthorityRepository authorityRepository;

    public UserAuthorityProvider(AuthorityRepository authorityRepository) {

        this.authorityRepository = authorityRepository;
    }
    public UserAuthority getUserAuthority(Authorities authorityName) {
        Authority authority = authorityRepository.findAuthorityByAuthorityName(authorityName);
        return new UserAuthority(authority);
    }
}

