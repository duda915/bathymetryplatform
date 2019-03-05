package com.mdud.bathymetryplatform.bathymetry;

import com.mdud.bathymetryplatform.bathymetry.point.BathymetryPoint;
import com.mdud.bathymetryplatform.bathymetry.polygonselector.BoxRectangle;
import com.mdud.bathymetryplatform.exception.MethodArgumentNotValidExceptionHandler;
import com.mdud.bathymetryplatform.gdal.GDALService;
import com.mdud.bathymetryplatform.geoserver.GeoServerService;
import com.mdud.bathymetryplatform.user.ApplicationUser;
import com.mdud.bathymetryplatform.user.ApplicationUserDTO;
import com.mdud.bathymetryplatform.user.ApplicationUserService;
import com.mdud.bathymetryplatform.user.authority.Authorities;
import com.mdud.bathymetryplatform.utility.JSONUtil;
import com.mdud.bathymetryplatform.utility.SQLDateBuilder;
import com.vividsolutions.jts.geom.Coordinate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class BathymetryDataSetControllerTest {

    @InjectMocks
    private BathymetryDataSetController bathymetryDataSetController;

    @Mock
    private BathymetryDataSetService bathymetryDataSetService;

    @Mock
    private ApplicationUserService applicationUserService;

    @Mock
    private GDALService gdalService;

    @Mock
    private GeoServerService geoServerService;

    private MockMvc mockMvc;

    private String endpoint = "/api/data";

    @Value(value = "classpath:testdata/testdata.xyz")
    private Resource resource;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(bathymetryDataSetController)
                .setControllerAdvice(MethodArgumentNotValidExceptionHandler.class).build();
    }

    @Test
    public void getAllDataSets() throws Exception {
        mockMvc.perform(get(endpoint))
                .andExpect(status().isOk());

        verify(bathymetryDataSetService, times(1)).getAllDataSets();
    }

    @Test
    public void getUserDataSets_AsNormalUser_ShouldReturnUserOnlyDataSets() throws Exception {
        Principal principal = () -> "user";

        when(applicationUserService.checkUserAuthority("user", Authorities.ADMIN)).thenReturn(false);

        mockMvc.perform(get(endpoint + "/user").principal(principal))
                .andExpect(status().isOk());

        verify(bathymetryDataSetService, times(1)).getDataSetsByUser("user");
    }

    @Test
    public void getUserDataSets_AsAdmin_ShouldReturnAllDataSets() throws Exception {
        Principal principal = () -> "admin";

        when(applicationUserService.checkUserAuthority("admin", Authorities.ADMIN)).thenReturn(true);

        mockMvc.perform(get(endpoint + "/user").principal(principal))
                .andExpect(status().isOk());

        verify(bathymetryDataSetService, times(1)).getAllDataSets();
    }

    @Test
    public void addDataSet_AddValidDataSet_ShouldAddDataSet() throws Exception {
        ApplicationUserDTO applicationUserDTO = new ApplicationUserDTO("user", "", "");
        ApplicationUser applicationUser = new ApplicationUser(applicationUserDTO);
        Principal principal = () -> "user";

        BathymetryDataSetDTO bathymetryDataSetDTO = new BathymetryDataSetDTO(applicationUser,
                32634, "name", SQLDateBuilder.now(), "me");

        String json = JSONUtil.convertObjectToJsonString(bathymetryDataSetDTO);

        BathymetryDataSet bathymetryDataSet = new BathymetryDataSet(bathymetryDataSetDTO, null);
        bathymetryDataSet.setId(1L);

        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", resource.getInputStream());
        MockMultipartFile jsonFile = new MockMultipartFile("data", "", "application/json", json.getBytes());

        when(applicationUserService.getApplicationUser("user")).thenReturn(applicationUser);
        when(bathymetryDataSetService.addDataSet(bathymetryDataSetDTO, mockMultipartFile.getBytes())).thenReturn(bathymetryDataSet);

        mockMvc.perform(multipart(endpoint)
                .file(mockMultipartFile)
                .file(jsonFile)
                .principal(principal))
                .andExpect(status().isOk());

        verify(bathymetryDataSetService, times(1)).addDataSet(bathymetryDataSetDTO, mockMultipartFile.getBytes());
        verify(gdalService, times(1)).createRaster(1L);
        verify(geoServerService, times(1)).addCoverageStore(null);
    }

    @Test
    public void addDataSet_AddInvalidDataSet_ShouldReturnBadRequest() throws Exception {
        ApplicationUserDTO applicationUserDTO = new ApplicationUserDTO("user", "", "");
        ApplicationUser applicationUser = new ApplicationUser(applicationUserDTO);
        Principal principal = () -> "user";

        BathymetryDataSetDTO bathymetryDataSetDTO = new BathymetryDataSetDTO(applicationUser, null, "", null, "");

        String json = JSONUtil.convertObjectToJsonString(bathymetryDataSetDTO);

        BathymetryDataSet bathymetryDataSet = new BathymetryDataSet(bathymetryDataSetDTO, null);
        bathymetryDataSet.setId(1L);

        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", resource.getInputStream());
        MockMultipartFile jsonFile = new MockMultipartFile("data", "", "application/json", json.getBytes());

        when(applicationUserService.getApplicationUser("user")).thenReturn(applicationUser);
        when(bathymetryDataSetService.addDataSet(bathymetryDataSetDTO, mockMultipartFile.getBytes())).thenReturn(bathymetryDataSet);

        mockMvc.perform(multipart(endpoint)
                .file(mockMultipartFile)
                .file(jsonFile)
                .principal(principal))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(content().string(allOf(containsString("epsgCode must not"),
                        containsString("dataOwner must not be"),
                        containsString("measurementDate must not be"),
                        containsString("name must not be"))));

    }

    @Test
    public void removeDataSet() throws Exception {
        Principal principal = () -> "user";

        mockMvc.perform(delete(endpoint)
                .param("id", "1")
                .principal(principal))
                .andExpect(status().isOk());

        verify(geoServerService, times(1)).deleteCoverageStore(1L);
        verify(bathymetryDataSetService, times(1)).removeDataSet(principal.getName(), 1L);
    }


    @Test
    public void downloadDataSetsByIds() throws Exception {
        Principal principal = () -> "user";
        ApplicationUserDTO applicationUserDTO = new ApplicationUserDTO("", "", "");
        ApplicationUser applicationUser = new ApplicationUser(applicationUserDTO);

        BathymetryDataSetDTO bathymetryDataSetDTO = new BathymetryDataSetDTO(applicationUser, 32634, "name", SQLDateBuilder.now(),
                "data");
        BathymetryDataSet bathymetryDataSet = new BathymetryDataSet(bathymetryDataSetDTO, new ArrayList<>());
        when(bathymetryDataSetService.getDataSet(1L)).thenReturn(bathymetryDataSet);
        when(bathymetryDataSetService.getDataSet(2L)).thenReturn(bathymetryDataSet);

        mockMvc.perform(get(endpoint + "/download")
                .param("id", "1")
                .param("id", "2")
                .principal(principal))
                .andExpect(status().isOk());

        verify(bathymetryDataSetService, times(1)).getDataSet(1L);
        verify(bathymetryDataSetService, times(1)).getDataSet(2L);
    }

    @Test
    public void downloadDataSetsBySelection() throws Exception {
        Principal principal = () -> "user";

        BoxRectangle boxRectangle = new BoxRectangle(new Coordinate(12, 12), new Coordinate(13, 13));
        String json = JSONUtil.convertObjectToJsonString(boxRectangle);

        when(bathymetryDataSetService.getAllBathymetryPointsWithinGeometry(1L, boxRectangle)).thenReturn(new ArrayList<>());
        when(bathymetryDataSetService.getAllBathymetryPointsWithinGeometry(2L, boxRectangle)).thenReturn(new ArrayList<>());

        mockMvc.perform(post(endpoint + "/download/selection")
                .param("id", "1")
                .param("id", "2")
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json))
                .andDo(print())
                .andExpect(status().isOk());


        verify(bathymetryDataSetService, times(1)).getAllBathymetryPointsWithinGeometry(1L, boxRectangle);
        verify(bathymetryDataSetService, times(1)).getAllBathymetryPointsWithinGeometry(2L, boxRectangle);
    }

    @Test
    public void countDataSetsBySelection() throws Exception {
        Principal principal = () -> "user";

        BoxRectangle boxRectangle = new BoxRectangle(new Coordinate(12, 12), new Coordinate(13, 13));
        String json = JSONUtil.convertObjectToJsonString(boxRectangle);

        when(bathymetryDataSetService.getAllBathymetryPointsWithinGeometry(1L, boxRectangle)).thenReturn(new ArrayList<>());
        when(bathymetryDataSetService.getAllBathymetryPointsWithinGeometry(2L, boxRectangle)).thenReturn(new ArrayList<>());

        mockMvc.perform(post(endpoint + "/download/selection/count")
                .param("id", "1")
                .param("id", "2")
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value(0));


        verify(bathymetryDataSetService, times(1)).getAllBathymetryPointsWithinGeometry(1L, boxRectangle);
        verify(bathymetryDataSetService, times(1)).getAllBathymetryPointsWithinGeometry(2L, boxRectangle);
    }

    @Test
    public void getDataSetCenter() throws Exception {
        when(geoServerService.getCoverageStoreCenter(1L)).thenReturn(new Coordinate());

        mockMvc.perform(get(endpoint + "/center")
                .param("id", "1"))
                .andExpect(status().isOk());

        verify(geoServerService, times(1)).getCoverageStoreCenter(1L);
    }

    @Test
    public void getDataSetBoundingBox() throws Exception {
        when(geoServerService.getCoverageStoreBoundingBox(1L)).thenReturn(new BoxRectangle());

        mockMvc.perform(get(endpoint + "/box")
                .param("id", "1"))
                .andExpect(status().isOk());

        verify(geoServerService, times(1)).getCoverageStoreBoundingBox(1L);
    }

    @Test
    public void getDataSetsBoundingBox() throws Exception {
        Long[] ids = {2L, 3L};
        when(geoServerService.getCoverageStoresBoundingBox(ids)).thenReturn(new BoxRectangle());

        mockMvc.perform(get(endpoint + "/globalbox")
                .param("ids", "2")
                .param("ids", "3"))
                .andExpect(status().isOk());

        verify(geoServerService, times(1)).getCoverageStoresBoundingBox(ids);
    }
}