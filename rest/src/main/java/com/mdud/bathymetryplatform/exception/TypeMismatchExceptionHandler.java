package com.mdud.bathymetryplatform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@ControllerAdvice
public class TypeMismatchExceptionHandler {

    @ResponseBody
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> errorHandler(HttpServletRequest req, MethodArgumentTypeMismatchException e) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("message", e.getParameter().getParameterName() + " is not valid");
        hashMap.put("url", req.getRequestURL());

        return new ResponseEntity<>(hashMap, HttpStatus.BAD_REQUEST);
    }

}
