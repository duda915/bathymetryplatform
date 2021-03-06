package com.mdud.bathymetryplatform.user.registration;

import com.mdud.bathymetryplatform.user.ApplicationUser;
import com.mdud.bathymetryplatform.utility.configuration.AppConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class MailServiceTest {

    @InjectMocks
    private MailService mailService;

    @Mock
    private JavaMailSender javaMailSender;

    @Spy
    private IPService ipService = new IPService();

    @Mock
    private AppConfiguration appConfiguration;

    @Test
    public void sendActivationLink_ShouldSendMail() {
        ApplicationUser applicationUser = new ApplicationUser();
        RegistrationToken registrationToken = new RegistrationToken(applicationUser);

        mailService.sendActivationLink(registrationToken);
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }


}