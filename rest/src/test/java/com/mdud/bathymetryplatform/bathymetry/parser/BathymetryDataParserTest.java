package com.mdud.bathymetryplatform.bathymetry.parser;

import com.mdud.bathymetryplatform.bathymetry.point.BathymetryPoint;
import com.mdud.bathymetryplatform.bathymetry.point.BathymetryPointBuilder;
import com.mdud.bathymetryplatform.exception.DataParsingException;
import org.junit.Test;

import java.util.List;

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

    @Test(expected = DataParsingException.class)
    public void parsePoint_EmptyPoint_ShouldThrowException() {
        BathymetryDataParser bathymetryDataParser = new BathymetryDataParser(4326);
        bathymetryDataParser.parsePoint("");
    }

    @Test(expected = DataParsingException.class)
    public void parsePoint_PointWithoutDepth_ShouldThrowException() {
        BathymetryDataParser bathymetryDataParser = new BathymetryDataParser(4326);
        bathymetryDataParser.parsePoint("12 12");
    }

    @Test(expected = DataParsingException.class)
    public void parsePoint_String_ShouldThrowException() {
        BathymetryDataParser bathymetryDataParser = new BathymetryDataParser(4326);
        bathymetryDataParser.parsePoint("test test test");
    }

    @Test(expected = DataParsingException.class)
    public void parsePoint_InvalidEPSGCode_ShouldThrowException() {
        BathymetryDataParser bathymetryDataParser = new BathymetryDataParser(12121212);
        bathymetryDataParser.parsePoint(" 367030.05     6025361.85   1.2 test");
    }

    @Test
    public void parseFile_ParseFileWithoutHeader() {
        BathymetryDataParser bathymetryDataParser = new BathymetryDataParser(32634);
        List<BathymetryPoint> bathymetryPoints = bathymetryDataParser.parseFile(" 367030.05     6025361.85   1.2 test\n367030.05     6025361.85   1.2 test".getBytes());

        assertEquals(2, bathymetryPoints.size());
    }

    @Test
    public void parseFile_ParseFileWithHeader() {
        BathymetryDataParser bathymetryDataParser = new BathymetryDataParser(32634);
        List<BathymetryPoint> bathymetryPoints = bathymetryDataParser.parseFile("header\n 367030.05     6025361.85   1.2 test".getBytes());

        assertEquals(1, bathymetryPoints.size());
    }

    @Test(expected = DataParsingException.class)
    public void parseFile_ParseEmptyFile_ShouldThrowException() {
        BathymetryDataParser bathymetryDataParser = new BathymetryDataParser(32634);
        bathymetryDataParser.parseFile("".getBytes());
    }

    @Test(expected = DataParsingException.class)
    public void parseFile_ParseInvalidFile() {
        BathymetryDataParser bathymetryDataParser = new BathymetryDataParser(32634);
        bathymetryDataParser.parseFile("12 12 12 \n test 12 12 \n 12 12 12".getBytes());
    }

}