package com.mdud.bathymetryplatform.user.registration;

import com.mdud.bathymetryplatform.controller.ResourceIdResponse;
import com.mdud.bathymetryplatform.user.ApplicationUserDTO;
import com.mdud.bathymetryplatform.utility.configuration.AppConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/api/register")
public class RegistrationController {
    private final RegistrationService registrationService;
    private final JavaMailSender javaMailSender;
    private final AppConfiguration appConfiguration;

    @Autowired
    public RegistrationController(RegistrationService registrationService, JavaMailSender javaMailSender, AppConfiguration appConfiguration) {
        this.registrationService = registrationService;
        this.javaMailSender = javaMailSender;
        this.appConfiguration = appConfiguration;
    }

    @PostMapping
    public ResourceIdResponse registerAccount(@Valid @RequestBody ApplicationUserDTO applicationUserDTO) {
        RegistrationToken registrationToken = registrationService.registerUser(applicationUserDTO);

        SimpleMailMessage activationMail = new SimpleMailMessage();
        activationMail.setSubject("BPlatform account activation");
        activationMail.setTo(registrationToken.getApplicationUser().getEmail());
        activationMail.setText("Account activation link: http://" + appConfiguration.getServerIPAddress() + ":" +
                appConfiguration.getServerPort() + "/api/register?token=" + registrationToken.getToken());
        javaMailSender.send(activationMail);

        return new ResourceIdResponse(registrationToken.getId(), "registration code send");
    }

    @GetMapping
    public void activateAccount(HttpServletResponse response, @RequestParam("token") String token) throws IOException {
        registrationService.activateUser(token);
        response.sendRedirect("http://" + appConfiguration.getServerIPAddress() + ":3000");
    }
}
