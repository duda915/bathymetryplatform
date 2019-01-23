package com.mdud.bathymetryplatform.bathymetry;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mdud.bathymetryplatform.user.ApplicationUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "bathymetry")
@Data @NoArgsConstructor @AllArgsConstructor
public class BathymetryDataSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private ApplicationUser applicationUser;

    @Column(name = "name")
    private String name;

    @Column(name = "measurement_date")
    private Date measurementDate;

    @Column(name = "owner")
    private String dataOwner;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "bathymetry_id", nullable = false)
    private List<BathymetryPoint> measurements;
}
