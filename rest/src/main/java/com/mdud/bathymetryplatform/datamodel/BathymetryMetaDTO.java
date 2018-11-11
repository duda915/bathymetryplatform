package com.mdud.bathymetryplatform.datamodel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter @Setter @NoArgsConstructor
public class BathymetryMetaDTO {
    private Long id;
    private String acquisitionName;
    private Date acquisitionDate;
    private String dataOwner;
    private Integer points;

    public BathymetryMetaDTO(Long id, String acquisitionName, Date acquisitionDate, String dataOwner, Integer points) {
        this.id = id;
        this.acquisitionName = acquisitionName;
        this.acquisitionDate = acquisitionDate;
        this.dataOwner = dataOwner;
        this.points = points;
    }

    public BathymetryMetaDTO(BathymetryCollection bathymetryCollection) {
        this.id = bathymetryCollection.getId();
        this.acquisitionName = bathymetryCollection.getAcquisitionName();
        this.acquisitionDate = bathymetryCollection.getAcquisitionDate();
        this.dataOwner = bathymetryCollection.getDataOwner();
        this.points = bathymetryCollection.getMeasureList().size();
    }
}
