package com.mdud.bathymetryplatform.bathymetry;

import com.mdud.bathymetryplatform.datamodel.dto.BathymetryMeasureDTO;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.*;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BathymetryDataParser {
    private int sourceEPSG;
    private CoordinateReferenceSystem sourceCRS;
    private CoordinateReferenceSystem targetCRS;
    private MathTransform transform;
    private GeometryFactory geometryFactory;
    private com.vividsolutions.jts.geom.GeometryFactory targetGeometryFactory;

    public BathymetryDataParser(int sourceEPSG) throws FactoryException {
        this.sourceEPSG = sourceEPSG;
        initParser();
    }

    private void initParser() throws FactoryException
    {
        sourceCRS = CRS.decode("EPSG:" + sourceEPSG);
        targetCRS = CRS.decode("EPSG:4326");
        transform = CRS.findMathTransform(sourceCRS, targetCRS);
        geometryFactory = new GeometryFactory(new PrecisionModel(), sourceEPSG);
        targetGeometryFactory =
                new com.vividsolutions.jts.geom.GeometryFactory(new com.vividsolutions.jts.geom.PrecisionModel(), 4326);
    }

    public BathymetryMeasureDTO parsePoint(String lineData) throws TransformException, NumberFormatException {
        String elements[] = lineData.replaceAll("\\s+", " ").split(" ");

        List<String> elementsList = new ArrayList<>(Arrays.asList(elements));
        elementsList.removeAll(Arrays.asList(""));

        if(elementsList.size() == 0) {
            return null;
        }

        //0 - x, 1 - y 2 - z
        Double x = Double.valueOf(elementsList.get(0));
        Double y = Double.valueOf(elementsList.get(1));

        Geometry geometry = geometryFactory.createPoint(new Coordinate(x, y));
        Point point = (Point) JTS.transform(geometry, transform);

        com.vividsolutions.jts.geom.Coordinate pointCoords;
        if(sourceEPSG == 4326) {
            pointCoords = new com.vividsolutions.jts.geom.Coordinate(point.getX(), point.getY());
        } else {
            pointCoords = new com.vividsolutions.jts.geom.Coordinate(point.getY(), point.getX());
        }
        com.vividsolutions.jts.geom.Point targetPoint =
                targetGeometryFactory.createPoint(pointCoords);
        Double depth = Double.valueOf(elementsList.get(2));

        return new BathymetryMeasureDTO(targetPoint, depth);
    }
}
