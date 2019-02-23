package com.mdud.bathymetryplatform.integration.user;

import com.mdud.bathymetryplatform.exception.UserNotFoundException;
import com.mdud.bathymetryplatform.user.ApplicationUser;
import com.mdud.bathymetryplatform.user.ApplicationUserDTO;
import com.mdud.bathymetryplatform.user.ApplicationUserService;
import com.mdud.bathymetryplatform.user.PasswordDTO;
import com.mdud.bathymetryplatform.user.authority.Authorities;
import com.mdud.bathymetryplatform.integration.user.token.TokenTestHelper;
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
        PasswordDTO passwordDTO = new PasswordDTO("newpassword");
        String json = JSONUtil.convertObjectToJsonString(passwordDTO);
        mockMvc.perform(put(userAPI).header("Authorization", adminHeader)
            .content(json)
        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        ApplicationUser applicationUser = applicationUserService.getApplicationUser("admin");

        assertTrue(ApplicationUser.PASSWORD_ENCODER.matches("newpassword", applicationUser.getPassword()));
    }

    @Test
    public void addUserWithoutRegistration_AddWithAdminAccount_ShouldAddNewUser() throws Exception {
        ApplicationUserDTO applicationUserDTO = new ApplicationUserDTO("test", "test", "test@mail.com");
        mockMvc.perform(post(userAPI).header("Authorization", adminHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONUtil.convertObjectToJsonString(applicationUserDTO)))
                .andExpect(status().isOk());

        applicationUserService.getApplicationUser(applicationUserDTO.getUsername());
    }

    @Test
    public void addUserWithoutRegistration_AddWithNormalUserAccount_ShouldReturnForbiddenStatus() throws Exception {
        ApplicationUserDTO applicationUserDTO = new ApplicationUserDTO("test", "Test", "test@mail.com");
        String writeUserHeader = tokenTestHelper.obtainAccessTokenHeader("write", "write");
        mockMvc.perform(post(userAPI).header("Authorization", writeUserHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONUtil.convertObjectToJsonString(applicationUserDTO)))
                .andExpect(status().isForbidden());
    }

    @Test(expected = UserNotFoundException.class)
    public void deleteUser_DeleteUserFromAdminAccount_ShouldDeleteUser() throws Exception {
        String deleteUser = "guest";
        mockMvc.perform(delete(userAPI)
                .header("Authorization", adminHeader)
                .content(deleteUser)
        ).andExpect(status().isOk());

        applicationUserService.getApplicationUser(deleteUser);
    }

    @Test
    public void deleteUser_DeleteUserFromNonAdminAccoutn_ShouldDeleteUser() throws Exception {
        String deleteUser = "guest";
        String writeUserHeader = tokenTestHelper.obtainAccessTokenHeader("write", "write");
        mockMvc.perform(delete(userAPI)
                .header("Authorization", writeUserHeader)
                .content(deleteUser)
        ).andExpect(status().isForbidden());
    }

    @Test
    public void addAuthorityToUser_AddAuthorityFromAdminAccount_ShouldAddAuthority() throws Exception {
        String targetUser = "guest";
        String adminAuthority = JSONUtil.convertObjectToJsonString(Authorities.ADMIN);
        mockMvc.perform(put(userAPI + "/authority")
                .header("Authorization", adminHeader)
                .content(adminAuthority)
                .contentType(MediaType.APPLICATION_JSON)
                .param("username", targetUser)
        ).andExpect(status().isOk());

        ApplicationUser readAdminUser = applicationUserService.getApplicationUser("guest");

        boolean act = readAdminUser.getUserAuthorities().stream().
                anyMatch(userAuthority -> userAuthority.getAuthority().getAuthorityName() == Authorities.ADMIN);

        assertTrue(act);
    }

    @Test
    public void addAuthorityToUser_AddAuthorityFromNonAdminAccount_ShouldReturnForbidden() throws Exception {
        String writeUserTokenHeader = tokenTestHelper.obtainAccessTokenHeader("write", "write");
        String targetUser = "guest";
        String adminAuthority = JSONUtil.convertObjectToJsonString(Authorities.ADMIN);
        mockMvc.perform(put(userAPI + "/authority")
                .header("Authorization", writeUserTokenHeader)
                .content(adminAuthority)
                .contentType(MediaType.APPLICATION_JSON)
                .param("username", targetUser)
        ).andExpect(status().isForbidden());
    }

    @Test
    public void removeUserAuthority_RemoveUserAuthorityFromAdminAccount_ShouldRemoveAuthority() throws Exception {
        String targetUser = "guest";
        String targetAuthority = JSONUtil.convertObjectToJsonString(Authorities.READ);
        mockMvc.perform(delete(userAPI + "/authority")
                .header("Authorization", adminHeader)
                .content(targetAuthority)
                .contentType(MediaType.APPLICATION_JSON)
                .param("username", targetUser)
        ).andExpect(status().isOk());

        ApplicationUser applicationUser = applicationUserService.getApplicationUser("guest");
        boolean act = applicationUser.getUserAuthorities()
                .stream().noneMatch(userAuthority -> userAuthority.getAuthority().getAuthorityName() == Authorities.READ);

        assertTrue(act);
    }

    @Test
    public void removeUserAuthority_RemoveUserAuthorityFromNonAdminAccount_ShouldReturnForbidden() throws Exception {
        String writeUserToken = tokenTestHelper.obtainAccessTokenHeader("write", "write");
        String targetUser = "guest";
        String targetAuthority = JSONUtil.convertObjectToJsonString(Authorities.READ);
        mockMvc.perform(delete(userAPI + "/authority")
                .header("Authorization", writeUserToken)
                .content(targetAuthority)
                .contentType(MediaType.APPLICATION_JSON)
                .param("username", targetUser)
        ).andExpect(status().isForbidden());

    }
}