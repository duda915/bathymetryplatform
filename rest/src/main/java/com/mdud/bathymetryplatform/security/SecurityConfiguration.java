package com.mdud.bathymetryplatform.security;

import com.mdud.bathymetryplatform.user.ApplicationUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private UserDetailsConfiguration userDetailsConfiguration;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return (BCryptPasswordEncoder) ApplicationUser.PASSWORD_ENCODER;
    }

    public SecurityConfiguration(@Autowired UserDetailsConfiguration userDetailsConfiguration) {
        this.userDetailsConfiguration = userDetailsConfiguration;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.userDetailsConfiguration)
                .passwordEncoder(ApplicationUser.PASSWORD_ENCODER);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest().denyAll()
                .and()
                .formLogin().disable();
    }


    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
