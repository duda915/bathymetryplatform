package com.mdud.bathymetryplatform.bathymetry;

import com.mdud.bathymetryplatform.datamodel.BathymetryCollection;
import com.mdud.bathymetryplatform.datamodel.BathymetryMeasure;

public class BathymetryFileBuilder {
    private String breakLine = "\n";
    private String tab = "\t";
    private StringBuilder builder;

    public BathymetryFileBuilder() {
        builder = new StringBuilder();
    }

    public void append(BathymetryMeasure bathymetryMeasure) {
        StringBuilder builder = new StringBuilder();

        builder.append(bathymetryMeasure.getMeasureCoords().getX());
        builder.append(tab);
        builder.append(bathymetryMeasure.getMeasureCoords().getY());
        builder.append(tab);
        builder.append(bathymetryMeasure.getMeasure());
        builder.append(breakLine);

        this.builder.append(builder);
    }

    public void append(BathymetryCollection bathymetryCollection) {
        bathymetryCollection.getMeasureList().forEach(this::append);
    }

    public String buildFile() {
        return this.builder.toString();
    }
}
