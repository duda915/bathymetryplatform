package com.mdud.bathymetryplatform.user.authority;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
class AuthoritiesInitializer implements CommandLineRunner {
    private Logger logger = LoggerFactory.getLogger(AuthoritiesInitializer.class);
    private AuthorityRepository authorityRepository;

    @Autowired
    public AuthoritiesInitializer(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    @Override
    public void run(String... args) {
        try {
            Arrays.asList(Authorities.values()).forEach(authorityName -> {
                authorityRepository.save(new Authority(authorityName));
            });
        } catch (DataIntegrityViolationException e) {
            logger.info("authorities initialized already");
        }
    }
}
