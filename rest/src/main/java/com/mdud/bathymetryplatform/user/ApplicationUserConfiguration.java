package com.mdud.bathymetryplatform.user;

import com.mdud.bathymetryplatform.user.authority.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationUserConfiguration {
    @Autowired
    private AuthorityRepository authorityRepository;

    @Bean
    public UserAuthorityProvider userAuthorityProvider() {
        return new UserAuthorityProvider(authorityRepository);
    }
}
