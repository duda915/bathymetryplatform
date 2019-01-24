package com.mdud.bathymetryplatform.bathymetry;

import com.mdud.bathymetryplatform.bathymetry.point.BathymetryPoint;
import com.mdud.bathymetryplatform.repository.NativePersister;
import com.mdud.bathymetryplatform.user.ApplicationUser;
import com.vividsolutions.jts.geom.Geometry;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BathymetryDataSetRepository extends CrudRepository<BathymetryDataSet, Long>, NativePersister<BathymetryDataSet> {
    Optional<List<BathymetryDataSet>> findAllByApplicationUser(ApplicationUser applicationUser);
}
