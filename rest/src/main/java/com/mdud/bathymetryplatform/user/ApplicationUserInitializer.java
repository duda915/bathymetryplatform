package com.mdud.bathymetryplatform.user;

import com.mdud.bathymetryplatform.initializer.AbstractInitializer;
import com.mdud.bathymetryplatform.user.authority.Authorities;
import com.mdud.bathymetryplatform.user.userauthority.UserAuthority;
import com.mdud.bathymetryplatform.user.userauthority.UserAuthorityProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class ApplicationUserInitializer extends AbstractInitializer {
    private Logger logger = LoggerFactory.getLogger(ApplicationUserInitializer.class);

    private ApplicationUserRepository applicationUserRepository;
    private UserAuthorityProvider userAuthorityProvider;

    public ApplicationUserInitializer(ApplicationUserRepository applicationUserRepository, UserAuthorityProvider userAuthorityProvider) {

        this.applicationUserRepository = applicationUserRepository;
        this.userAuthorityProvider = userAuthorityProvider;
    }

    @Override
    public void init() {
        if(applicationUserRepository.findByUsername("admin").orElse(null) != null
                && applicationUserRepository.findByUsername("noauthority").orElse(null) != null) {
            logger.info("default users initialized already");
            return;
        }

        saveNoAuthorityUser();
        saveReadAuthorityUser();
        saveWriteAuthorityUser();
        saveAdminUser();
    }

    private void saveAdminUser() {
        Set<UserAuthority> authoritySet = new HashSet<>();
        authoritySet.add(userAuthorityProvider.getUserAuthority(Authorities.READ));
        authoritySet.add(userAuthorityProvider.getUserAuthority(Authorities.WRITE));
        authoritySet.add(userAuthorityProvider.getUserAuthority(Authorities.ADMIN));
        ApplicationUser admin = new ApplicationUser("admin", "admin", authoritySet);
        admin.setActive(true);
        applicationUserRepository.save(admin);
    }

    private void saveWriteAuthorityUser() {
        Set<UserAuthority> authoritySet = new HashSet<>();
        authoritySet.add(userAuthorityProvider.getUserAuthority(Authorities.READ));
        authoritySet.add(userAuthorityProvider.getUserAuthority(Authorities.WRITE));
        ApplicationUser writeAuthorityUser = new ApplicationUser("write", "write", authoritySet);
        writeAuthorityUser.setActive(true);
        applicationUserRepository.save(writeAuthorityUser);
    }

    private void saveReadAuthorityUser() {
        Set<UserAuthority> authoritySet = new HashSet<>();
        authoritySet.add(userAuthorityProvider.getUserAuthority(Authorities.READ));
        ApplicationUser readAuthorityUser = new ApplicationUser("read", "read", authoritySet);
        readAuthorityUser.setActive(true);
        applicationUserRepository.save(readAuthorityUser);
    }

    private void saveNoAuthorityUser() {
        ApplicationUser noAuthorityUser = new ApplicationUser("noauthority", "noauthority", new HashSet<>());
        applicationUserRepository.save(noAuthorityUser);
    }
}
