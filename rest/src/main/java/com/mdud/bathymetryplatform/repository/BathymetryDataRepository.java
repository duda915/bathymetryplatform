package com.mdud.bathymetryplatform.repository;

import com.mdud.bathymetryplatform.bathymetry.BathymetryDataSet;
import com.mdud.bathymetryplatform.user.ApplicationUser;
import com.mdud.bathymetryplatform.repository.custom.BathymetryCollectionPersister;
import org.springframework.data.repository.CrudRepository;

public interface BathymetryDataRepository extends CrudRepository<BathymetryDataSet, Long>, BathymetryCollectionPersister<BathymetryDataSet> {
    Iterable<BathymetryDataSet> findAllByApplicationUser(ApplicationUser applicationUser);
}
