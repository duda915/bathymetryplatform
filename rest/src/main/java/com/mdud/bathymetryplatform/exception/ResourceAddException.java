package com.mdud.bathymetryplatform.exception;

public class ResourceAddException extends RuntimeException {
    public ResourceAddException(String message) {
        super("Data insertion failed: " + message);
    }
}
