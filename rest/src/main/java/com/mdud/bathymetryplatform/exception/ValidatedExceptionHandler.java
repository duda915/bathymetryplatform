package com.mdud.bathymetryplatform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;

@ControllerAdvice
public class ValidatedExceptionHandler {

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> errorHandler(HttpServletRequest req, ConstraintViolationException e) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("message", e.getMessage().substring(e.getMessage().indexOf('.') + 1));
        hashMap.put("url", req.getRequestURL());

        return new ResponseEntity<>(hashMap, HttpStatus.BAD_REQUEST);
    }
}
