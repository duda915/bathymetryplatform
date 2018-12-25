package com.mdud.bathymetryplatform.datamodel.dto;

import com.vividsolutions.jts.geom.Point;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class BathymetryMeasureDTO {
    private Point measureCoords;
    private Double measure;
}
