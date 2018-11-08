package com.mdud.bathymetryplatform.repository;

import com.mdud.bathymetryplatform.datamodel.BathymetryCollection;
import org.springframework.data.repository.CrudRepository;

public interface BathymetryDataRepository extends CrudRepository<BathymetryCollection, Long> {

}
