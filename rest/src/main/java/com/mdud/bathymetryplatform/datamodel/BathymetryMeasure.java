package com.mdud.bathymetryplatform.datamodel;


import com.vividsolutions.jts.geom.Point;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import javax.persistence.*;

@Entity
@Table(name = "bathymetry")
@Getter @Setter @NoArgsConstructor
public class BathymetryMeasure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gid;

    @Column(name = "meta_id", insertable = false, updatable = false)
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
}
