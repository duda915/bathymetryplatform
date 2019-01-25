package com.mdud.bathymetryplatform.user;

import com.mdud.bathymetryplatform.user.token.TokenTestHelper;
import com.mdud.bathymetryplatform.utility.JSONUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(secure = false)
@Transactional
public class ApplicationUserControllerTest {

    @Autowired
    ApplicationUserService applicationUserService;

    private TokenTestHelper tokenTestHelper;

    @LocalServerPort
    private int port;

    @Autowired
    private MockMvc mockMvc;

    private String userAPI;

    @Before
    public void before() {
        tokenTestHelper = new TokenTestHelper(mockMvc);
        this.userAPI = "http://localhost:" + port +
                "/api/user";
    }

    @Test
    public void testAuthentication_LogAsValidUser_ShouldBeOK() throws Exception {
        String token = tokenTestHelper.obtainAccessTokenHeader("admin", "admin");
    }

    @Test(expected = AssertionError.class)
    public void testAuthentication_LogWithInvalidPassword_ShouldThrowException() throws Exception {
        String token = tokenTestHelper.obtainAccessTokenHeader("admin", "notvalidpass");
    }

    @Test(expected = AssertionError.class)
    public void testAuthentication_LogWithInvalidUsername_ShouldThrowException() throws Exception {
        String token = tokenTestHelper.obtainAccessTokenHeader("notvaliduser", "notvalidpass");
    }

    @Test
    public void getLoggedUser_LogAsAdmin_ShouldReturnUser() throws Exception {
        String header = tokenTestHelper.obtainAccessTokenHeader("admin", "admin");
        ApplicationUser applicationUser = applicationUserService.getApplicationUser("admin");
        mockMvc.perform(get(userAPI).header("Authorization", header)).andExpect(status().isOk())
                .andExpect(content().json(JSONUtil.convertObjectToJsonString(applicationUser)));
    }

    




}