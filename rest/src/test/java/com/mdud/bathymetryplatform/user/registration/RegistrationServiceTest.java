package com.mdud.bathymetryplatform.user.registration;

import com.mdud.bathymetryplatform.user.ApplicationUser;
import com.mdud.bathymetryplatform.user.ApplicationUserDTO;
import com.mdud.bathymetryplatform.user.ApplicationUserService;
import com.mdud.bathymetryplatform.utility.SQLDateBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RegistrationServiceTest {

    @InjectMocks
    private RegistrationService registrationService;

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private MailService mailService;

    @Mock
    private ApplicationUserService applicationUserService;

    @Test
    public void registerUser_AddUserGenerateTokenAndSendMail() {
        ApplicationUserDTO applicationUserDTO = new ApplicationUserDTO("user", "user", "user");
        ApplicationUser applicationUser = new ApplicationUser(applicationUserDTO);
        RegistrationToken registrationToken = new RegistrationToken(applicationUser);

        when(registrationRepository.save(any(RegistrationToken.class))).thenReturn(registrationToken);

        RegistrationToken newToken = registrationService.registerUser(applicationUserDTO);

        assertEquals(registrationToken, newToken);

        verify(registrationRepository, times(1)).save(any(RegistrationToken.class));
        verify(mailService, times(1)).sendActivationLink(registrationToken);
        verify(applicationUserService, times(1)).addNewUser(applicationUserDTO);
    }

    @Test
    public void activateUser_ValidToken_ShouldActivateUser() {
        RegistrationToken registrationToken = new RegistrationToken(new ApplicationUser());
        when(registrationRepository.findByToken("token")).thenReturn(Optional.of(registrationToken));

        registrationService.activateUser("token");

        verify(applicationUserService, times(1)).activateUser(null);
        verify(registrationRepository, times(1)).delete(registrationToken);
    }

    @Test(expected = WrongTokenException.class)
    public void activateUser_InvalidToken_ShouldThrowException() {
        registrationService.activateUser("token");
    }

    @Test(expected = RegistrationException.class)
    public void activateUser_ExpiredToken_ShouldThrowException() {
        RegistrationToken registrationToken = new RegistrationToken(new ApplicationUser());
        registrationToken.setExpirationDate(SQLDateBuilder.now());
        when(registrationRepository.findByToken("token")).thenReturn(Optional.of(registrationToken));

        registrationService.activateUser("token");
    }
}