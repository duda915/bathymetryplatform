package com.mdud.bathymetryplatform.regression;

import com.mdud.bathymetryplatform.bathymetry.BathymetryDataSet;
import com.mdud.bathymetryplatform.bathymetry.BathymetryDataSetDTO;
import com.mdud.bathymetryplatform.bathymetry.BathymetryDataSetService;
import com.mdud.bathymetryplatform.bathymetry.parser.BathymetryFileBuilder;
import com.mdud.bathymetryplatform.bathymetry.point.BathymetryPoint;
import com.mdud.bathymetryplatform.bathymetry.polygonselector.BoxRectangle;
import com.mdud.bathymetryplatform.controller.ResourceIdResponse;
import com.mdud.bathymetryplatform.gdal.GDALService;
import com.mdud.bathymetryplatform.geoserver.GeoServerService;
import com.mdud.bathymetryplatform.user.ApplicationUser;
import com.mdud.bathymetryplatform.user.ApplicationUserService;
import com.mdud.bathymetryplatform.utility.SQLDateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.security.Principal;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/regression")
public class RegressionController {
    private final RegressionService regressionService;
    private final BathymetryDataSetService bathymetryDataSetService;
    private final ApplicationUserService applicationUserService;
    private final GDALService gdalService;
    private final GeoServerService geoServerService;

    public RegressionController(RegressionService regressionService, BathymetryDataSetService bathymetryDataSetService,
                                ApplicationUserService applicationUserService, GDALService gdalService,
                                GeoServerService geoServerService) {
        this.regressionService = regressionService;
        this.bathymetryDataSetService = bathymetryDataSetService;
        this.applicationUserService = applicationUserService;
        this.gdalService = gdalService;
        this.geoServerService = geoServerService;
    }

    @PreAuthorize("hasAuthority('WRITE')")
    @PostMapping("/publish")
    public ResourceIdResponse publishResults(Principal principal, @RequestBody BoxRectangle boxRectangle) {
        List<BathymetryPoint> pointList = regressionService.getResults(boxRectangle);
        ApplicationUser applicationUser = applicationUserService.getApplicationUser(principal.getName());
        BathymetryDataSetDTO bathymetryDataSetDTO = new BathymetryDataSetDTO(applicationUser, 4326, "Regression: " + boxRectangle.getLowerRightVertex().toString()
                 + boxRectangle.getUpperLeftVertex().toString(),
                SQLDateBuilder.now(), "RegressionService");
        BathymetryDataSet bathymetryDataSet = bathymetryDataSetService.addDataSet(bathymetryDataSetDTO, pointList);
        File raster = gdalService.createRaster(bathymetryDataSet.getId());
        geoServerService.addCoverageStore(raster);
        if(raster != null) {
            raster.delete();
        }

        return new ResourceIdResponse(bathymetryDataSet.getId(), "results published");
    }

    @PostMapping(value = "/download")
    public ResponseEntity<byte[]> downloadResults(@RequestBody BoxRectangle boxRectangle) {
        BathymetryFileBuilder bathymetryFileBuilder = new BathymetryFileBuilder();
        regressionService.getResults(boxRectangle)
                .forEach(bathymetryFileBuilder::append);

        return createFileResponseEntity(bathymetryFileBuilder.buildFile().getBytes());
    }

    private ResponseEntity<byte[]> createFileResponseEntity(byte[] outFile) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("content-disposition", "attachment; filename=" + "regressionresults.txt");
        responseHeaders.add("Content-Type", "application/json");
        return new ResponseEntity<>(outFile, responseHeaders, HttpStatus.OK);
    }

}
