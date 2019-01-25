package com.mdud.bathymetryplatform.security;

import com.mdud.bathymetryplatform.user.ApplicationUser;
import com.mdud.bathymetryplatform.repository.UserRepository;
import com.mdud.bathymetryplatform.user.ApplicationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsConfiguration implements UserDetailsService {

    private final ApplicationUserRepository applicationUserRepository;

    @Autowired
    public UserDetailsConfiguration(ApplicationUserRepository applicationUserRepository) {
        this.applicationUserRepository = applicationUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        ApplicationUser applicationUser = applicationUserRepository.findByUsername(name).orElse(null);

        assert applicationUser != null;
        String[] userAuthorities = applicationUser.getUserAuthorities().stream().map(userAuthority -> userAuthority.getAuthority().getAuthorityName().toString())
                .toArray(String[]::new);

        return new User(applicationUser.getUsername(), applicationUser.getPassword(),
                AuthorityUtils.createAuthorityList(userAuthorities));
    }
}
