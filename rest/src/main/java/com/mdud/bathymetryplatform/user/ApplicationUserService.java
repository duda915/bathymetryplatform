package com.mdud.bathymetryplatform.user;

import com.mdud.bathymetryplatform.user.authority.Authorities;
import com.mdud.bathymetryplatform.user.userauthority.UserAuthority;
import com.mdud.bathymetryplatform.user.userauthority.UserAuthorityProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class ApplicationUserService {
    private final ApplicationUserRepository applicationUserRepository;

    private final UserAuthorityProvider userAuthorityProvider;

    @Autowired
    public ApplicationUserService(ApplicationUserRepository applicationUserRepository, UserAuthorityProvider userAuthorityProvider) {
        this.applicationUserRepository = applicationUserRepository;
        this.userAuthorityProvider = userAuthorityProvider;
    }

    public ApplicationUser getApplicationUser(String username) {
        return tryGetApplicationUser(username).orElseThrow(() -> new ApplicationUserServiceException("user " + username + " not exists"));
    }

    private Optional<ApplicationUser> tryGetApplicationUser(String username) {
        return applicationUserRepository.findByUsername(username);
    }

    private void throwIfUserExists(String username) {
        if(tryGetApplicationUser(username).orElse(null) != null) {
            throw new ApplicationUserServiceException("user " + username + " already exists");
        }
    }

    private void throwIfUserNotExists(String username) {
        getApplicationUser(username);
    }

    public ApplicationUser addNewUser(String username, String password) {
        throwIfUserExists(username);

        UserAuthority readAuthority = userAuthorityProvider.getUserAuthority(Authorities.READ);
        UserAuthority writeAuthority = userAuthorityProvider.getUserAuthority(Authorities.WRITE);
        Set<UserAuthority> userAuthoritySet = new HashSet<>();
        userAuthoritySet.add(readAuthority);
        userAuthoritySet.add(writeAuthority);

        ApplicationUser applicationUser = new ApplicationUser(username, password, userAuthoritySet);
        return applicationUserRepository.save(applicationUser);
    }

    public ApplicationUser changeUserPassword(String username, String newPassword) {
        throwIfUserNotExists(username);

        ApplicationUser applicationUser = getApplicationUser(username);

        if(ApplicationUser.PASSWORD_ENCODER.matches(newPassword, applicationUser.getPassword())) {
            throw new ApplicationUserServiceException("passwords are the same");
        }

        applicationUser.setPassword(newPassword);

        return applicationUserRepository.save(applicationUser);
    }

    public ApplicationUser addNewAuthority(String username, Authorities authority) {
        throwIfUserNotExists(username);

        ApplicationUser applicationUser = getApplicationUser(username);
        UserAuthority newAuthority = userAuthorityProvider.getUserAuthority(authority);

        if(applicationUser.getUserAuthorities().stream().anyMatch(userAuthority -> userAuthority.getAuthority() == newAuthority.getAuthority())) {
            throw new ApplicationUserServiceException("user own this authority already");
        }

        applicationUser.getUserAuthorities().add(newAuthority);
        return applicationUserRepository.save(applicationUser);
    }

    public ApplicationUser removeUserAuthority(String username, Authorities authority) {
        throwIfUserNotExists(username);

        ApplicationUser applicationUser = getApplicationUser(username);
        UserAuthority removeAuthority = userAuthorityProvider.getUserAuthority(authority);

        if(!applicationUser.getUserAuthorities().stream().anyMatch(userAuthority -> userAuthority.getAuthority() == removeAuthority.getAuthority())) {
            throw new ApplicationUserServiceException("user do not have this authority");
        }

        applicationUser.getUserAuthorities().removeIf(userAuthority -> userAuthority.getAuthority() == removeAuthority.getAuthority());

        return applicationUserRepository.save(applicationUser);
    }

    public void removeUser(String username) {
        throwIfUserNotExists(username);
        ApplicationUser applicationUser = getApplicationUser(username);
        applicationUserRepository.delete(applicationUser);
    }


}
