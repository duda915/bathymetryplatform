package com.mdud.bathymetryplatform.user;

import com.mdud.bathymetryplatform.user.authority.AuthorityRepository;
import com.mdud.bathymetryplatform.user.userauthority.UserAuthorityProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationUserConfiguration {

    private final ApplicationUserRepository applicationUserRepository;

    private final UserAuthorityProvider userAuthorityProvider;

    @Autowired
    public ApplicationUserConfiguration(ApplicationUserRepository applicationUserRepository, UserAuthorityProvider userAuthorityProvider) {
        this.applicationUserRepository = applicationUserRepository;
        this.userAuthorityProvider = userAuthorityProvider;
    }

    @Bean
    public ApplicationUserInitializer applicationUserInitializer() {
        return new ApplicationUserInitializer(applicationUserRepository, userAuthorityProvider);
    }
}

