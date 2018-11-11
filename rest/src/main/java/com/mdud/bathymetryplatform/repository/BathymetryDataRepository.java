package com.mdud.bathymetryplatform.repository;

import com.mdud.bathymetryplatform.datamodel.BathymetryCollection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface BathymetryDataRepository extends CrudRepository<BathymetryCollection, Long> {

}
