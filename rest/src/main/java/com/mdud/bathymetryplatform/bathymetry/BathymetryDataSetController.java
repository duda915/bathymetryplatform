package com.mdud.bathymetryplatform.bathymetry;


import com.mdud.bathymetryplatform.bathymetry.parser.BathymetryFileBuilder;
import com.mdud.bathymetryplatform.bathymetry.point.BathymetryPoint;
import com.mdud.bathymetryplatform.bathymetry.polygonselector.BoxRectangle;
import com.mdud.bathymetryplatform.controller.ResourceIdResponse;
import com.mdud.bathymetryplatform.controller.StringResponse;
import com.mdud.bathymetryplatform.gdal.GDALService;
import com.mdud.bathymetryplatform.geoserver.GeoServerService;
import com.mdud.bathymetryplatform.user.ApplicationUserService;
import com.mdud.bathymetryplatform.user.authority.Authorities;
import com.vividsolutions.jts.geom.Coordinate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@CrossOrigin
@RestController
@RequestMapping("/api/data")
public class BathymetryDataSetController {

    private final BathymetryDataSetService bathymetryDataSetService;
    private final ApplicationUserService applicationUserService;
    private final GDALService gdalService;
    private final GeoServerService geoServerService;

    @Autowired
    public BathymetryDataSetController(BathymetryDataSetService bathymetryDataSetService, ApplicationUserService applicationUserService,
                                       GDALService gdalService, GeoServerService geoServerService) {
        this.bathymetryDataSetService = bathymetryDataSetService;
        this.applicationUserService = applicationUserService;
        this.gdalService = gdalService;
        this.geoServerService = geoServerService;
    }

    @GetMapping
    @Transactional
    public List<BathymetryDataSet> getAllDataSets() {
        return bathymetryDataSetService.getAllDataSets();
    }

    @GetMapping("/user")
    @Transactional
    public List<BathymetryDataSet> getUserDataSets(Principal principal) {
        if (applicationUserService.checkUserAuthority(principal.getName(), Authorities.ADMIN)) {
            return bathymetryDataSetService.getAllDataSets();
        } else {
            return bathymetryDataSetService.getDataSetsByUser(principal.getName());
        }
    }

    @PreAuthorize("hasAuthority('WRITE')")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ResourceIdResponse addDataSet(Principal principal, @RequestPart("file") MultipartFile file,
                                         @Valid @RequestPart("data") BathymetryDataSetDTO bathymetryDataSetDTO) throws IOException {
        bathymetryDataSetDTO.setApplicationUser(applicationUserService.getApplicationUser(principal.getName()));
        BathymetryDataSet bathymetryDataSet;

        bathymetryDataSet = bathymetryDataSetService.addDataSet(bathymetryDataSetDTO, file.getBytes());

        File rasterFile = gdalService.createRaster(bathymetryDataSet.getId());
        geoServerService.addCoverageStore(rasterFile);
        if(rasterFile != null) {
            rasterFile.delete();
        }

        return new ResourceIdResponse(bathymetryDataSet.getId(), "data successfully uploaded");
    }

    @DeleteMapping
    public StringResponse deleteDataSet(Principal principal, @RequestParam(name = "id") Long id) {
        bathymetryDataSetService.removeDataSet(principal.getName(), id);
        geoServerService.deleteCoverageStore(id);
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

    @PostMapping(value = "/download/selection", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<byte[]> downloadDataSetsBySelection(@RequestParam("id") Long[] ids, @RequestBody BoxRectangle boxRectangle) {
        BathymetryFileBuilder bathymetryFileBuilder = new BathymetryFileBuilder();

        Arrays.asList(ids).forEach(id -> {
            List<BathymetryPoint> bathymetryPoints = bathymetryDataSetService.getAllBathymetryPointsWithinGeometry(id, boxRectangle);
            bathymetryPoints.forEach(bathymetryFileBuilder::append);
        });

        byte file[] = bathymetryFileBuilder.buildFile().getBytes();
        return createFileResponseEntity(file);
    }

    @PostMapping(value = "/download/selection/count", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public StringResponse countDataSetsBySelection(@RequestParam("id") Long[] ids, @RequestBody BoxRectangle boxRectangle) {
        AtomicInteger integer = new AtomicInteger();
        integer.set(0);
        Arrays.asList(ids).forEach(id -> {
            List<BathymetryPoint> bathymetryPoints = bathymetryDataSetService.getAllBathymetryPointsWithinGeometry(id, boxRectangle);
            integer.addAndGet(bathymetryPoints.size());
        });


        return new StringResponse(String.valueOf(integer.get()));
    }

    @GetMapping("/center")
    public Coordinate getDataSetCenter(@RequestParam("id") Long id) {
        return geoServerService.getCoverageStoreCenter(id);
    }

    @GetMapping("/box")
    public BoxRectangle getDataSetBoundingBox(@RequestParam("id") Long id) {
        return geoServerService.getCoverageStoreBoundingBox(id);
    }

    private ResponseEntity<byte[]> createFileResponseEntity(byte[] outFile) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("content-disposition", "attachment; filename=" + "results.txt");
        responseHeaders.add("Content-Type", "application/json");
        return new ResponseEntity<>(outFile, responseHeaders, HttpStatus.OK);
    }
}
