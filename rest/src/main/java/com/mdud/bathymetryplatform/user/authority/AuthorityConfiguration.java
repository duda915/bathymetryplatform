package com.mdud.bathymetryplatform.user.authority;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class AuthorityConfiguration {
    private AuthorityRepository authorityRepository;

    @Autowired
    public AuthorityConfiguration(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    @Bean
    public AuthoritiesInitializer authoritiesInitializer() {
        return new AuthoritiesInitializer(authorityRepository);
    }
}
