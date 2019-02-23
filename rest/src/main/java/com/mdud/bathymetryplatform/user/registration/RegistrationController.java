package com.mdud.bathymetryplatform.user.registration;

import com.mdud.bathymetryplatform.controller.ResourceIdResponse;
import com.mdud.bathymetryplatform.user.ApplicationUserDTO;
import com.mdud.bathymetryplatform.utility.configuration.AppConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/api/register")
public class RegistrationController {
    private final RegistrationService registrationService;
    private final AppConfiguration appConfiguration;
    private final MailService mailService;

    @Autowired
    public RegistrationController(RegistrationService registrationService, AppConfiguration appConfiguration, MailService mailService) {
        this.registrationService = registrationService;
        this.mailService = mailService;
        this.appConfiguration = appConfiguration;
    }

    @PostMapping
    public ResourceIdResponse registerAccount(@Valid @RequestBody ApplicationUserDTO applicationUserDTO) {
        RegistrationToken registrationToken = registrationService.registerUser(applicationUserDTO);


        return new ResourceIdResponse(registrationToken.getId(), "registration code send");
    }

    @GetMapping
    public void activateAccount(HttpServletResponse response, @RequestParam("token") String token) throws IOException {
        registrationService.activateUser(token);
        response.sendRedirect("http://" + appConfiguration.getServerIPAddress() + ":3000");
    }
}
