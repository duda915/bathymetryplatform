package com.mdud.bathymetryplatform.bathymetry.point;


import com.mdud.bathymetryplatform.datamodel.dto.BathymetryMeasureDTO;
import com.vividsolutions.jts.geom.Point;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "bathymetry_point")
@Data @NoArgsConstructor
public class BathymetryPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gid;

    @Column(name = "coordinates")
    private Point measurementCoordinates;

    @Column(name = "bathymetry_id", insertable = false, updatable = false)
    private Long bathymetryId;

    @Column(name = "depth")
    private Double depth;

    public BathymetryPoint(Point measurementCoordinates, Double depth) {
        this.measurementCoordinates = measurementCoordinates;
        this.depth = depth;
    }

    public BathymetryPoint(Point measurementCoordinates) {
        this.measurementCoordinates = measurementCoordinates;
    }

    public BathymetryPoint(BathymetryMeasureDTO bathymetryMeasureDTO) {
        this.measurementCoordinates = bathymetryMeasureDTO.getMeasureCoords();
        this.depth = bathymetryMeasureDTO.getMeasure();
    }
}
