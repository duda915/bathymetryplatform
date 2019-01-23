package com.mdud.bathymetryplatform.user;

import com.mdud.bathymetryplatform.user.authority.Authorities;
import com.mdud.bathymetryplatform.user.authority.Authority;
import com.mdud.bathymetryplatform.user.authority.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

