package com.mdud.bathymetryplatform.user.authority;


import com.mdud.bathymetryplatform.initializer.AbstractInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Arrays;

public class AuthoritiesInitializer extends AbstractInitializer {
    private Logger logger = LoggerFactory.getLogger(AuthoritiesInitializer.class);
    private AuthorityRepository authorityRepository;

    @Autowired
    public AuthoritiesInitializer(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    @Override
    public void init() {
        try {
            Arrays.asList(Authorities.values()).forEach(authorityName ->
                    authorityRepository.save(new Authority(authorityName)));
        } catch (DataIntegrityViolationException e) {
            logger.info("authorities initialized already");
        }
    }
}

