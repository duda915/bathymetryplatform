package com.mdud.bathymetryplatform.user;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.security.Principal;
import java.util.HashSet;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ApplicationUserControllerTest {

    @Autowired
    private ApplicationUserService applicationUserService;

    @Autowired
    private MockMvc mockMvc;

    private String userAPI;
    private TokenTestHelper tokenTestHelper;
    private String adminHeader;

    @Before
    public void before() throws Exception {
        tokenTestHelper = new TokenTestHelper(mockMvc);
        this.userAPI =
                "/api/user";
        adminHeader = tokenTestHelper.obtainAccessTokenHeader("admin", "admin");
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
        ApplicationUser applicationUser = applicationUserService.getApplicationUser("admin");
        mockMvc.perform(get(userAPI).header("Authorization", adminHeader)).andExpect(status().isOk())
                .andExpect(content().json(JSONUtil.convertObjectToJsonString(applicationUser)));
    }

    @Test
    public void changeUserPassword_ChangeAdminPassword_ShouldChangePassword() throws Exception {
        String newPassword = "newpassword";
        mockMvc.perform(put(userAPI).header("Authorization", adminHeader)
            .content(newPassword)).andExpect(status().isOk());
        ApplicationUser applicationUser = applicationUserService.getApplicationUser("admin");

        assertTrue(ApplicationUser.PASSWORD_ENCODER.matches(newPassword, applicationUser.getPassword()));
    }

    @Test
    public void addUserWithoutRegistration_AddWithAdminAccount_ShouldAddNewUser() throws Exception {
        ApplicationUserDTO applicationUserDTO = new ApplicationUserDTO("test", "test");
        mockMvc.perform(post(userAPI).header("Authorization", adminHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONUtil.convertObjectToJsonString(applicationUserDTO)))
                .andExpect(status().isOk());

        applicationUserService.getApplicationUser(applicationUserDTO.getUsername());

    }






}