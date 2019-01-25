package com.mdud.bathymetryplatform.bathymetry;

import com.mdud.bathymetryplatform.bathymetry.point.BathymetryPoint;
import com.mdud.bathymetryplatform.bathymetry.point.BathymetryPointBuilder;
import com.mdud.bathymetryplatform.user.ApplicationUser;
import com.mdud.bathymetryplatform.user.ApplicationUserService;
import com.mdud.bathymetryplatform.user.token.TokenTestHelper;
import com.mdud.bathymetryplatform.utility.JSONUtil;
import com.mdud.bathymetryplatform.utility.SQLDateBuilder;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BathymetryDataSetControllerTest {

    @Autowired
    private ApplicationUserService applicationUserService;

    @Autowired
    private BathymetryDataSetService bathymetryDataSetService;

    @Autowired
    private MockMvc mockMvc;

    @Value(value = "classpath:testdata/testdata.xyz")
    private Resource resource;

    private String dataAPI = "/api/data";
    private TokenTestHelper tokenTestHelper;
    private String adminHeader;
    private BathymetryDataSet testBathymetryDataSet;

    @Before
    public void before() throws Exception {
        this.tokenTestHelper = new TokenTestHelper(mockMvc);
        this.adminHeader = tokenTestHelper.obtainAccessTokenHeader("admin", "admin");
        ApplicationUser applicationUser = applicationUserService.getApplicationUser("admin");

        List<BathymetryPoint> bathymetryPointList = new ArrayList<>();
        Arrays.asList(1,2,3,4,5).forEach(number -> bathymetryPointList.add(new BathymetryPointBuilder()
                .point(number, number)
                .depth(5)
                .buildPoint()
        ));

        this.testBathymetryDataSet = new BathymetryDataSet(applicationUser, "name", SQLDateBuilder.now(), "owner", bathymetryPointList);
    }

    @Test
    public void getAllDataSets_GetAllDataSetsWithoutAuthentication_ShouldReturnForbiddenStatus() throws Exception {
        mockMvc.perform(get(dataAPI)).andExpect(status().isUnauthorized());
    }

    @Test
    public void getAllDataSets_GetAllDataSetsWithAuthentication_ShouldReturnOKStatus() throws Exception {
        mockMvc.perform(get(dataAPI)
                .header("Authorization", adminHeader))
                .andExpect(status().isOk());
    }

    @Test
    public void addDataSet_AddDataSet_ShouldReturnOKStatus() throws Exception {
        byte[] file = IOUtils.toByteArray(resource.getURI());
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", file);
        ApplicationUser applicationUser = applicationUserService.getApplicationUser("admin");
        BathymetryDataSetDTO bathymetryDataSetDTO = new BathymetryDataSetDTO(applicationUser,
                32634, "test", SQLDateBuilder.now(), "owner");

        String json = JSONUtil.convertObjectToJsonString(bathymetryDataSetDTO);

        mockMvc.perform(multipart(dataAPI)
                .file(mockMultipartFile)
                .header("Authorization", adminHeader)
                .contentType(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk());
    }






}