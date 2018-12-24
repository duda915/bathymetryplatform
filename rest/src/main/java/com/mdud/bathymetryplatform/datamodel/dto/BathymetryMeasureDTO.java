package com.mdud.bathymetryplatform.datamodel.dto;

import com.vividsolutions.jts.geom.Point;
import lombok.Getter;

@Getter
public class BathymetryMeasureDTO {
    private Point measureCoords;
    private Double measure;

    public BathymetryMeasureDTO(Point measureCoords, Double measure) {
        this.measureCoords = measureCoords;
        this.measure = measure;
    }
}
