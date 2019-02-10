package com.mdud.bathymetryplatform.bathymetry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mdud.bathymetryplatform.bathymetry.point.BathymetryPoint;
import com.mdud.bathymetryplatform.bathymetry.point.BathymetryPointBuilder;
import com.mdud.bathymetryplatform.bathymetry.polygonselector.BoxRectangle;
import com.mdud.bathymetryplatform.controller.ResourceIdResponse;
import com.mdud.bathymetryplatform.user.ApplicationUser;
import com.mdud.bathymetryplatform.user.ApplicationUserService;
import com.mdud.bathymetryplatform.user.token.TokenTestHelper;
import com.mdud.bathymetryplatform.utility.JSONUtil;
import com.mdud.bathymetryplatform.utility.SQLDateBuilder;
import com.vividsolutions.jts.geom.Coordinate;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.Part;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Propagation;
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

    private Logger logger = LoggerFactory.getLogger(BathymetryDataSetControllerTest.class);

    @Autowired
    private ApplicationUserService applicationUserService;

    @Autowired
    private BathymetryDataSetService bathymetryDataSetService;

    @Autowired
    private BathymetryDataSetRepository bathymetryDataSetRepository;

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

    @After
    public void after() {
        bathymetryDataSetService.getAllDataSets().forEach(dataSet -> {
            try {
                clearDataSetAfterNonTransactionalTest(dataSet.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }

            bathymetryDataSetRepository.delete(dataSet);
        });

    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    public void getAllDataSets_GetAllDataSetsWithoutAuthentication_ShouldReturnForbiddenStatus() throws Exception {
        mockMvc.perform(get(dataAPI)).andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    public void getAllDataSets_GetAllDataSetsWithAuthentication_ShouldReturnOKStatus() throws Exception {
        addTestDataSet();
        mockMvc.perform(get(dataAPI)
                .header("Authorization", adminHeader))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    public void addDataSet_AddDataSet_ShouldReturnOKStatus() throws Exception {
        ResourceIdResponse resourceIdResponse = addTestDataSet();
        assertNotEmpty(resourceIdResponse.getResponse());
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    public void addDataSet_AddDataSetAsReadUser_ShouldReturnForbiddenStatus() throws Exception {
        byte[] file = IOUtils.toByteArray(resource.getURI());
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", file);
        ApplicationUser applicationUser = applicationUserService.getApplicationUser("guest");
        BathymetryDataSetDTO bathymetryDataSetDTO = new BathymetryDataSetDTO(applicationUser,
                32634, "test", SQLDateBuilder.now(), "owner");

        String readHeader = tokenTestHelper.obtainAccessTokenHeader("guest", "guest");

        String json = JSONUtil.convertObjectToJsonString(bathymetryDataSetDTO);

        MockMultipartFile jsonFile =
                new MockMultipartFile("data", "data", "application/json", json.getBytes());

        mockMvc.perform(multipart(dataAPI)
                .file(jsonFile)
                .file(mockMultipartFile)
                .header("Authorization", readHeader)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)).andExpect(status().isForbidden());


    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    public void downloadDataSetsById_DownloadDataSet_ShouldReturnDataSet() throws Exception {
        ResourceIdResponse resourceIdResponse = addTestDataSet();

        mockMvc.perform(get(dataAPI + "/download")
                        .param("id", String.valueOf(resourceIdResponse.getId()))
                .header("Authorization", adminHeader))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    public void downloadDataSetsBySelection_ShouldDownloadDataSetsBySelection_ShouldReturnSelectedDataSets() throws Exception {
        ResourceIdResponse resourceIdResponse = addTestDataSet();

        BoxRectangle boxRectangle = new BoxRectangle(new Coordinate(17.8, 54.82),
                new Coordinate(17.9, 54.81));

        String jsonRectangle = JSONUtil.convertObjectToJsonString(boxRectangle);

        mockMvc.perform(post(dataAPI + "/download/selection")
                .param("id", String.valueOf(resourceIdResponse.getId()))
                .header("Authorization", adminHeader)
                .content(jsonRectangle)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

    }

    private ResourceIdResponse addTestDataSet() throws Exception {
        //add
        byte[] file = IOUtils.toByteArray(resource.getURI());
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", file);
        ApplicationUser applicationUser = applicationUserService.getApplicationUser("admin");
        BathymetryDataSetDTO bathymetryDataSetDTO = new BathymetryDataSetDTO(applicationUser,
                32634, "test", SQLDateBuilder.now(), "owner");

        String json = JSONUtil.convertObjectToJsonString(bathymetryDataSetDTO);

        MockMultipartFile jsonFile = new MockMultipartFile("data", "", "application/json", json.getBytes());
        String response = mockMvc.perform(multipart(dataAPI)
                .file(mockMultipartFile)
                .file(jsonFile)
                .header("Authorization", adminHeader)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        return JSONUtil.convertJSONStringToObject(response, ResourceIdResponse.class);
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    public void getDataSetCenter_GetCenterOfDataSetFromGeoServer_ShouldReturnDataSetCenter() throws Exception {
        ResourceIdResponse resourceIdResponse = addTestDataSet();

        mockMvc.perform(get(dataAPI + "/center")
                .param("id", resourceIdResponse.getId().toString())
                .header("Authorization", adminHeader))
                .andExpect(status().isOk()).andDo(print());
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    public void getDataSetBoundingBox_GetBoundingBoxFromGeoServer_ShouldReturnBoundingBox() throws Exception {
        ResourceIdResponse resourceIdResponse = addTestDataSet();
        mockMvc.perform(get(dataAPI + "/box")
                .header("Authorization", adminHeader)
                .param("id", resourceIdResponse.getId().toString()))
                .andExpect(status().isOk()).andDo(print());
    }

    private void clearDataSetAfterNonTransactionalTest(Long id) throws Exception {
        mockMvc.perform(delete(dataAPI)
                .header("Authorization", adminHeader)
                .param("id", id.toString()))
                .andExpect(status().isOk());
    }

    private void assertNotEmpty(String actual) {
        Assert.assertNotEquals("", actual);
    }


}