package com.mdud.bathymetryplatform.epsg;

import com.mdud.bathymetryplatform.exception.EPSGException;
import lombok.extern.java.Log;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Log
public class EPSGCodeService {
    @Value(value = "classpath:epsg.csv")
    private Resource codesCSV;

    public List<EPSGCode> getAllCodes() {
        return parseCodes();
    }

    private List<EPSGCode> parseCodes() {
        List<EPSGCode> epsgCodes = new ArrayList<>();
        try {
            byte[] bytes = IOUtils.toByteArray(codesCSV.getURI());
            String file = IOUtils.toString(bytes, "UTF-8");
            String[] codes = file.split("\n");


            Arrays.asList(codes).forEach(code -> {
                epsgCodes.add(new EPSGCode(Long.valueOf(code)));
            });
        } catch (IOException e) {
            throw new EPSGException("failed to read epsgcodes");
        }

        return epsgCodes;
    }
}

