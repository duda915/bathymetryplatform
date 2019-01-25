package com.mdud.bathymetryplatform.bathymetry;

import com.mdud.bathymetryplatform.user.ApplicationUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;

@Data
@AllArgsConstructor
public class BathymetryDataSetDTO {
    private ApplicationUser applicationUser;
    private Integer epsgCode;
    private String name;
    private Date measurementDate;
    private String dataOwner;
}
