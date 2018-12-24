package com.mdud.bathymetryplatform.repository;

import com.mdud.bathymetryplatform.datamodel.BathymetryMeasure;
import com.vividsolutions.jts.geom.Geometry;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BathymetryMeasureRepository extends CrudRepository<BathymetryMeasure, Long> {
    @Query("SELECT bm FROM BathymetryMeasure as bm where bm.metaId = :id AND within(bm.measureCoords, :geom) = true")
    Optional<Iterable<BathymetryMeasure>> findAllWithinGeometry(@Param("id") Long id, @Param("geom") Geometry geometry);
}
