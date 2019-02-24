package com.mdud.bathymetryplatform.bathymetry;

import com.mdud.bathymetryplatform.bathymetry.point.BathymetryPoint;
import com.mdud.bathymetryplatform.bathymetry.point.BathymetryPointRepository;
import com.mdud.bathymetryplatform.bathymetry.polygonselector.BoxRectangle;
import com.mdud.bathymetryplatform.bathymetry.parser.BathymetryDataParser;
import com.mdud.bathymetryplatform.exception.*;
import com.mdud.bathymetryplatform.user.ApplicationUser;
import com.mdud.bathymetryplatform.user.ApplicationUserService;
import com.mdud.bathymetryplatform.user.authority.Authorities;
import com.vividsolutions.jts.geom.Geometry;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Transactional
public class BathymetryDataSetService {

    private final BathymetryDataSetRepository bathymetryDataSetRepository;
    private final BathymetryPointRepository bathymetryPointRepository;
    private ApplicationUserService applicationUserService;

    @Autowired
    public BathymetryDataSetService(BathymetryDataSetRepository bathymetryDataSetRepository, BathymetryPointRepository bathymetryPointRepository,
                                    ApplicationUserService applicationUserService) {
        this.bathymetryDataSetRepository = bathymetryDataSetRepository;
        this.bathymetryPointRepository = bathymetryPointRepository;
        this.applicationUserService = applicationUserService;
    }

    public BathymetryDataSet getDataSet(Long id) {
        return bathymetryDataSetRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("dataset not found"));
    }

    public List<BathymetryDataSet> getAllDataSets() {
        return StreamSupport.stream(bathymetryDataSetRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    public List<BathymetryDataSet> getDataSetsByUser(String username) {
        ApplicationUser applicationUser = applicationUserService.getApplicationUser(username);
        return bathymetryDataSetRepository.findAllByApplicationUser(applicationUser).orElse(new ArrayList<>());
    }

    public List<BathymetryPoint> getAllBathymetryPointsWithinGeometry(Long id, BoxRectangle boxRectangle) {
        Geometry geometry = boxRectangle.buildGeometry(boxRectangle);
        return bathymetryPointRepository.findAllWithinGeometry(id, geometry).orElse(new ArrayList<>());
    }

    public Integer countAllBathymetryPointsWithinGeometry(Long id, BoxRectangle boxRectangle) {
        Geometry geometry = boxRectangle.buildGeometry(boxRectangle);
        return bathymetryPointRepository.findAllWithinGeometry(id, geometry).orElse(new ArrayList<>()).size();
    }

    private void throwIfNotExists(Long id) {
        getDataSet(id);
    }

    public BathymetryDataSet addDataSet(BathymetryDataSet bathymetryDataSet) {
        bathymetryDataSet = bathymetryDataSetRepository.nativeSave(bathymetryDataSet);
        return getDataSet(bathymetryDataSet.getId());
    }

    public BathymetryDataSet addDataSet(BathymetryDataSet bathymetryDataSet, int epsg, byte[] file) {
        BathymetryDataParser bathymetryDataParser;
        bathymetryDataParser = new BathymetryDataParser(epsg);

        List<BathymetryPoint> bathymetryPoints;
        try {
            bathymetryPoints = bathymetryDataParser.parseFile(file);
        } catch (NumberFormatException e) {
            throw new DataParsingException("invalid file");
        }

        if (bathymetryPoints == null) {
            throw new DataParsingException("data format not recognized");
        }

        bathymetryDataSet.setMeasurements(bathymetryPoints);
        return addDataSet(bathymetryDataSet);

    }


    public void removeDataSet(String username, Long id) {
        throwIfNotExists(id);
        BathymetryDataSet bathymetryDataSet = getDataSet(id);
        ApplicationUser applicationUser = applicationUserService.getApplicationUser(username);
        if (bathymetryDataSet.getApplicationUser().equals(applicationUser) || applicationUser.getUserAuthorities().stream().anyMatch(userAuthority ->
                userAuthority.getAuthority().getAuthorityName() == Authorities.ADMIN)) {
            bathymetryDataSetRepository.deleteById(id);
        } else {
            throw new AccessDeniedException("this resource belongs to other user");
        }
    }

    public BathymetryDataSet addDataSetFromDTO(BathymetryDataSetDTO bathymetryDataSetDTO, MultipartFile file) {
        BathymetryDataSet bathymetryDataSet = new BathymetryDataSet(bathymetryDataSetDTO.getApplicationUser(),
                bathymetryDataSetDTO.getName(),
                bathymetryDataSetDTO.getMeasurementDate(),
                bathymetryDataSetDTO.getDataOwner(), new ArrayList<>());
        int epsg = bathymetryDataSetDTO.getEpsgCode();

        byte[] fileBytes;
        try {
            fileBytes = file.getBytes();
        } catch (IOException e) {
            throw new ResourceAddException("failed to read file");
        }

        return addDataSet(bathymetryDataSet, epsg, fileBytes);
    }

    public BathymetryDataSet addData(BathymetryDataSetDTO bathymetryDataSetDTO, byte[] file) {
        BathymetryDataParser bathymetryDataParser = new BathymetryDataParser(bathymetryDataSetDTO.getEpsgCode());
        List<BathymetryPoint> bathymetryPoints = bathymetryDataParser.parseFile(file);

        BathymetryDataSet bathymetryDataSet = new BathymetryDataSet(bathymetryDataSetDTO, bathymetryPoints);
        return bathymetryDataSetRepository.nativeSave(bathymetryDataSet);
    }
}

