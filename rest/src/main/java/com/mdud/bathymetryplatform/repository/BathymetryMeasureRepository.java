package com.mdud.bathymetryplatform.repository;

import com.mdud.bathymetryplatform.bathymetry.BathymetryPoint;
import com.vividsolutions.jts.geom.Geometry;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BathymetryMeasureRepository extends CrudRepository<BathymetryPoint, Long> {
    @Query("SELECT bm FROM BathymetryPoint as bm where bm.bathymetryId = :id AND within(bm.measurementCoordinates, :geom) = true")
    Optional<Iterable<BathymetryPoint>> findAllWithinGeometry(@Param("id") Long id, @Param("geom") Geometry geometry);
}
