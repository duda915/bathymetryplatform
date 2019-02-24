package com.mdud.bathymetryplatform.user;

import com.mdud.bathymetryplatform.controller.StringResponse;
import com.mdud.bathymetryplatform.user.authority.Authorities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@CrossOrigin
@RestController
@RequestMapping("/api/user")
public class ApplicationUserController {

    private final ApplicationUserService applicationUserService;

    @Autowired
    public ApplicationUserController(ApplicationUserService applicationUserService) {
        this.applicationUserService = applicationUserService;
    }

    @GetMapping
    public ApplicationUser getLoggedUser(Principal principal) {
        return applicationUserService.getApplicationUser(principal.getName());
    }

    @PutMapping
    @PreAuthorize("hasAuthority('WRITE')")
    public ApplicationUser changeUserPassword(Principal principal, @Valid @RequestBody PasswordDTO passwordDTO) {
        return applicationUserService.changeUserPassword(principal.getName(), passwordDTO.getNewPassword());
    }


}
