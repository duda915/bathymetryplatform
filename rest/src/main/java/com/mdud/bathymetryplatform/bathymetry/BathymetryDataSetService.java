package com.mdud.bathymetryplatform.bathymetry;

import com.mdud.bathymetryplatform.bathymetry.point.BathymetryPoint;
import com.mdud.bathymetryplatform.bathymetry.point.BathymetryPointRepository;
import com.mdud.bathymetryplatform.exception.AccessDeniedException;
import com.mdud.bathymetryplatform.exception.ResourceAlreadyExistsException;
import com.mdud.bathymetryplatform.exception.ResourceNotFoundException;
import com.mdud.bathymetryplatform.user.ApplicationUser;
import com.mdud.bathymetryplatform.user.ApplicationUserService;
import com.mdud.bathymetryplatform.user.authority.Authorities;
import com.vividsolutions.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

    public List<BathymetryDataSet> getDataSetsByUser(String username) {
        ApplicationUser applicationUser = applicationUserService.getApplicationUser(username);
        return bathymetryDataSetRepository.findAllByApplicationUser(applicationUser).orElse(new ArrayList<>());
    }

    public List<BathymetryPoint> getAllBathymetryPointsWithinGeometry(Long id, Geometry geometry) {
        return bathymetryPointRepository.findAllWithinGeometry(id, geometry).orElse(new ArrayList<>());
    }

    private void throwIfNotExists(Long id) {
        getDataSet(id);
    }

    private void throwIfExists(Long id) {
        if(bathymetryDataSetRepository.findById(id).orElse(null) != null) {
            throw new ResourceAlreadyExistsException("dataset already exist");
        }
    }

    public BathymetryDataSet addDataSet(String username, BathymetryDataSet bathymetryDataSet) {
        ApplicationUser applicationUser = applicationUserService.getApplicationUser(username);
        if(applicationUser.getUserAuthorities().stream().anyMatch(userAuthority -> userAuthority.getAuthority().getAuthorityName().equals(Authorities.WRITE))) {
            bathymetryDataSet = bathymetryDataSetRepository.nativeSave(bathymetryDataSet);
        } else {
            throw new AccessDeniedException("adding resource require write authority");
        }

        return getDataSet(bathymetryDataSet.getId());
    }

    public void removeDataSet(String username, Long id) {
        throwIfNotExists(id);
        BathymetryDataSet bathymetryDataSet = getDataSet(id);
        ApplicationUser applicationUser = applicationUserService.getApplicationUser(username);
        if(bathymetryDataSet.getApplicationUser().equals(applicationUser) || applicationUser.getUserAuthorities().stream().anyMatch(userAuthority ->
                userAuthority.getAuthority().getAuthorityName() == Authorities.ADMIN)) {
            bathymetryDataSetRepository.deleteById(id);
        } else {
            throw new AccessDeniedException("this resource belongs to other user");
        }
    }
}

