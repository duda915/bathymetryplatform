package com.mdud.bathymetryplatform.bathymetry.parser;

import com.mdud.bathymetryplatform.bathymetry.point.BathymetryPoint;
import com.mdud.bathymetryplatform.bathymetry.point.BathymetryPointBuilder;
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
import java.util.*;

public class BathymetryDataParser {
    private final Logger logger = LoggerFactory.getLogger(BathymetryDataParser.class);

    private int sourceEPSG;
    private CoordinateReferenceSystem sourceCRS;
    private CoordinateReferenceSystem targetCRS;
    private MathTransform transform;
    private GeometryFactory geometryFactory;

    public BathymetryDataParser(int sourceEPSG) {
        this.sourceEPSG = sourceEPSG;
        initParser();
    }

    private void initParser() {
        try {
            sourceCRS = CRS.decode("EPSG:" + sourceEPSG);
            targetCRS = CRS.decode("EPSG:4326");
            transform = CRS.findMathTransform(sourceCRS, targetCRS);
            geometryFactory = new GeometryFactory(new PrecisionModel(), sourceEPSG);
        } catch (FactoryException e) {
            throw new DataParsingException("wrong epsg code");
        }
    }

    BathymetryPoint parsePoint(String lineData) {
        ParsedPoint parsedPoint = parseLine(lineData).orElseThrow(() -> new DataParsingException("line contains invalid point"));

        ParsedPoint transformedPoint = transformCoordinates(parsedPoint);

        return new BathymetryPointBuilder()
                .point((float) transformedPoint.x, (float) transformedPoint.y)
                .depth(transformedPoint.depth)
                .buildPoint();
    }

    private Optional<ParsedPoint> parseLine(String lineData) {
        String[] elements = lineData.replaceAll("\\s+", " ").split(" ");

        List<String> elementsList = new ArrayList<>(Arrays.asList(elements));
        elementsList.removeAll(Collections.singletonList(""));

        ParsedPoint parsedPoint = null;

        try {
            double x = Double.valueOf(elementsList.get(0));
            double y = Double.valueOf(elementsList.get(1));
            double depth = Double.valueOf(elementsList.get(2));

            parsedPoint = new ParsedPoint(x, y, depth);
        } catch (Exception ignored) {
        }

        return Optional.ofNullable(parsedPoint);
    }

    private ParsedPoint transformCoordinates(ParsedPoint parsedPoint) {
        Geometry geometry = geometryFactory.createPoint(new Coordinate(parsedPoint.x, parsedPoint.y));
        Point point;

        try {
            point = (Point) JTS.transform(geometry, transform);
        } catch (TransformException e) {
            e.printStackTrace();
            throw new DataParsingException("failed to transform coordinates");
        }

        if (sourceEPSG != 4326) {
            return new ParsedPoint(point.getY(), point.getX(), parsedPoint.depth);
        } else {
            return new ParsedPoint(point.getX(), point.getY(), parsedPoint.depth);
        }
    }

    public List<BathymetryPoint> parseFile(byte[] bytes) {
        String[] l = new String(bytes, StandardCharsets.UTF_8).split("\n");
        ArrayList<String> lines = new ArrayList<>(Arrays.asList(l));

        List<BathymetryPoint> measures = new ArrayList<>();

        try {
            BathymetryPoint headerCheck = parsePoint(lines.remove(0));
            measures.add(headerCheck);
        } catch (DataParsingException ignored) {
        }

        lines.forEach(line -> {
            BathymetryPoint measure = parsePoint(line);
            measures.add(measure);
        });

        validatePoints(measures);

        return measures;

    }

    private void validatePoints(List<BathymetryPoint> measures) {
        if(measures.size() == 0) {
            throw new DataParsingException("failed to read file");
        }
    }

    private class ParsedPoint {
        double x;
        double y;
        double depth;

        ParsedPoint(double x, double y, double depth) {
            this.x = x;
            this.y = y;
            this.depth = depth;
        }
    }

}

