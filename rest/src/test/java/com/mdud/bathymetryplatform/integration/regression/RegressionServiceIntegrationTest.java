package com.mdud.bathymetryplatform.integration.regression;

import com.mdud.bathymetryplatform.bathymetry.point.BathymetryPoint;
import com.mdud.bathymetryplatform.bathymetry.polygonselector.BoxRectangle;
import com.mdud.bathymetryplatform.regression.RegressionException;
import com.mdud.bathymetryplatform.regression.RegressionService;
import com.mdud.bathymetryplatform.utility.configuration.AppConfigurationImpl;
import com.vividsolutions.jts.geom.Coordinate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfigurationImpl.class, RegressionService.class})
@TestPropertySource("classpath:application.properties")
public class RegressionServiceIntegrationTest {

    @Autowired
    private RegressionService regressionService;

    @Test
    public void boundsInit() {
        System.out.println(regressionService.getBounds());
        Assert.assertEquals(17.95, regressionService.getBounds().getUpperLeftVertex().x, 0.15);
        Assert.assertEquals(55.03, regressionService.getBounds().getUpperLeftVertex().y, 0.15);
        Assert.assertEquals(19.58, regressionService.getBounds().getLowerRightVertex().x, 0.15);
        Assert.assertEquals(54.02, regressionService.getBounds().getLowerRightVertex().y, 0.15);
    }

    @Test
    public void getResults() {
        BoxRectangle boxRectangle = new BoxRectangle(new Coordinate(18.1, 54.9), new Coordinate(19.4,54.2));
        List<BathymetryPoint> list = regressionService.getResults(boxRectangle);

        list.forEach(System.out::println);
        Assert.assertFalse(list.size() == 0);
    }

    @Test(expected = RegressionException.class)
    public void getResults_OutOfBounds_ShouldThrowException() {
        BoxRectangle boxRectangle = new BoxRectangle(new Coordinate(12.0, 55.0), new Coordinate(19.5,54.2));
        regressionService.getResults(boxRectangle);
    }

}
