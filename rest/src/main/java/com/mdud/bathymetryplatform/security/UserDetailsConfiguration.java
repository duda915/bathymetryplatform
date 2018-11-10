package com.mdud.bathymetryplatform.security;

import com.mdud.bathymetryplatform.datamodel.AppUser;
import com.mdud.bathymetryplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsConfiguration implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsConfiguration(@Autowired UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        AppUser appUser = userRepository.findDistinctByUsername(name);

        //parse roles
        String[] userDetailsRoles = appUser.getUserRoles().stream().map(x -> x.getRole().getRoleName())
                .toArray(String[]::new);

        return new User(appUser.getUsername(), appUser.getPassword(),
                AuthorityUtils.createAuthorityList(userDetailsRoles));
    }
}
