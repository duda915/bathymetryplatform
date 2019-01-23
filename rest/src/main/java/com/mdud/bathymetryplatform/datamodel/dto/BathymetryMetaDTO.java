package com.mdud.bathymetryplatform.datamodel.dto;

import com.mdud.bathymetryplatform.bathymetry.BathymetryDataSet;
import lombok.Getter;

import java.sql.Date;

@Getter
public class BathymetryMetaDTO {
    private Long id;
    private AppUserDTO appUserDTO;
    private String acquisitionName;
    private Date acquisitionDate;
    private String dataOwner;

    public BathymetryMetaDTO(BathymetryDataSet bathymetryDataSet) {
        this.id = bathymetryDataSet.getId();
        this.appUserDTO = new AppUserDTO(bathymetryDataSet.getApplicationUser());
        this.acquisitionName = bathymetryDataSet.getName();
        this.acquisitionDate = bathymetryDataSet.getMeasurementDate();
        this.dataOwner = bathymetryDataSet.getDataOwner();
    }
}
