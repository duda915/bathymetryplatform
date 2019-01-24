package com.mdud.bathymetryplatform.bathymetry.point;

import com.vividsolutions.jts.geom.Geometry;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BathymetryPointRepository extends CrudRepository<BathymetryPoint, Long> {
    @Query("SELECT point FROM BathymetryPoint as point where point.bathymetryId = :id AND within(point.measurementCoordinates, :geom) = true")
    Optional<Iterable<BathymetryPoint>> findAllWithinGeometry(@Param("id") Long id, @Param("geom") Geometry geometry);
}
