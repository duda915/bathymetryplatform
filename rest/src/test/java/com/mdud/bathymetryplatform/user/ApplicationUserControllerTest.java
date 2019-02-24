package com.mdud.bathymetryplatform.user;

import com.mdud.bathymetryplatform.utility.JSONUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
public class ApplicationUserControllerTest {
    private MockMvc mockMvc;

    private String endpoint = "/api/user";

    @InjectMocks
    private ApplicationUserController applicationUserController;

    @Mock
    private ApplicationUserService applicationUserService;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(applicationUserController).build();
    }

    @Test
    public void getUser() throws Exception {
        ApplicationUserDTO applicationUserDTO = new ApplicationUserDTO("user", "user", "");
        ApplicationUser applicationUser = new ApplicationUser(applicationUserDTO);
        when(applicationUserService.getApplicationUser("user")).thenReturn(applicationUser);

        Principal principal = () -> "user";

        mockMvc.perform(get(endpoint).principal(principal))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("user")));
        verify(applicationUserService, times(1)).getApplicationUser("user");
    }

    @Test
    public void changeUserPassword() throws Exception {
        ApplicationUserDTO applicationUserDTO = new ApplicationUserDTO("user", "user", "");
        ApplicationUser applicationUser = new ApplicationUser(applicationUserDTO);
        when(applicationUserService.getApplicationUser("user")).thenReturn(applicationUser);

        Principal principal = () -> "user";

        PasswordDTO passwordDTO = new PasswordDTO("newpass");
        String json = JSONUtil.convertObjectToJsonString(passwordDTO);


        mockMvc.perform(put(endpoint).principal(principal)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json))
                .andExpect(status().isOk());
        verify(applicationUserService, times(1)).changeUserPassword("user", "newpass");
    }


}