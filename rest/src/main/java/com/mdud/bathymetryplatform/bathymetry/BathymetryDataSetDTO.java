package com.mdud.bathymetryplatform.bathymetry;

import com.mdud.bathymetryplatform.user.ApplicationUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Date;

@Data
@AllArgsConstructor
public class BathymetryDataSetDTO {
    private ApplicationUser applicationUser;

    @NotNull
    private Integer epsgCode;
    @NotNull
    @NotEmpty
    private String name;
    @NotNull
    private Date measurementDate;
    @NotEmpty
    @NotNull
    private String dataOwner;
}
