package com.mdud.bathymetryplatform.datamodel.dto;

import com.mdud.bathymetryplatform.datamodel.BathymetryCollection;
import lombok.Getter;

import java.sql.Date;

@Getter
public class BathymetryMetaDTO {
    private Long id;
    private AppUserDTO appUserDTO;
    private String acquisitionName;
    private Date acquisitionDate;
    private String dataOwner;

    public BathymetryMetaDTO(BathymetryCollection bathymetryCollection) {
        this.id = bathymetryCollection.getId();
        this.appUserDTO = new AppUserDTO(bathymetryCollection.getAppUser());
        this.acquisitionName = bathymetryCollection.getAcquisitionName();
        this.acquisitionDate = bathymetryCollection.getAcquisitionDate();
        this.dataOwner = bathymetryCollection.getDataOwner();
    }
}
