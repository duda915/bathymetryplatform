package com.mdud.bathymetryplatform.bathymetry.polygonselector;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

public class SimpleRectangle implements GeometryAdapter<SimpleRectangle>{
    private Coordinate upperLeftVertex;
    private Coordinate lowerRightVertex;

    public SimpleRectangle(Coordinate upperLeftVertex, Coordinate lowerRightVertex) {
        this.upperLeftVertex = upperLeftVertex;
        this.lowerRightVertex = lowerRightVertex;
    }

    public Coordinate getUpperLeftVertex() {
        return upperLeftVertex;
    }

    public Coordinate getLowerRightVertex() {
        return lowerRightVertex;
    }

    @Override
    public Geometry buildGeometry(SimpleRectangle object) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        return geometryFactory.createPolygon(new Coordinate[]{
                new Coordinate(upperLeftVertex.x, upperLeftVertex.y),
                new Coordinate(upperLeftVertex.x, lowerRightVertex.y),
                new Coordinate(lowerRightVertex.x, lowerRightVertex.y),
                new Coordinate(lowerRightVertex.x, upperLeftVertex.y),
                new Coordinate(upperLeftVertex.x, upperLeftVertex.y)
        });
    }
}
