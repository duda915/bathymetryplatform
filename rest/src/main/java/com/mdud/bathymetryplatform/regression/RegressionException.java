package com.mdud.bathymetryplatform.regression;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RegressionException extends RuntimeException{
    public RegressionException(String message) {
        super(message);
    }
}
