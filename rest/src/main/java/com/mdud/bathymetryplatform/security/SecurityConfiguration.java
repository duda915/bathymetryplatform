package com.mdud.bathymetryplatform.security;

import com.mdud.bathymetryplatform.datamodel.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private UserDetailsConfiguration userDetailsConfiguration;

    public SecurityConfiguration(@Autowired UserDetailsConfiguration userDetailsConfiguration) {
        this.userDetailsConfiguration = userDetailsConfiguration;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.userDetailsConfiguration)
                .passwordEncoder(AppUser.PASSWORD_ENCODER);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest().denyAll()
                .and()
                .formLogin().disable();
//                    .and()
//                .formLogin()
//                    .defaultSuccessUrl("/", true)
//                    .permitAll()
//                    .and()
//                .httpBasic()
//                    .and()
//                .csrf().disable()
//                .logout()
//                    .logoutSuccessUrl("/");
    }

    @Bean
    public AuthenticationManager authManager() throws Exception{
        return authenticationManager();
    }
}
