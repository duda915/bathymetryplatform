package com.mdud.bathymetryplatform.user.userauthority;

import com.mdud.bathymetryplatform.user.authority.AuthorityRepository;
import com.mdud.bathymetryplatform.user.userauthority.UserAuthorityProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserAuthorityConfiguration {
    @Autowired
    private AuthorityRepository authorityRepository;
    @Bean
    public UserAuthorityProvider userAuthorityProvider() {
        return new UserAuthorityProvider(authorityRepository);
    }

}
