package com.mdud.bathymetryplatform.regression;

import com.vividsolutions.jts.geom.Coordinate;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;


public class RegressionCoordTransformer {

    public static Coordinate transformTo32634(Coordinate coordinate) {
        try {
            CoordinateReferenceSystem source = CRS.decode("EPSG:4326");
            CoordinateReferenceSystem target = CRS.decode("EPSG:32634");
            MathTransform transform = CRS.findMathTransform(source, target);
            GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

            flipCoordinate(coordinate);
            return transformCoordinate(coordinate, transform, geometryFactory);
        } catch (FactoryException | TransformException e) {
            e.printStackTrace();
            throw new RegressionException("failed to transform coords");
        }
    }

    public static Coordinate transformFrom32634(Coordinate coordinate) {
        try {
            CoordinateReferenceSystem source = CRS.decode("EPSG:32634");
            CoordinateReferenceSystem target = CRS.decode("EPSG:4326");
            MathTransform transform = CRS.findMathTransform(source, target);
            GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 32634);

            Coordinate transformed = transformCoordinate(coordinate, transform, geometryFactory);
            flipCoordinate(transformed);
            return transformed;
        } catch (FactoryException | TransformException e) {
            e.printStackTrace();
            throw new RegressionException("failed to transform coords");
        }

    }

    private static void flipCoordinate(Coordinate coordinate) {
        double x = coordinate.x;
        coordinate.x = coordinate.y;
        coordinate.y = x;
    }

    private static Coordinate transformCoordinate(Coordinate coordinate, MathTransform transform, GeometryFactory geometryFactory) throws TransformException {
        org.locationtech.jts.geom.Coordinate locationCoord = new org.locationtech.jts.geom.Coordinate(coordinate.x, coordinate.y);
        Geometry geometry = geometryFactory.createPoint(locationCoord);
        Point point = (Point) JTS.transform(geometry, transform);

        return new Coordinate(point.getX(), point.getY());
    }
}
