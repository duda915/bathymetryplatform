package com.mdud.bathymetryplatform.user.registration;

import com.mdud.bathymetryplatform.user.ApplicationUser;
import com.mdud.bathymetryplatform.user.ApplicationUserDTO;
import com.mdud.bathymetryplatform.user.ApplicationUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;



@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class RegistrationServiceTest {
    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private ApplicationUserService applicationUserService;

    @Test
    public void registerUser_RegisterUser_ShouldRegisterUser(){
        ApplicationUserDTO applicationUserDTO = new ApplicationUserDTO("test", "test", "test");
        RegistrationToken registrationToken = registrationService.registerUser(applicationUserDTO);

        assertFalse(registrationToken.getApplicationUser().isActive());
    }

    @Test
    public void activateUser_RegisterAndActivateUserUsingToken_ShouldActivateUser() {
        ApplicationUserDTO applicationUserDTO = new ApplicationUserDTO("test", "test", "test");
        RegistrationToken registrationToken = registrationService.registerUser(applicationUserDTO);
        registrationService.activateUser(registrationToken.getToken());

        ApplicationUser applicationUser = applicationUserService.getApplicationUser("test");

        assertTrue(applicationUser.isActive());
    }
}