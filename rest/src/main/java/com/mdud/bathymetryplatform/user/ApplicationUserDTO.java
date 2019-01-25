package com.mdud.bathymetryplatform.user;

import lombok.Data;

@Data
public class ApplicationUserDTO {
    private String username;
    private String password;


    public ApplicationUserDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
