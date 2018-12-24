package com.mdud.bathymetryplatform.datamodel.dto;

import com.mdud.bathymetryplatform.datamodel.AppUser;
import lombok.Getter;

//for register purposes
@Getter
public class AppUserDTO {
    private String username;
    private String password;

    public AppUserDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public AppUserDTO(AppUser appUser) {
        this.username = appUser.getUsername();
        this.password = appUser.getPassword();
    }
}
