package com.mdud.bathymetryplatform.epsg;


import lombok.Data;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;

@Entity
@Table(name = "epsg_coordinatereferencesystem")
@Immutable
@Data
public class EPSGCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coord_ref_sys_code")
    private Long epsgCode;

    @Column(name = "coord_ref_sys_name")
    private String name;
}


