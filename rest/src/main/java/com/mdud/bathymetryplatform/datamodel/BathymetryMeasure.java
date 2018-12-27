package com.mdud.bathymetryplatform.datamodel;


import com.mdud.bathymetryplatform.datamodel.dto.BathymetryMeasureDTO;
import com.vividsolutions.jts.geom.Point;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "bathymetry")
@Data @NoArgsConstructor
public class BathymetryMeasure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gid;

    @Column(name = "meta_id", updatable = false)
    private Long metaId;

    @Column(name = "coords")
    private Point measureCoords;

    @Column(name = "measure")
    private Double measure;

    public BathymetryMeasure(Long metaId, Point measureCoords, Double measure) {
        this.metaId = metaId;
        this.measureCoords = measureCoords;
        this.measure = measure;
    }

    public BathymetryMeasure(Point measureCoords) {
        this.measureCoords = measureCoords;
    }

    public BathymetryMeasure(BathymetryMeasureDTO bathymetryMeasureDTO) {
        this.measureCoords = bathymetryMeasureDTO.getMeasureCoords();
        this.measure = bathymetryMeasureDTO.getMeasure();
    }
}
