package com.mdud.bathymetryplatform.bathymetryutil;

import com.mdud.bathymetryplatform.bathymetry.BathymetryDataSet;
import com.mdud.bathymetryplatform.bathymetry.point.BathymetryPoint;

public class BathymetryFileBuilder {

    private StringBuilder builder;

    public BathymetryFileBuilder() {
        builder = new StringBuilder();
    }

    public void append(BathymetryPoint bathymetryPoint) {
        String breakLine = "\n";
        String tab = "\t";

        StringBuilder builder = new StringBuilder();

        builder.append(bathymetryPoint.getMeasurementCoordinates().getX());
        builder.append(tab);
        builder.append(bathymetryPoint.getMeasurementCoordinates().getY());
        builder.append(tab);
        builder.append(bathymetryPoint.getDepth());
        builder.append(breakLine);

        this.builder.append(builder);
    }

    public void append(BathymetryDataSet bathymetryDataSet) {
        bathymetryDataSet.getMeasurements().forEach(this::append);
    }

    public String buildFile() {
        return this.builder.toString();
    }
}
