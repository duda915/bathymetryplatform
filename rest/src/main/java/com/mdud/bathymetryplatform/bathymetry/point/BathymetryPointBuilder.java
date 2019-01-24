package com.mdud.bathymetryplatform.bathymetry.point;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

public class BathymetryPointBuilder {
    private GeometryFactory geometryFactory;
    private Point point;
    private Double depth;

    public BathymetryPointBuilder() {
        geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    }

    public BathymetryPointBuilder point(float x, float y) {
        point = geometryFactory.createPoint(new Coordinate(x, y));
        return this;
    }

    public BathymetryPointBuilder depth(double depth) {
        this.depth = depth;
        return this;
    }

    public BathymetryPoint buildPoint() {
        return new BathymetryPoint(point, depth);
    }
}
