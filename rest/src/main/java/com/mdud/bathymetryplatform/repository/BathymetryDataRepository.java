package com.mdud.bathymetryplatform.repository;

import com.mdud.bathymetryplatform.user.ApplicationUser;
import com.mdud.bathymetryplatform.datamodel.BathymetryCollection;
import com.mdud.bathymetryplatform.repository.custom.BathymetryCollectionPersister;
import org.springframework.data.repository.CrudRepository;

public interface BathymetryDataRepository extends CrudRepository<BathymetryCollection, Long>, BathymetryCollectionPersister<BathymetryCollection> {
    Iterable<BathymetryCollection> findAllByApplicationUser(ApplicationUser applicationUser);
}
