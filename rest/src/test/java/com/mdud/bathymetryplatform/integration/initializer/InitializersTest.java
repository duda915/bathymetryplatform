package com.mdud.bathymetryplatform.integration.initializer;

import com.mdud.bathymetryplatform.geoserver.GeoServerService;
import com.mdud.bathymetryplatform.user.ApplicationUserRepository;
import com.mdud.bathymetryplatform.user.authority.Authorities;
import com.mdud.bathymetryplatform.user.authority.AuthoritiesInitializer;
import com.mdud.bathymetryplatform.user.authority.AuthorityRepository;
import com.mdud.bathymetryplatform.utility.configuration.AppConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InitializersTest {

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private AppConfiguration appConfiguration;

    @Autowired
    private GeoServerService geoServerService;

    @Test
    public void authoritiesInitializer() {
        boolean act = Arrays.stream(Authorities.values())
                .allMatch(auth -> authorityRepository.findAuthorityByAuthorityName(auth) != null);

        assertTrue(act);
    }

    @Test
    public void applicationUserInitializer() {
        String[] users= {
                "admin",
                "write",
                "guest"
        };

        boolean act = Arrays.stream(users).allMatch(user -> applicationUserRepository.findByUsername(user).isPresent());

        assertTrue(act);
    }

    @Test
    public void gdalInitializer() {
        File gdalTargetDir = new File(appConfiguration.getGDALTargetLocation());

        boolean act = gdalTargetDir.exists() && gdalTargetDir.isDirectory();

        assertTrue(act);
    }

    @Test
    public void geoServerInitializer() {
        boolean isWorkspaceInitialized = geoServerService.checkIfWorkspaceExists();
        boolean isPrimaryStyleInitialized = geoServerService.checkIfStyleExists("primarystyle");
        boolean isSecondaryStyleInitialized = geoServerService.checkIfStyleExists("secondarystyle");

        assertTrue(isWorkspaceInitialized && isPrimaryStyleInitialized && isSecondaryStyleInitialized);
    }

}
