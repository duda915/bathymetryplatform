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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class BathymetryDataSetServiceTest {

    @Autowired
    private BathymetryDataSetService bathymetryDataSetService;

    @Autowired
    private ApplicationUserService applicationUserService;

    private List<BathymetryPoint> bathymetryPoints;
    private ApplicationUser writeUser;


    @Before
    public void before() {
        bathymetryPoints = new ArrayList<>();
        Arrays.asList(1,2,3,4,5).forEach(point -> {
            bathymetryPoints.add(new BathymetryPointBuilder()
            .point(point, point)
            .depth(5)
            .buildPoint());
        });

        writeUser = applicationUserService.getApplicationUser("write");
    }

    @Test
    public void getDataSetsByUser_GetEmptyUserDataSets_ShouldReturnZeroSizeList() {
        List<BathymetryDataSet> bathymetryDataSets = bathymetryDataSetService.getDataSetsByUser("write");

        assertEquals(0, bathymetryDataSets.size());
    }
}