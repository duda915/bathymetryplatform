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
    public ApplicationUser changeUserPassword(Principal principal, @Valid @RequestBody PasswordDTO passwordDTO) {
        return applicationUserService.changeUserPassword(principal.getName(), passwordDTO.getNewPassword());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ApplicationUser addUserWithoutRegistration(@RequestBody ApplicationUserDTO applicationUserDTO) {
        return applicationUserService.addNewUser(applicationUserDTO.getUsername(), applicationUserDTO.getPassword(), "");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping
    public StringResponse deleteUser(Principal principal, @RequestBody String username) {
        if(principal.getName().equals(username)) {
            throw new AccessDeniedException("Removing yourself is not allowed");
        }

        applicationUserService.removeUser(username);
        return new StringResponse("User removed");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/authority")
    public StringResponse addAuthorityToUser(@RequestParam(name = "username") String username, @RequestBody Authorities authority) {
        applicationUserService.addNewAuthority(username, authority);

        return new StringResponse("Authority added");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/authority")
    public StringResponse removeUserAuthority(@RequestParam String username, @RequestBody Authorities authority) {
        applicationUserService.removeUserAuthority(username, authority);

        return new StringResponse("Authority removed");
    }





}
