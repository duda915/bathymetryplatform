package com.mdud.bathymetryplatform.bathymetry.point;

import com.vividsolutions.jts.geom.Point;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class BathymetryPointDTO {
    private Point measureCoords;
    private Double measure;
}
