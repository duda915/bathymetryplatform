package com.mdud.bathymetryplatform.exception;

public class AccessDeniedException extends RuntimeException{
    public AccessDeniedException(String message) {
        super("Access denied: " + message);
    }
}
