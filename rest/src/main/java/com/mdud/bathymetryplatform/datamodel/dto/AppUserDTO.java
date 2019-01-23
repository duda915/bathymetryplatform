package com.mdud.bathymetryplatform.datamodel.dto;

import com.mdud.bathymetryplatform.user.ApplicationUser;
import lombok.AllArgsConstructor;
import lombok.Getter;

//for register purposes
@Getter @AllArgsConstructor
public class AppUserDTO {
    private String username;
    private String password;

    public AppUserDTO(ApplicationUser applicationUser) {
        this.username = applicationUser.getUsername();
        this.password = applicationUser.getPassword();
    }
}
