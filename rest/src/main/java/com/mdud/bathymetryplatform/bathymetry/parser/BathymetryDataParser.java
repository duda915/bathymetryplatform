package com.mdud.bathymetryplatform.bathymetry.parser;

import com.mdud.bathymetryplatform.bathymetry.point.BathymetryPoint;
import com.mdud.bathymetryplatform.bathymetry.point.BathymetryPointDTO;
import com.mdud.bathymetryplatform.exception.DataParsingException;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.*;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BathymetryDataParser {
    private final Logger logger = LoggerFactory.getLogger(BathymetryDataParser.class);

    private int sourceEPSG;
    private CoordinateReferenceSystem sourceCRS;
    private CoordinateReferenceSystem targetCRS;
    private MathTransform transform;
    private GeometryFactory geometryFactory;
    private com.vividsolutions.jts.geom.GeometryFactory targetGeometryFactory;

    public BathymetryDataParser(int sourceEPSG)  {
        this.sourceEPSG = sourceEPSG;
        initParser();
    }

    private void initParser()
    {
        try {
            sourceCRS = CRS.decode("EPSG:" + sourceEPSG);
            targetCRS = CRS.decode("EPSG:4326");
            transform = CRS.findMathTransform(sourceCRS, targetCRS);
            geometryFactory = new GeometryFactory(new PrecisionModel(), sourceEPSG);
            targetGeometryFactory =
                    new com.vividsolutions.jts.geom.GeometryFactory(new com.vividsolutions.jts.geom.PrecisionModel(), 4326);
        } catch (FactoryException e) {
            throw new DataParsingException("wrong epsg code");
        }
    }

    public BathymetryPointDTO parsePoint(String lineData) {
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
        Point point = null;
        try {
            point = (Point) JTS.transform(geometry, transform);
        } catch (TransformException e) {
            e.printStackTrace();
        }

        com.vividsolutions.jts.geom.Coordinate pointCoords;
        if(sourceEPSG == 4326) {
            pointCoords = new com.vividsolutions.jts.geom.Coordinate(point.getX(), point.getY());
        } else {
            pointCoords = new com.vividsolutions.jts.geom.Coordinate(point.getY(), point.getX());
        }
        com.vividsolutions.jts.geom.Point targetPoint =
                targetGeometryFactory.createPoint(pointCoords);
        Double depth = Double.valueOf(elementsList.get(2));

        return new BathymetryPointDTO(targetPoint, depth);
    }

    public List<BathymetryPoint> parseFile(MultipartFile file) {
        try {
            return parseFile(file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            throw new DataParsingException("cannot read file");
        }
    }

    public List<BathymetryPoint> parseFile(byte[] bytes) {
        String[] lines = new String(bytes, StandardCharsets.UTF_8).split("\n");
        List<BathymetryPoint> measures = new ArrayList<>();

        try {
            BathymetryPointDTO headerCheck = parsePoint(lines[0]);
            measures.add(new BathymetryPoint(headerCheck));
        } catch (NumberFormatException ignored) {}

        try {
            for(int i = 1; i < lines.length; i++) {
                BathymetryPointDTO measureDTO = parsePoint(lines[i]);
                if(measureDTO == null)
                    continue;
                measures.add(new BathymetryPoint(measureDTO));
            }

            return measures;
        } catch (NumberFormatException e) {
            throw new DataParsingException("failed to read file");
        }

    }
}
