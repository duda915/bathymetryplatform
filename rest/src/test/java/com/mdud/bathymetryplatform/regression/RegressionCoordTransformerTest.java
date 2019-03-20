package com.mdud.bathymetryplatform.regression;

import com.vividsolutions.jts.geom.Coordinate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class RegressionCoordTransformerTest {

    @Test
    public void transformTo32634() {
        Coordinate coordinate = new Coordinate(17.95, 54.02);

        coordinate = RegressionCoordTransformer.transformTo32634(coordinate);

        assertEquals(300000.0, coordinate.x, 10000);
        assertEquals(5990220.0, coordinate.y, 10000);
    }

    @Test
    public void transfromFrom32634() {
        Coordinate coordinate = new Coordinate(300000, 5990220);

        coordinate = RegressionCoordTransformer.transformFrom32634(coordinate);

        assertEquals(17.95, coordinate.x, 0.1);
        assertEquals(54.02, coordinate.y, 0.1);

    }
}