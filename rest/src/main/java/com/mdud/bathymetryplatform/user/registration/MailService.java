package com.mdud.bathymetryplatform.user.registration;

import com.mdud.bathymetryplatform.utility.configuration.AppConfiguration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender javaMailSender;
    private final AppConfiguration appConfiguration;

    public MailService(JavaMailSender javaMailSender, AppConfiguration appConfiguration) {
        this.javaMailSender = javaMailSender;
        this.appConfiguration = appConfiguration;
    }

    void sendActivationLink(RegistrationToken registrationToken) {
        SimpleMailMessage activationMail = new SimpleMailMessage();
        activationMail.setSubject("BPlatform account activation");
        activationMail.setTo(registrationToken.getApplicationUser().getEmail());
        activationMail.setText("Account activation link: http://" + appConfiguration.getServerIPAddress() + ":" +
                appConfiguration.getServerPort() + "/api/register?token=" + registrationToken.getToken());
        javaMailSender.send(activationMail);
    }
}
