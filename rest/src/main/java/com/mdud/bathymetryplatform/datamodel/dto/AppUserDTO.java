package com.mdud.bathymetryplatform.datamodel.dto;

import com.mdud.bathymetryplatform.datamodel.AppUser;
import lombok.AllArgsConstructor;
import lombok.Getter;

//for register purposes
@Getter @AllArgsConstructor
public class AppUserDTO {
    private String username;
    private String password;

    public AppUserDTO(AppUser appUser) {
        this.username = appUser.getUsername();
        this.password = appUser.getPassword();
    }
}
