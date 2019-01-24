package com.mdud.bathymetryplatform.bathymetry.polygonselector;

import com.vividsolutions.jts.geom.Geometry;

public interface GeometryAdapter<T> {
    Geometry buildGeometry(T object);
}
