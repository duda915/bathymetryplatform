package com.mdud.bathymetryplatform.geoserver;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GeoServerServiceTest {


    @Autowired
    private GeoServerService geoServerService;

    @Test
    public void checkIfWorkspaceExists_CheckIfExistsGeoServerWorkspaceExists_ShouldReturnTrue() {
        boolean act = geoServerService.checkIfWorkspaceExists();
        assertTrue(act);
    }
}