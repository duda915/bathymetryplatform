package com.mdud.bathymetryplatform.user.registration;

import com.mdud.bathymetryplatform.controller.ResourceIdResponse;
import com.mdud.bathymetryplatform.user.ApplicationUserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/register")
public class RegistrationController {
    private final RegistrationService registrationService;

    @Autowired
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    public ResourceIdResponse registerAccount(ApplicationUserDTO applicationUserDTO) {

        return new ResourceIdResponse(1L, "ss");
    }
}
