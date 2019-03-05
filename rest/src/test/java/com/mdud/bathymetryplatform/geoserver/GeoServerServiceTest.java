package com.mdud.bathymetryplatform.geoserver;

import com.mdud.bathymetryplatform.bathymetry.BathymetryDataSetService;
import com.mdud.bathymetryplatform.bathymetry.polygonselector.BoxRectangle;
import com.mdud.bathymetryplatform.utility.configuration.AppConfiguration;
import com.vividsolutions.jts.geom.Coordinate;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
public class GeoServerServiceTest {
    @InjectMocks
    private GeoServerService geoServerService;

    @Mock
    private BathymetryDataSetService bathymetryDataSetService;

    @Mock
    private AppConfiguration appConfiguration;

    @Test
    public void buildBoxFromBoxes() {
        List<BoxRectangle> boxes = Arrays.asList(new BoxRectangle(new Coordinate(18.93, 54.38),
                        new Coordinate(18.96, 54.35)),
                new BoxRectangle(new Coordinate(18.28, 54.84),
                        new Coordinate(18.30, 54.83)));

        BoxRectangle boxRectangle = geoServerService.buildBoxFromBoxes(boxes);

        BoxRectangle expectedBox = new BoxRectangle(new Coordinate(18.28, 54.84), new Coordinate(18.96, 54.35));

        Assert.assertThat(boxRectangle, CoreMatchers.is(expectedBox));


    }
}