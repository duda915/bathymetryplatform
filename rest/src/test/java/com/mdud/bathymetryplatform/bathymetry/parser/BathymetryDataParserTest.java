package com.mdud.bathymetryplatform.bathymetry.parser;

import com.mdud.bathymetryplatform.bathymetry.point.BathymetryPoint;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BathymetryDataParserTest {

    @Test
    public void parsePoint_32634() {
        BathymetryDataParser bathymetryDataParser = new BathymetryDataParser(32634);
        BathymetryPoint bathymetryPoint = bathymetryDataParser.parsePoint(" 367030.05     6025361.85   1.2 test");

        double parsedX = bathymetryPoint.getMeasurementCoordinates().getX();
        double parsedY = bathymetryPoint.getMeasurementCoordinates().getY();

        double expectedX = 18.9537;
        double expectedY = 54.3587;

        assertEquals(expectedX, parsedX, 0.0001);
        assertEquals(expectedY, parsedY, 0.0001);
        assertEquals(1.2, bathymetryPoint.getDepth(), 0.001);
    }

    @Test
    public void parsePoint_4326() {
        BathymetryDataParser bathymetryDataParser = new BathymetryDataParser(4326);
        BathymetryPoint bathymetryPoint = bathymetryDataParser.parsePoint("18.9537 54.3587 1.2");

        double parsedX = bathymetryPoint.getMeasurementCoordinates().getX();
        double parsedY = bathymetryPoint.getMeasurementCoordinates().getY();

        double expectedX = 18.9537;
        double expectedY = 54.3587;

        assertEquals(expectedX, parsedX, 0.0001);
        assertEquals(expectedY, parsedY, 0.0001);
    }
}