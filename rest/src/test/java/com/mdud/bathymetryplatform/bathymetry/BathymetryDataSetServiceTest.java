package com.mdud.bathymetryplatform.bathymetry;

import com.mdud.bathymetryplatform.bathymetry.point.BathymetryPoint;
import com.mdud.bathymetryplatform.bathymetry.point.BathymetryPointBuilder;
import com.mdud.bathymetryplatform.bathymetry.point.BathymetryPointRepository;
import com.mdud.bathymetryplatform.bathymetry.polygonselector.SimpleRectangle;
import com.mdud.bathymetryplatform.exception.AccessDeniedException;
import com.mdud.bathymetryplatform.exception.ResourceNotFoundException;
import com.mdud.bathymetryplatform.user.ApplicationUser;
import com.mdud.bathymetryplatform.user.ApplicationUserService;
import com.mdud.bathymetryplatform.utility.SQLDateBuilder;
import com.vividsolutions.jts.geom.Coordinate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class BathymetryDataSetServiceTest {

    @Autowired
    private BathymetryDataSetService bathymetryDataSetService;

    @Autowired
    private BathymetryPointRepository bathymetryPointRepository;

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

    @Test
    public void getDataSetsByUser_GetUserDataSets_ShouldReturnDataSetsList() {
        BathymetryDataSet bathymetryDataSet = new BathymetryDataSet(writeUser, "test", SQLDateBuilder.now(), "owner", bathymetryPoints);
        bathymetryDataSet = bathymetryDataSetService.addDataSet(writeUser.getUsername(), bathymetryDataSet);

        assertEquals(5, bathymetryDataSet.getMeasurements().size());
    }

    @Test(expected = AccessDeniedException.class)
    public void addDataSet_TryToAddDataWithReadOnlyAuthorityUser_ShouldThrowAccessDeniedException() {
        ApplicationUser readUser = applicationUserService.getApplicationUser("read");
        BathymetryDataSet bathymetryDataSet = new BathymetryDataSet(readUser, "test", SQLDateBuilder.now(), "owner", bathymetryPoints);
        bathymetryDataSetService.addDataSet(readUser.getUsername(), bathymetryDataSet);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getDataSet_GetNonExistentDataSet_ShouldThrowResourceNotFoundException() {
        bathymetryDataSetService.getDataSet(-1L);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void removeDataSet_RemoveNonExistentDataSet_ShouldThrowResourceNotFoundException() {
        bathymetryDataSetService.removeDataSet("read", -1L);
    }

    @Test
    public void removeDataSet_RemoveExistentDataSet_ShouldRemoveAllData() {
        BathymetryDataSet bathymetryDataSet = new BathymetryDataSet(writeUser, "test", SQLDateBuilder.now(), "owner", bathymetryPoints);
        bathymetryDataSet = bathymetryDataSetService.addDataSet(writeUser.getUsername(), bathymetryDataSet);

        bathymetryDataSetService.removeDataSet("write", bathymetryDataSet.getId());

        long act = StreamSupport.stream(bathymetryPointRepository.findAll().spliterator(), false).count();

        assertEquals(0L, act);
    }

    @Test(expected = AccessDeniedException.class)
    public void removeDataSet_RemoveOtherUserDataSet_ShouldThrowAccessDeniedException() {
        BathymetryDataSet bathymetryDataSet = new BathymetryDataSet(writeUser, "test", SQLDateBuilder.now(), "owner", bathymetryPoints);
        bathymetryDataSet = bathymetryDataSetService.addDataSet(writeUser.getUsername(), bathymetryDataSet);
        bathymetryDataSetService.removeDataSet("read", bathymetryDataSet.getId());
    }

    @Test
    public void getAllDataSets_AddTwoDataSets_ShouldReturnAllDataSets() {
        BathymetryDataSet bathymetryDataSet = new BathymetryDataSet(writeUser, "test", SQLDateBuilder.now(), "owner", bathymetryPoints);
        bathymetryDataSet = bathymetryDataSetService.addDataSet(writeUser.getUsername(), bathymetryDataSet);
        bathymetryDataSet = new BathymetryDataSet(writeUser, "test", SQLDateBuilder.now(), "owner", bathymetryPoints);
        bathymetryDataSet = bathymetryDataSetService.addDataSet(writeUser.getUsername(), bathymetryDataSet);

        int count = bathymetryDataSetService.getAllDataSets().size();
        assertEquals(2, count);
    }

    @Test
    public void getAllBathymetryPointsWithinGeometry_AddDataSet_ShouldReturnPointsInsideRectangle() {
        BathymetryDataSet bathymetryDataSet = new BathymetryDataSet(writeUser, "test", SQLDateBuilder.now(), "owner", bathymetryPoints);
        bathymetryDataSet = bathymetryDataSetService.addDataSet(writeUser.getUsername(), bathymetryDataSet);
        SimpleRectangle simpleRectangle = new SimpleRectangle(new Coordinate(2.5, 4.5), new Coordinate(4.5, 2.5));

        List<BathymetryPoint> bathymetryPoints = bathymetryDataSetService.getAllBathymetryPointsWithinGeometry(bathymetryDataSet.getId(), simpleRectangle);

        assertEquals(2, bathymetryPoints.size());
    }

    @Test
    public void countAllBathymetryPointsWithinGeometry_AddDataSet_ShouldReturnPointsCountInsideRectangle() {
        BathymetryDataSet bathymetryDataSet = new BathymetryDataSet(writeUser, "test", SQLDateBuilder.now(), "owner", bathymetryPoints);
        bathymetryDataSet = bathymetryDataSetService.addDataSet(writeUser.getUsername(), bathymetryDataSet);
        SimpleRectangle simpleRectangle = new SimpleRectangle(new Coordinate(2.5, 4.5), new Coordinate(4.5, 2.5));

        int act = bathymetryDataSetService.countAllBathymetryPointsWithinGeometry(bathymetryDataSet.getId(), simpleRectangle);

        assertEquals(2, act);
    }


}