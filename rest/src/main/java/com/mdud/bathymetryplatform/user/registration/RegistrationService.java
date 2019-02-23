package com.mdud.bathymetryplatform.user.registration;

import com.mdud.bathymetryplatform.user.ApplicationUser;
import com.mdud.bathymetryplatform.user.ApplicationUserDTO;
import com.mdud.bathymetryplatform.user.ApplicationUserService;
import com.mdud.bathymetryplatform.utility.SQLDateBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RegistrationService {
    private final RegistrationRepository registrationRepository;
    private final ApplicationUserService applicationUserService;
    private final MailService mailService;

    @Autowired
    public RegistrationService(RegistrationRepository registrationRepository, ApplicationUserService applicationUserService, MailService mailService) {
        this.registrationRepository = registrationRepository;
        this.applicationUserService = applicationUserService;
        this.mailService = mailService;
    }

    @Transactional
    public RegistrationToken registerUser(ApplicationUserDTO applicationUserDTO){
        ApplicationUser applicationUser = applicationUserService.addNewUser(applicationUserDTO);

        RegistrationToken registrationToken = registrationRepository.save(new RegistrationToken(applicationUser));
        mailService.sendActivationLink(registrationToken);

        return registrationToken;
    }

    @Transactional
    public void activateUser(String token) {
        RegistrationToken registrationToken = registrationRepository
                .findByToken(token).orElseThrow(() -> new WrongTokenException("wrong token"));

        if(registrationToken.getExpirationDate().after(SQLDateBuilder.now())) {
            applicationUserService.activateUser(registrationToken.getApplicationUser().getUsername());
            registrationRepository.delete(registrationToken);
        } else {
            registrationRepository.delete(registrationToken);
            throw new RegistrationException("token expired");
        }
    }
}

