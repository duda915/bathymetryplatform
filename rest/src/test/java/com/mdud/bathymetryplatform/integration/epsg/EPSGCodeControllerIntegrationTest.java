package com.mdud.bathymetryplatform.integration.epsg;


import com.mdud.bathymetryplatform.epsg.EPSGCodeController;
import com.mdud.bathymetryplatform.integration.user.token.TokenTestHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class EPSGCodeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private String endpoint = "/api/epsg";

    private TokenTestHelper tokenTestHelper;

    @Before
    public void setup() {
        tokenTestHelper = new TokenTestHelper(mockMvc);
    }

    @Test
    public void getAllCodes_WithWriteUser_ShouldReturnEPSGCodes() throws Exception {
        String header = tokenTestHelper.obtainAccessTokenHeader("admin", "admin");

        mockMvc.perform(get(endpoint)
        .header("Authorization", header))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("4326")));
    }

    @Test
    public void getAllCodes_WithReadUser_ShouldReturnForbidden() throws Exception {
        String header = tokenTestHelper.obtainAccessTokenHeader("guest", "guest");

        mockMvc.perform(get(endpoint)
                .header("Authorization", header))
                .andExpect(status().isForbidden());
    }
}
