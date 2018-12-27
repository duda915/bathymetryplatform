package com.mdud.bathymetryplatform.utility;

import com.mdud.bathymetryplatform.datamodel.*;
import com.mdud.bathymetryplatform.repository.BathymetryDataRepository;
import com.mdud.bathymetryplatform.repository.RoleRepository;
import com.mdud.bathymetryplatform.repository.UserRepository;
import com.mdud.bathymetryplatform.security.AppRoles;
import com.mdud.bathymetryplatform.utility.configuration.AppConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.sql.SQLException;
import java.util.*;

@Component
public class AppInitRunner implements CommandLineRunner {

    private Logger logger = LoggerFactory.getLogger(AppInitRunner.class);

    @Autowired
    private BathymetryDataRepository bathymetryDataRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AppConfiguration appConfiguration;

    @Override
    public void run(String... args) throws Exception {
        createGDALDir();
        try {
            addDefaultUsers();
        } catch (Exception e) {
            logger.info("Default users already created");
        }
    }

    private void createGDALDir() throws Exception {
        File dir = new File(appConfiguration.getGDALTargetLocation());
        if(!dir.exists()) {
            if(dir.mkdir()) {
                logger.info("GDAL dir created");
            } else {
                throw new Exception("GDAL dir creation failed");
            }
        } else {
            logger.info("GDAL dir already exist");
        }
    }

    private void addDefaultUsers() {
        Role userRole = roleRepository.findDistinctByRoleName(AppRoles.USER);
        Role superUserRole = roleRepository.findDistinctByRoleName(AppRoles.SUPER_USER);

        Set<UserRole> superUserRoles = new HashSet<>();
        superUserRoles.add(new UserRole(null, userRole));
        superUserRoles.add(new UserRole(null, superUserRole));

        Set<UserRole> defaultRoles = new HashSet<>();
        defaultRoles.add(new UserRole(null, userRole));

        AppUser superUser = new AppUser("superuser", "test", superUserRoles);
        AppUser newUser = new AppUser("newuser", "password", defaultRoles);

        userRepository.save(superUser);
        userRepository.save(newUser);

    }
}
