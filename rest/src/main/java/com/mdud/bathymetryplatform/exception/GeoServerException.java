package com.mdud.bathymetryplatform.exception;

public class GeoServerException extends Exception {
    public GeoServerException(String message) {
        super("GeoServer exception " + message);
    }
}
