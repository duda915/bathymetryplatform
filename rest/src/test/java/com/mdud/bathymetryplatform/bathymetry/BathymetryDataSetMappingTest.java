package com.mdud.bathymetryplatform.bathymetry;

import com.mdud.bathymetryplatform.user.ApplicationUser;
import com.mdud.bathymetryplatform.user.ApplicationUserRepository;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class BathymetryDataSetMappingTest {
    @Autowired
    private BathymetryDataSetRepository bathymetryDataSetRepository;
    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Test
    public void save_SaveDataSetWithoutPoints_ShouldSaveDataSet() {
        ApplicationUser applicationUser = applicationUserRepository.findByUsername("read").orElse(null);
        BathymetryDataSet bathymetryDataSet = new BathymetryDataSet(applicationUser, "test", new java.sql.Date(new Date().getTime()), "owner", new ArrayList<>());
        bathymetryDataSet = bathymetryDataSetRepository.save(bathymetryDataSet);

        Assert.assertNotNull(bathymetryDataSet.getId());
    }

    @Test
    public void save_SaveDataSetWithPoint_ShouldSaveDataSetWithPoint() {
        ApplicationUser applicationUser = applicationUserRepository.findByUsername("read").orElse(null);
        List<BathymetryPoint> bathymetryPoints = new ArrayList<>();
        bathymetryPoints.add(new BathymetryPoint(new GeometryFactory(new PrecisionModel(), 4326).createPoint(new Coordinate(1,1))));
        BathymetryDataSet bathymetryDataSet = new BathymetryDataSet(applicationUser, "test", new java.sql.Date(new Date().getTime()), "owner", new ArrayList<>());
        bathymetryDataSet = bathymetryDataSetRepository.save(bathymetryDataSet);

        boolean act = bathymetryDataSet.getId() != null &&
                bathymetryDataSet.getApplicationUser() != null &&
                bathymetryDataSet.getMeasurements().stream().allMatch(bathymetryPoint -> bathymetryPoint.getGid() != null);

        Assert.assertTrue(act);
    }
}
