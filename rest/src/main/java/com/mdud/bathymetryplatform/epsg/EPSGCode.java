package com.mdud.bathymetryplatform.epsg;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;

@Data
@AllArgsConstructor
public class EPSGCode {
    private Long epsgCode;
}


