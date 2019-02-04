package com.mdud.bathymetryplatform.user.registration;

import com.mdud.bathymetryplatform.user.ApplicationUser;
import com.mdud.bathymetryplatform.user.ApplicationUserDTO;
import com.mdud.bathymetryplatform.user.ApplicationUserService;
import com.mdud.bathymetryplatform.utility.SQLDateBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;

@Service
public class RegistrationService {
    private final RegistrationRepository registrationRepository;
    private final ApplicationUserService applicationUserService;

    @Autowired
    public RegistrationService(RegistrationRepository registrationRepository, ApplicationUserService applicationUserService) {
        this.registrationRepository = registrationRepository;
        this.applicationUserService = applicationUserService;
    }

    @Transactional
    public RegistrationToken registerUser(ApplicationUserDTO applicationUserDTO){
        if(applicationUserDTO.getEmail().isEmpty() || applicationUserDTO.getEmail() == null) {
            throw new RegistrationException("email is empty");
        }

        ApplicationUser applicationUser = applicationUserService.addNewUser(applicationUserDTO.getUsername(),
                applicationUserDTO.getPassword(), applicationUserDTO.getEmail());
        RegistrationToken registrationToken = new RegistrationToken(applicationUser);
        return registrationRepository.save(registrationToken);

        //TODO send token to email
    }

    @Transactional
    public void activateUser(String token) {
        RegistrationToken registrationToken = registrationRepository
                .findByToken(token).orElseThrow(() -> new WrongTokenException("wrong token"));

        if(registrationToken.getExpirationDate().before(SQLDateBuilder.now())) {
            applicationUserService.activateUser(registrationToken.getApplicationUser().getUsername());
            registrationRepository.delete(registrationToken);
        } else {
            registrationRepository.delete(registrationToken);
            throw new RegistrationException("token expired");
        }
    }
}

