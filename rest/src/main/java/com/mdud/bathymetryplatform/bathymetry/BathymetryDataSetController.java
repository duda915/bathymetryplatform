package com.mdud.bathymetryplatform.bathymetry;


import com.mdud.bathymetryplatform.bathymetry.point.BathymetryPoint;
import com.mdud.bathymetryplatform.bathymetry.polygonselector.SimpleRectangle;
import com.mdud.bathymetryplatform.bathymetryutil.BathymetryFileBuilder;
import com.mdud.bathymetryplatform.gdal.GDALService;
import com.mdud.bathymetryplatform.bathymetryutil.GeoServerCoverageStoreManager;
import com.mdud.bathymetryplatform.controller.ResourceIdResponse;
import com.mdud.bathymetryplatform.controller.StringResponse;
import com.mdud.bathymetryplatform.exception.GeoServerException;
import com.mdud.bathymetryplatform.exception.ResourceAddException;
import com.mdud.bathymetryplatform.user.ApplicationUserService;
import com.mdud.bathymetryplatform.user.authority.Authorities;
import com.mdud.bathymetryplatform.utility.configuration.AppConfiguration;
import com.vividsolutions.jts.geom.Coordinate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/data")
public class BathymetryDataSetController {

    private final BathymetryDataSetService bathymetryDataSetService;
    private final ApplicationUserService applicationUserService;
    private final AppConfiguration appConfiguration;

    @Autowired
    public BathymetryDataSetController(BathymetryDataSetService bathymetryDataSetService, ApplicationUserService applicationUserService, AppConfiguration appConfiguration) {
        this.bathymetryDataSetService = bathymetryDataSetService;
        this.applicationUserService = applicationUserService;
        this.appConfiguration = appConfiguration;
    }

    @GetMapping
    public List<BathymetryDataSet> getAllDataSets() {
        return bathymetryDataSetService.getAllDataSets();
    }

    @GetMapping("/user")
    public List<BathymetryDataSet> getUserDataSets(Principal principal) {
        if(applicationUserService.checkUserAuthority(principal.getName(), Authorities.ADMIN)) {
            return bathymetryDataSetService.getAllDataSets();
        } else {
            return bathymetryDataSetService.getDataSetsByUser(principal.getName());
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResourceIdResponse addDataSet(Principal principal, @RequestParam(value = "file") MultipartFile file,
                                         @RequestBody BathymetryDataSetDTO bathymetryDataSetDTO) {
        bathymetryDataSetDTO.setApplicationUser(applicationUserService.getApplicationUser(principal.getName()));
        BathymetryDataSet bathymetryDataSet;
        try {
            bathymetryDataSet = bathymetryDataSetService.addDataSetFromDTO(bathymetryDataSetDTO, file);
        } catch (IOException e) {
            throw new ResourceAddException("data file is required");
        }

        GDALService gdalService = new GDALService(appConfiguration);
        File rasterFile = gdalService.createRaster(bathymetryDataSet.getId());
        GeoServerCoverageStoreManager geoServerCoverageStoreManager = new GeoServerCoverageStoreManager(appConfiguration);

        try {
            geoServerCoverageStoreManager.addCoverageStore(rasterFile);
        } catch (GeoServerException e) {
            bathymetryDataSetService.removeDataSet(principal.getName(), bathymetryDataSet.getId());
            throw new ResourceAddException("failed to add data");
        } finally {
            rasterFile.delete();
        }


        return new ResourceIdResponse(bathymetryDataSet.getId(), "data successfully uploaded");
    }

    @DeleteMapping
    public StringResponse deleteDataSet(Principal principal, @RequestParam(name = "id") Long id) {
        bathymetryDataSetService.removeDataSet(principal.getName(), id);
        GeoServerCoverageStoreManager geoServerCoverageStoreManager = new GeoServerCoverageStoreManager(appConfiguration);
        geoServerCoverageStoreManager.deleteCoverageStore(id);
        return new StringResponse("data successfully removed");
    }

    @GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Transactional
    public ResponseEntity<byte[]> downloadDataSetsByIds(@RequestParam("id") Long[] ids) {
        BathymetryFileBuilder bathymetryFileBuilder = new BathymetryFileBuilder();

        Arrays.asList(ids).forEach(id -> {
            BathymetryDataSet bathymetryDataSet = bathymetryDataSetService.getDataSet(id);
            bathymetryFileBuilder.append(bathymetryDataSet);
        });

        byte[] file = bathymetryFileBuilder.buildFile().getBytes();
        return createFileResponseEntity(file);
    }

    @GetMapping(value = "/download/selection",  produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<byte[]> downloadDataSetsBySelection(@RequestParam("id") Long[] ids, @RequestBody SimpleRectangle simpleRectangle) {
        BathymetryFileBuilder bathymetryFileBuilder = new BathymetryFileBuilder();

        Arrays.asList(ids).forEach(id -> {
            List<BathymetryPoint> bathymetryPoints = bathymetryDataSetService.getAllBathymetryPointsWithinGeometry(id, simpleRectangle);
            bathymetryPoints.forEach(bathymetryFileBuilder::append);
        });

        byte file[] = bathymetryFileBuilder.buildFile().getBytes();
        return createFileResponseEntity(file);
    }

    @GetMapping("/center")
    public Coordinate getDataSetCenter(@RequestParam("id") Long id) {
        GeoServerCoverageStoreManager geoServerCoverageStoreManager = new GeoServerCoverageStoreManager(appConfiguration);
        return geoServerCoverageStoreManager.getCoverageStoreCenterCoordinate(id);
    }

    private ResponseEntity<byte[]> createFileResponseEntity(byte[] outFile) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("content-disposition", "attachment; filename=" + "results.txt");
        responseHeaders.add("Content-Type", "application/json");
        return new ResponseEntity<>(outFile, responseHeaders, HttpStatus.OK);
    }
}
