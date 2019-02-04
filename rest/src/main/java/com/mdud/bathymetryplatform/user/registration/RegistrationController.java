package com.mdud.bathymetryplatform.user.registration;

import com.mdud.bathymetryplatform.controller.ResourceIdResponse;
import com.mdud.bathymetryplatform.controller.StringResponse;
import com.mdud.bathymetryplatform.user.ApplicationUserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/register")
public class RegistrationController {
    private final RegistrationService registrationService;

    @Autowired
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    public ResourceIdResponse registerAccount(@Valid @RequestBody ApplicationUserDTO applicationUserDTO) {
        RegistrationToken registrationToken = registrationService.registerUser(applicationUserDTO);

        return new ResourceIdResponse(registrationToken.getId(), "registration code send");
    }

    @GetMapping
    public StringResponse activateAccount(@RequestParam("token") String token) {
        registrationService.activateUser(token);
        return new StringResponse("account activated");
    }
}
