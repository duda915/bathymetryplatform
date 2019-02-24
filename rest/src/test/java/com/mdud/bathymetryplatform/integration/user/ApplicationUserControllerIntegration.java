package com.mdud.bathymetryplatform.integration.user;

import com.mdud.bathymetryplatform.integration.user.token.TokenTestHelper;
import com.mdud.bathymetryplatform.user.ApplicationUser;
import com.mdud.bathymetryplatform.user.ApplicationUserService;
import com.mdud.bathymetryplatform.user.PasswordDTO;
import com.mdud.bathymetryplatform.utility.JSONUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ApplicationUserControllerIntegration {

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
        tokenTestHelper.obtainAccessTokenHeader("admin", "admin");
    }

    @Test(expected = AssertionError.class)
    public void testAuthentication_LogWithInvalidPassword_ShouldThrowException() throws Exception {
        tokenTestHelper.obtainAccessTokenHeader("admin", "notvalidpass");
    }

    @Test(expected = AssertionError.class)
    public void testAuthentication_LogWithInvalidUsername_ShouldThrowException() throws Exception {
        tokenTestHelper.obtainAccessTokenHeader("notvaliduser", "notvalidpass");
    }

    @Test
    public void getLoggedUser_LogAsAdmin_ShouldReturnUser() throws Exception {
        ApplicationUser applicationUser = applicationUserService.getApplicationUser("admin");
        mockMvc.perform(get(userAPI).header("Authorization", adminHeader)).andExpect(status().isOk())
                .andExpect(content().json(JSONUtil.convertObjectToJsonString(applicationUser)));
    }

    @Test
    public void changeUserPassword_ChangeAdminPassword_ShouldChangePassword() throws Exception {
        PasswordDTO passwordDTO = new PasswordDTO("newpassword");
        String json = JSONUtil.convertObjectToJsonString(passwordDTO);
        mockMvc.perform(put(userAPI).header("Authorization", adminHeader)
            .content(json)
        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        ApplicationUser applicationUser = applicationUserService.getApplicationUser("admin");

        assertTrue(ApplicationUser.PASSWORD_ENCODER.matches("newpassword", applicationUser.getPassword()));
    }

}