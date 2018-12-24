package com.mdud.bathymetryplatform.datamodel;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "bathymetry_meta")
@Data @NoArgsConstructor @AllArgsConstructor
public class BathymetryCollection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private AppUser appUser;

    @Column(name = "name")
    private String acquisitionName;

    @Column(name = "collection_date")
    private Date acquisitionDate;

    @Column(name = "author")
    private String dataOwner;

    @Column(name = "layer_name")
    private String layerName;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "meta_id", referencedColumnName = "id", insertable = false, updatable = false, nullable = false)
    private List<BathymetryMeasure> measureList;
}
