package com.mdud.bathymetryplatform.bathymetry;

import com.mdud.bathymetryplatform.bathymetry.point.BathymetryPoint;
import com.mdud.bathymetryplatform.bathymetry.point.BathymetryPointBuilder;
import com.mdud.bathymetryplatform.user.ApplicationUser;
import com.mdud.bathymetryplatform.user.ApplicationUserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class BathymetryDataSetRepositoryImplTest {

    @Autowired
    private BathymetryDataSetRepository bathymetryDataSetRepository;

    @Autowired
    private ApplicationUserService applicationUserService;

    private ApplicationUser applicationUser;

    @Before
    public void before() {
        applicationUser = applicationUserService.getApplicationUser("write");
    }

    @Test
    public void nativeSave_SaveBathymetryDataSetWithoutPoints_ShouldSaveBathymetryDataSetWithoutPoints() {
        BathymetryDataSet bathymetryDataSet = new BathymetryDataSet(applicationUser, "name", new Date(12121L), "owner", new ArrayList<>());
        bathymetryDataSet = bathymetryDataSetRepository.nativeSave(bathymetryDataSet);

        assertNotNull(bathymetryDataSet.getId());
    }

    @Test
    public void nativeSave_SaveBathymetryDataSetWithPoints_ShouldSaveBathymetryWithPoints() {
        List<BathymetryPoint> points = new ArrayList<>();
        Arrays.asList(11,12,13).forEach(number -> points.add(new BathymetryPointBuilder()
                .point(number, 10).depth(11).buildPoint())
        );
        BathymetryDataSet bathymetryDataSet = new BathymetryDataSet(applicationUser, "name", new Date(12121L), "owner", points);
        bathymetryDataSet = bathymetryDataSetRepository.nativeSave(bathymetryDataSet);
        BathymetryDataSet newData = bathymetryDataSetRepository.findById(bathymetryDataSet.getId()).get();

        boolean act = newData.getId() != null
                && newData.getMeasurements().stream().allMatch(point -> point.getBathymetryId().equals(newData.getId()));

        assertTrue(act);
    }
}