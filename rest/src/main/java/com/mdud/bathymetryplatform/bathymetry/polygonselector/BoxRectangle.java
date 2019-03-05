package com.mdud.bathymetryplatform.bathymetry.polygonselector;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class BoxRectangle implements GeometryAdapter<BoxRectangle>{
    private Coordinate upperLeftVertex;
    private Coordinate lowerRightVertex;

    public BoxRectangle(Coordinate upperLeftVertex, Coordinate lowerRightVertex) {
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
    public Geometry buildGeometry(BoxRectangle object) {
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
