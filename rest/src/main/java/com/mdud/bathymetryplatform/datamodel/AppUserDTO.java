package com.mdud.bathymetryplatform.datamodel;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class AppUserDTO {
    private String username;
    private String password;

    public AppUserDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
