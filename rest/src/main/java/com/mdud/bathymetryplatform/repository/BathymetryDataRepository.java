package com.mdud.bathymetryplatform.repository;

import com.mdud.bathymetryplatform.datamodel.AppUser;
import com.mdud.bathymetryplatform.datamodel.BathymetryCollection;
import com.mdud.bathymetryplatform.datamodel.BathymetryMeasure;
import com.mdud.bathymetryplatform.repository.custom.BathymetryCollectionPersister;
import com.vividsolutions.jts.geom.Geometry;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BathymetryDataRepository extends CrudRepository<BathymetryCollection, Long>, BathymetryCollectionPersister<BathymetryCollection> {
    Iterable<BathymetryCollection> findAllByAppUser(AppUser appUser);
}
