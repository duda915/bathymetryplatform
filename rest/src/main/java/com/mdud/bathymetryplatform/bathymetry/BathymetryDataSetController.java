package com.mdud.bathymetryplatform.bathymetry;


import com.mdud.bathymetryplatform.bathymetry.point.BathymetryPoint;
import com.mdud.bathymetryplatform.bathymetry.polygonselector.SimpleRectangle;
import com.mdud.bathymetryplatform.bathymetryutil.BathymetryFileBuilder;
import com.mdud.bathymetryplatform.controller.StringResponse;
import com.mdud.bathymetryplatform.exception.ResourceAddException;
import com.mdud.bathymetryplatform.user.ApplicationUser;
import com.mdud.bathymetryplatform.user.ApplicationUserService;
import com.mdud.bathymetryplatform.user.authority.Authorities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/data")
public class BathymetryDataSetController {

    private final BathymetryDataSetService bathymetryDataSetService;
    private final ApplicationUserService applicationUserService;

    @Autowired
    public BathymetryDataSetController(BathymetryDataSetService bathymetryDataSetService, ApplicationUserService applicationUserService) {
        this.bathymetryDataSetService = bathymetryDataSetService;
        this.applicationUserService = applicationUserService;
    }

    @GetMapping
    public List<BathymetryDataSet> getAllDataSets() {
        return bathymetryDataSetService.getAllDataSets();
    }

    @GetMapping("/user")
    public List<BathymetryDataSet> getUserDataSets(Principal principal) {
        if(applicationUserService.checkUserAuthority(principal.getName(), Authorities.ADMIN)) {
            return bathymetryDataSetService.getAllDataSets();
        } else {
            return bathymetryDataSetService.getDataSetsByUser(principal.getName());
        }
    }

    @PostMapping
    public StringResponse addDataSet(Principal principal, @RequestBody BathymetryDataSetDTO bathymetryDataSetDTO) {
        bathymetryDataSetDTO.setApplicationUser(applicationUserService.getApplicationUser(principal.getName()));
        try {
            bathymetryDataSetService.addDataSetFromDTO(bathymetryDataSetDTO);
        } catch (IOException e) {
            throw new ResourceAddException("data file is required");
        }

        return new StringResponse("data successfully uploaded");
    }

    @DeleteMapping
    public StringResponse deleteDataSet(Principal principal, @RequestParam(name = "id") Long id) {
        bathymetryDataSetService.removeDataSet(principal.getName(), id);
        return new StringResponse("data successfully removed");
    }

    @GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadDataSetsByIds(@RequestParam("id") Long[] ids) {
        BathymetryFileBuilder bathymetryFileBuilder = new BathymetryFileBuilder();

        Arrays.asList(ids).forEach(id -> {
            BathymetryDataSet bathymetryDataSet = bathymetryDataSetService.getDataSet(id);
            bathymetryFileBuilder.append(bathymetryDataSet);
        });

        byte[] file = bathymetryFileBuilder.buildFile().getBytes();
        return createFileResponseEntity(file);
    }

    @GetMapping(value = "/download/selection",  produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadDataSetsBySelection(@RequestParam("id") Long[] ids, @RequestBody SimpleRectangle simpleRectangle) {
        BathymetryFileBuilder bathymetryFileBuilder = new BathymetryFileBuilder();

        Arrays.asList(ids).forEach(id -> {
            List<BathymetryPoint> bathymetryPoints = bathymetryDataSetService.getAllBathymetryPointsWithinGeometry(id, simpleRectangle);
            bathymetryPoints.forEach(bathymetryFileBuilder::append);
        });

        byte file[] = bathymetryFileBuilder.buildFile().getBytes();
        return createFileResponseEntity(file);
    }

    private ResponseEntity<byte[]> createFileResponseEntity(byte[] outFile) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("content-disposition", "attachment; filename=" + "results.txt");
        responseHeaders.add("Content-Type", "application/json");
        return new ResponseEntity<>(outFile, responseHeaders, HttpStatus.OK);
    }
}
