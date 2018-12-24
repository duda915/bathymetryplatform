package com.mdud.bathymetryplatform.controller;


import com.mdud.bathymetryplatform.bathymetry.BathymetryDataParser;
import com.mdud.bathymetryplatform.bathymetry.BathymetryFileBuilder;
import com.mdud.bathymetryplatform.datamodel.*;
import com.mdud.bathymetryplatform.datamodel.dto.BathymetryMeasureDTO;
import com.mdud.bathymetryplatform.datamodel.dto.BathymetryMetaDTO;
import com.mdud.bathymetryplatform.exception.AccessDeniedException;
import com.mdud.bathymetryplatform.exception.ResourceAddException;
import com.mdud.bathymetryplatform.exception.ResourceNotFoundException;
import com.mdud.bathymetryplatform.repository.*;
import com.mdud.bathymetryplatform.security.AppRoles;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/data")
public class BathymetryDataController {
    private final Logger logger = LoggerFactory.getLogger(BathymetryDataController.class);

    private BathymetryDataRepository bathymetryDataRepository;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BathymetryMeasureRepository bathymetryMeasureRepository;
    private EntityManagerFactory entityManagerFactory;

    public BathymetryDataController(@Autowired BathymetryDataRepository bathymetryDataRepository,
                                    @Autowired UserRepository userRepository,
                                    @Autowired RoleRepository roleRepository,
                                    @Autowired BathymetryMeasureRepository bathymetryMeasureRepository,
                                    @Autowired EntityManagerFactory entityManagerFactory) {
        this.bathymetryDataRepository = bathymetryDataRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bathymetryMeasureRepository = bathymetryMeasureRepository;
        this.entityManagerFactory = entityManagerFactory;

    }

    @GetMapping("/datasets")
    private List<BathymetryMetaDTO> getDataSetsMeta() {
        List<BathymetryMetaDTO> dataSets = new ArrayList<>();
        bathymetryDataRepository.findAll().forEach(data -> dataSets.add(new BathymetryMetaDTO(data)));
        return dataSets;
    }

    @GetMapping("/datasets/user")
    private List<BathymetryMetaDTO> getUserDataSets(Principal principal) {
        AppUser appUser = userRepository.findDistinctByUsername(principal.getName());
        Role superUserRole = roleRepository.findDistinctByRoleName(AppRoles.SUPER_USER);

        List<BathymetryMetaDTO> dataSets = new ArrayList<>();

        if(!appUser.checkRole(superUserRole)) {
            bathymetryDataRepository.findAllByAppUser(appUser).forEach(data -> dataSets.add(new BathymetryMetaDTO(data)));
            return dataSets;
        } else {
            bathymetryDataRepository.findAll().forEach(data -> dataSets.add(new BathymetryMetaDTO(data)));
            return dataSets;
        }
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.OK)
    private void addNewData(@RequestParam("name") String acquisitionName,
                              @RequestParam("date") Date acquisitionDate,
                              @RequestParam("owner") String dataOwner,
                              @RequestParam("crs") Integer crs,
                              @RequestParam("file") MultipartFile data,
                              Principal principal) throws ResourceAddException {
        try {
            AppUser user = userRepository.findDistinctByUsername(principal.getName());

            logger.info("addNewData by: " + user.getUsername());

            String layerName = user.getUsername() + acquisitionName.hashCode();

            BathymetryCollection newCollection = new BathymetryCollection(null, user, acquisitionName,
                    acquisitionDate, dataOwner, layerName, null);

            List<BathymetryMeasure> measures = new ArrayList<>();

            BathymetryDataParser dataParser = new BathymetryDataParser(crs);

            double parsingStart = System.currentTimeMillis();

            String lines[] = new String(data.getBytes(), StandardCharsets.UTF_8).split("\n");

            try {
                BathymetryMeasureDTO headerCheck = dataParser.parsePoint(lines[0]);
                measures.add(new BathymetryMeasure(headerCheck));
            } catch (NumberFormatException e) {
                logger.info("Cannot parse first line of file - header?");
            }

            for(int i = 1; i < lines.length; i++) {
                BathymetryMeasureDTO measureDTO = dataParser.parsePoint(lines[i]);
                if(measureDTO == null)
                    continue;
                measures.add(new BathymetryMeasure(measureDTO));

            }

            double parsingEnd = System.currentTimeMillis() - parsingStart;
            double persistenceStart = System.currentTimeMillis();
            logger.info("Parsing time: " + parsingEnd);

            EntityManager entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            entityManager.persist(newCollection);
            entityManager.flush();
            entityManager.clear();
            for(int i = 0; i < measures.size(); i++) {
                measures.get(i).setMetaId(newCollection.getId());
                entityManager.persist(measures.get(i));
                if(i % 50 == 0) {
                    entityManager.flush();
                    entityManager.clear();
                }
            }
            entityManager.getTransaction().commit();
            entityManager.close();

            double persistenceEnd = System.currentTimeMillis() - persistenceStart;
            logger.info("Persistence time: " + persistenceEnd);

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
        }

    }

    @GetMapping(value = "/getdata", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    private ResponseEntity<byte[]> getData(@RequestParam("id") Long[] ids, HttpServletResponse response) {
        BathymetryFileBuilder fileBuilder = new BathymetryFileBuilder();

        for(Long id : ids) {
            BathymetryCollection bathymetryCollection = bathymetryDataRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("wrong id"));
            fileBuilder.append(bathymetryCollection);
        }

        byte[] outFile = fileBuilder.buildFile().getBytes(StandardCharsets.UTF_8);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("content-disposition", "attachment; filename=" + "results.txt");
        responseHeaders.add("Content-Type", "application/json");

        return new ResponseEntity<>(outFile, responseHeaders, HttpStatus.OK);
    }

    @DeleteMapping("/datasets/user/delete")
    @ResponseStatus(HttpStatus.OK)
    private void deleteDataSet(@RequestParam("id") Long id, Principal principal) {
        AppUser user = userRepository.findDistinctByUsername(principal.getName());
        BathymetryCollection bathymetryCollection = bathymetryDataRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("wrong id"));
        Role superUserRole = roleRepository.findDistinctByRoleName(AppRoles.SUPER_USER);

        if(bathymetryCollection.getAppUser() != user && !user.checkRole(superUserRole)) {
            throw new AccessDeniedException("insufficient privileges");
        }

        bathymetryDataRepository.delete(bathymetryCollection);
    }

    @GetMapping(value = "/getdata/geometry", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    private ResponseEntity<byte[]> getDataWithinGeometry(@RequestParam("id") Long ids[], @RequestParam("coords") double coords[], HttpServletResponse response) {
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

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("content-disposition", "attachment; filename=" + "results.txt");
        responseHeaders.add("Content-Type", "application/json");

        return new ResponseEntity<>(outFile, responseHeaders, HttpStatus.OK);
    }
}
