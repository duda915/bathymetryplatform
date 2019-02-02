package com.mdud.bathymetryplatform.security;

import com.mdud.bathymetryplatform.exception.UserNotFoundException;
import com.mdud.bathymetryplatform.user.ApplicationUser;
import com.mdud.bathymetryplatform.repository.UserRepository;
import com.mdud.bathymetryplatform.user.ApplicationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserDetailsConfiguration implements UserDetailsService {

    private final ApplicationUserRepository applicationUserRepository;

    @Autowired
    public UserDetailsConfiguration(ApplicationUserRepository applicationUserRepository) {
        this.applicationUserRepository = applicationUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        ApplicationUser applicationUser = applicationUserRepository.findByUsername(name).orElseThrow(() -> new UserNotFoundException("user not found"));

        List<GrantedAuthority> authorityList = applicationUser.getUserAuthorities().stream().map(userAuthority ->
                new SimpleGrantedAuthority(userAuthority.getAuthority().getAuthorityName().name())).collect(Collectors.toList());


        return User.builder()
                .username(applicationUser.getUsername())
                .password(applicationUser.getPassword())
                .authorities(authorityList)
                .build();

    }
}
