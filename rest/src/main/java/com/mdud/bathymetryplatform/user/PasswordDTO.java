package com.mdud.bathymetryplatform.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PasswordDTO {
    @NotNull
    @NotEmpty
    private String newPassword;
}
