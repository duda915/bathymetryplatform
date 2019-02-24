package com.mdud.bathymetryplatform.bathymetry;

import com.mdud.bathymetryplatform.exception.MethodArgumentNotValidExceptionHandler;
import com.mdud.bathymetryplatform.gdal.GDALService;
import com.mdud.bathymetryplatform.geoserver.GeoServerService;
import com.mdud.bathymetryplatform.user.ApplicationUser;
import com.mdud.bathymetryplatform.user.ApplicationUserDTO;
import com.mdud.bathymetryplatform.user.ApplicationUserService;
import com.mdud.bathymetryplatform.user.authority.Authorities;
import com.mdud.bathymetryplatform.utility.JSONUtil;
import com.mdud.bathymetryplatform.utility.SQLDateBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.ContentResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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


}