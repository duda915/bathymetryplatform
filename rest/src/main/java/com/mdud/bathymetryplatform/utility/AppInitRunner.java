package com.mdud.bathymetryplatform.utility;

import com.mdud.bathymetryplatform.repository.*;
import com.mdud.bathymetryplatform.security.AppRoles;
import com.mdud.bathymetryplatform.user.ApplicationUser;
import com.mdud.bathymetryplatform.user.authority.Authority;
import com.mdud.bathymetryplatform.user.UserAuthority;
import com.mdud.bathymetryplatform.utility.configuration.AppConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

@Component
public class AppInitRunner implements CommandLineRunner {

    private Logger logger = LoggerFactory.getLogger(AppInitRunner.class);

    @Autowired
    private BathymetryDataRepository bathymetryDataRepository;

    @Autowired
    private BathymetryMeasureRepository bathymetryMeasureRepository;

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
        Authority userAuthority = roleRepository.findDistinctByRoleName(AppRoles.USER);
        Authority superUserAuthority = roleRepository.findDistinctByRoleName(AppRoles.SUPER_USER);
        Authority guestAuthority = roleRepository.findDistinctByRoleName(AppRoles.GUEST);

        Set<UserAuthority> superUserAuthorities = new HashSet<>();
        superUserAuthorities.add(new UserAuthority(null, userAuthority));
        superUserAuthorities.add(new UserAuthority(null, superUserAuthority));

        Set<UserAuthority> defaultRoles = new HashSet<>();
        defaultRoles.add(new UserAuthority(null, userAuthority));

        Set<UserAuthority> guestUserAuthority = new HashSet<>();
        guestUserAuthority.add(new UserAuthority(null, guestAuthority));

        ApplicationUser superUser = new ApplicationUser("superuser", "test", superUserAuthorities);
        ApplicationUser newUser = new ApplicationUser("newuser", "password", defaultRoles);
        ApplicationUser guestUser = new ApplicationUser("guest", "guest", guestUserAuthority);

        userRepository.save(superUser);
        userRepository.save(newUser);
        userRepository.save(guestUser);

    }
}
