package com.mdud.bathymetryplatform.datamodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "bathymetry_meta")
@Data @NoArgsConstructor
public class BathymetryMeta {
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

}
