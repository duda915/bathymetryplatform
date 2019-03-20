package com.mdud.bathymetryplatform.user.registration;

import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender javaMailSender;
    private final IPService ipService;

    @LocalServerPort
    private int port;

    public MailService(JavaMailSender javaMailSender, IPService ipService) {
        this.javaMailSender = javaMailSender;
        this.ipService = ipService;
    }

    void sendActivationLink(RegistrationToken registrationToken) {
        System.out.println(port);
        SimpleMailMessage activationMail = new SimpleMailMessage();
        activationMail.setSubject("BPlatform account activation");
        activationMail.setTo(registrationToken.getApplicationUser().getEmail());
        activationMail.setText("Account activation link: http://" + ipService.getExternalIp() + ":" +
                port + "/api/register?token=" + registrationToken.getToken());
        javaMailSender.send(activationMail);
    }


}
