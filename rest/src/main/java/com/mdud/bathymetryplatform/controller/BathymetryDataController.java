package com.mdud.bathymetryplatform.controller;


import com.mdud.bathymetryplatform.bathymetry.*;
import com.mdud.bathymetryplatform.datamodel.AppUser;
import com.mdud.bathymetryplatform.datamodel.BathymetryCollection;
import com.mdud.bathymetryplatform.datamodel.BathymetryMeasure;
import com.mdud.bathymetryplatform.datamodel.Role;
import com.mdud.bathymetryplatform.datamodel.dto.BathymetryMetaDTO;
import com.mdud.bathymetryplatform.exception.*;
import com.mdud.bathymetryplatform.repository.BathymetryDataRepository;
import com.mdud.bathymetryplatform.repository.BathymetryMeasureRepository;
import com.mdud.bathymetryplatform.repository.RoleRepository;
import com.mdud.bathymetryplatform.repository.UserRepository;
import com.mdud.bathymetryplatform.security.AppRoles;
import com.mdud.bathymetryplatform.utility.configuration.AppConfiguration;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/data")
@Validated
public class BathymetryDataController {
    private final Logger logger = LoggerFactory.getLogger(BathymetryDataController.class);

    private BathymetryDataRepository bathymetryDataRepository;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BathymetryMeasureRepository bathymetryMeasureRepository;
    private AppConfiguration appConfiguration;

    @Autowired
    public BathymetryDataController(BathymetryDataRepository bathymetryDataRepository,
                                    UserRepository userRepository,
                                    RoleRepository roleRepository,
                                    BathymetryMeasureRepository bathymetryMeasureRepository,
                                    AppConfiguration appConfiguration) {
        this.bathymetryDataRepository = bathymetryDataRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bathymetryMeasureRepository = bathymetryMeasureRepository;
        this.appConfiguration = appConfiguration;
    }

    @GetMapping("/datasets")
    public List<BathymetryMetaDTO> getDataSetsMeta() {
        List<BathymetryMetaDTO> dataSets = new ArrayList<>();
        bathymetryDataRepository.findAll().forEach(data -> dataSets.add(new BathymetryMetaDTO(data)));
        return dataSets;
    }

    @GetMapping("/datasets/user")
    public List<BathymetryMetaDTO> getUserDataSets(Principal principal) {

        AppUser appUser = userRepository.findDistinctByUsername(principal.getName());
        Role superUserRole = roleRepository.findDistinctByRoleName(AppRoles.SUPER_USER);

        List<BathymetryMetaDTO> dataSets = new ArrayList<>();

        if(!appUser.checkRole(superUserRole)) {
            bathymetryDataRepository.findAllByAppUser(appUser).forEach(data -> dataSets.add(new BathymetryMetaDTO(data)));
        } else {
            bathymetryDataRepository.findAll().forEach(data -> dataSets.add(new BathymetryMetaDTO(data)));
        }

        return dataSets;
    }

    @PreAuthorize("hasAnyAuthority('USER', 'SUPERUSER')")
    @PostMapping("/datasets")
    @ResponseStatus(HttpStatus.OK)
    public void addNewData(@NotEmpty @RequestParam("name") String acquisitionName,
                              @RequestParam("date") Date acquisitionDate,
                              @NotEmpty @RequestParam("owner") String dataOwner,
                              @NotNull @RequestParam("crs") Integer crs,
                              @RequestParam("file") MultipartFile data,
                           Principal principal) {

        BathymetryCollection newCollection = null;
        File gdalFile = null;
        AppUser user = userRepository.findDistinctByUsername(principal.getName());

        try {

            newCollection = new BathymetryCollection(null, user, acquisitionName,
                    acquisitionDate, dataOwner, null);

            BathymetryDataParser dataParser = new BathymetryDataParser(crs);
            double parsingStart = System.currentTimeMillis();
            newCollection.setMeasureList(dataParser.parseFile(data));
            double parsingEnd = System.currentTimeMillis() - parsingStart;
            logger.info("Parsing time: " + parsingEnd);

            double persistenceStart = System.currentTimeMillis();
            bathymetryDataRepository.save(newCollection);
            double persistenceEnd = System.currentTimeMillis() - persistenceStart;
            logger.info("Persistence time: " + persistenceEnd);

            double geoServerTransferStart = System.currentTimeMillis();
            GDALGrid gdalGrid = new GDALGrid(appConfiguration);
            gdalFile = gdalGrid.createGridRasterFromDB(newCollection.getId());
            GeoServerCoverageStoreManager geoServerCoverageStoreManager = new GeoServerCoverageStoreManager(appConfiguration);
            geoServerCoverageStoreManager.addCoverageStore(gdalFile);
            double geoServerTransferEnd = System.currentTimeMillis() - geoServerTransferStart;
            logger.info("Transfer time: " + geoServerTransferEnd);

        } catch (NumberFormatException e) {
            throw new ResourceAddException("data parsing error");
        } catch (NoSuchAuthorityCodeException e) {
            throw new ResourceAddException("wrong epsg code");
        } catch (FactoryException e) {
            throw new ResourceAddException("geotools error");
        } catch (TransformException e) {
            throw new ResourceAddException("data transformation error");
        } catch (IOException e) {
            throw new ResourceAddException("data format unrecognized");
        } catch (GDALException e) {
            if(newCollection != null) {
                bathymetryDataRepository.delete(newCollection);
            }
            throw new ResourceAddException("gdal error");
        } catch (GeoServerException e) {
            bathymetryDataRepository.delete(newCollection);
            throw new ResourceAddException("geoserver error");
        } finally {
            if(gdalFile != null) {
                gdalFile.delete();
            }
        }

    }

    @DeleteMapping("/datasets")
    @ResponseStatus(HttpStatus.OK)
    public void deleteDataSet(@RequestParam("id") Long id, Principal principal) {
        AppUser user = userRepository.findDistinctByUsername(principal.getName());
        BathymetryCollection bathymetryCollection = bathymetryDataRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("wrong id"));
        Role superUserRole = roleRepository.findDistinctByRoleName(AppRoles.SUPER_USER);

        if(bathymetryCollection.getAppUser() != user && !user.checkRole(superUserRole)) {
            throw new AccessDeniedException("insufficient privileges");
        }

        GeoServerCoverageStoreManager geoServerCoverageStoreManager = new GeoServerCoverageStoreManager(appConfiguration);
        try {
            geoServerCoverageStoreManager.deleteCoverageStore(id);
            bathymetryDataRepository.delete(bathymetryCollection);
        } catch (GeoServerException e) {
            throw new ResourceNotFoundException("geoserver wrong id");
        }
    }

    @GetMapping(value = "/datasets/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> getData(@RequestParam("id") Long[] ids, HttpServletResponse response) {
        BathymetryFileBuilder fileBuilder = new BathymetryFileBuilder();

        for(Long id : ids) {
            BathymetryCollection bathymetryCollection = bathymetryDataRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("wrong id"));
            fileBuilder.append(bathymetryCollection);
        }

        byte[] outFile = fileBuilder.buildFile().getBytes(StandardCharsets.UTF_8);

        return createFileResponseEntity(outFile);
    }



    @GetMapping(value = "/datasets/download/geometry", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> getDataWithinGeometry(@RequestParam("id") Long ids[], @RequestParam("coords") double coords[], HttpServletResponse response) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(),4326);
        Geometry geometry = geometryFactory.createPolygon(new Coordinate[]{
                new Coordinate(coords[0], coords[1]),
                new Coordinate(coords[0], coords[3]),
                new Coordinate(coords[2], coords[3]),
                new Coordinate(coords[2], coords[1]),
                new Coordinate(coords[0], coords[1])
        });

        BathymetryFileBuilder fileBuilder = new BathymetryFileBuilder();

        List<BathymetryMeasure> measures = new ArrayList<>();
        for(Long id : ids) {
            Iterable<BathymetryMeasure> bathymetryMeasures = bathymetryMeasureRepository.findAllWithinGeometry(id, geometry)
                    .orElse(null);
            if(bathymetryMeasures == null) {
                continue;
            }

            bathymetryMeasures.forEach(measures::add);
        }

        if(measures.size() == 0) {
            throw new ResourceNotFoundException("no resources selected");
        }

        measures.forEach(fileBuilder::append);

        byte[] outFile = fileBuilder.buildFile().getBytes(StandardCharsets.UTF_8);

        return createFileResponseEntity(outFile);
    }

    @GetMapping("/datasets/center")
    public PlainPoint getDataSetCenter(@RequestParam("id") Long id) {
        GeoServerCoverageStoreManager geoServerCoverageStoreManager = new GeoServerCoverageStoreManager(appConfiguration);
        try {
            return geoServerCoverageStoreManager.getCoverageStoreCenter(id);
        } catch (GeoServerException e) {
            throw new ResourceNotFoundException("cannot get center point of this layer");
        }
    }

    private ResponseEntity<byte[]> createFileResponseEntity(byte[] outFile) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("content-disposition", "attachment; filename=" + "results.txt");
        responseHeaders.add("Content-Type", "application/json");

        return new ResponseEntity<>(outFile, responseHeaders, HttpStatus.OK);
    }

}
