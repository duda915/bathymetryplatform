package com.mdud.bathymetryplatform.datamodel;


import lombok.Data;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "bathymetry_meta")
@Data
public class BathymetryCollection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String acquisitionName;

    @Column(name = "collection_date")
    private Date acquisitionDate;

    @Column(name = "author")
    private String dataOwner;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "meta_id", referencedColumnName = "id", nullable = false)
    private List<BathymetryMeasure> measureList;

    public BathymetryCollection(String acquisitionName, Date acquisitionDate, String dataOwner) {
        this.acquisitionName = acquisitionName;
        this.acquisitionDate = acquisitionDate;
        this.dataOwner = dataOwner;
    }
}
