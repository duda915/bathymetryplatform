package com.mdud.bathymetryplatform.user.authority;


import com.mdud.bathymetryplatform.initializer.AbstractInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.stream.StreamSupport;

public class AuthoritiesInitializer extends AbstractInitializer {
    private Logger logger = LoggerFactory.getLogger(AuthoritiesInitializer.class);
    private AuthorityRepository authorityRepository;

    @Autowired
    public AuthoritiesInitializer(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    @Override
    public void init() {
        if(areAuthoritiesInitialized()) {
            logger.info("authorities initialized already");
            return;
        }

        Arrays.asList(Authorities.values()).forEach(authorityName -> authorityRepository.save(new Authority(authorityName)));
    }

    private boolean areAuthoritiesInitialized() {
        return StreamSupport.stream(authorityRepository.findAll().spliterator(), false).count() == 3;
    }
}

