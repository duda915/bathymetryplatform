package com.mdud.bathymetryplatform.user;

import com.mdud.bathymetryplatform.exception.UserAlreadyExistsException;
import com.mdud.bathymetryplatform.exception.UserException;
import com.mdud.bathymetryplatform.exception.UserNotFoundException;
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
        return tryGetApplicationUser(username).orElseThrow(() -> new UserNotFoundException("user " + username + " not exists"));
    }

    private Optional<ApplicationUser> tryGetApplicationUser(String username) {
        return applicationUserRepository.findByUsername(username);
    }

    private void throwIfUserExists(String username) {
        if(tryGetApplicationUser(username).orElse(null) != null) {
            throw new UserAlreadyExistsException("user " + username + " already exists");
        }
    }

    private void throwIfEmailExists(String email) {
        ApplicationUser applicationUser = applicationUserRepository.findByEmail(email)
                .orElse(null);
        if(applicationUser != null) {
            throw new UserAlreadyExistsException("email already in use");
        }
    }

    private void throwIfUserNotExists(String username) {
        getApplicationUser(username);
    }

    public ApplicationUser addNewUser(ApplicationUserDTO applicationUserDTO) {
        throwIfUserExists(applicationUserDTO.getUsername());
        throwIfEmailExists(applicationUserDTO.getEmail());

        UserAuthority readAuthority = userAuthorityProvider.getUserAuthority(Authorities.READ);
        UserAuthority writeAuthority = userAuthorityProvider.getUserAuthority(Authorities.WRITE);
        Set<UserAuthority> userAuthoritySet = new HashSet<>();
        userAuthoritySet.add(readAuthority);
        userAuthoritySet.add(writeAuthority);

        ApplicationUser applicationUser = new ApplicationUser(applicationUserDTO, userAuthoritySet);
        return applicationUserRepository.save(applicationUser);
    }

    public ApplicationUser changeUserPassword(String username, String newPassword) {
        throwIfUserNotExists(username);

        ApplicationUser applicationUser = getApplicationUser(username);

        if(ApplicationUser.PASSWORD_ENCODER.matches(newPassword, applicationUser.getPassword())) {
            throw new UserException("passwords are the same");
        }

        applicationUser.setPassword(newPassword);

        return applicationUserRepository.save(applicationUser);
    }

    public boolean checkUserAuthority(String username, Authorities authority) {
        return getApplicationUser(username).getUserAuthorities()
                .stream().anyMatch(userAuthority -> userAuthority.getAuthority().getAuthorityName() == authority);
    }

    public ApplicationUser activateUser(String username) {
        throwIfUserNotExists(username);

        ApplicationUser applicationUser = getApplicationUser(username);
        applicationUser.setActive(true);
        return applicationUserRepository.save(applicationUser);
    }

}
