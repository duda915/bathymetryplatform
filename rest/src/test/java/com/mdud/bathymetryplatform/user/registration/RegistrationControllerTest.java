package com.mdud.bathymetryplatform.user.registration;

import com.mdud.bathymetryplatform.exception.MethodArgumentNotValidExceptionHandler;
import com.mdud.bathymetryplatform.user.ApplicationUser;
import com.mdud.bathymetryplatform.user.ApplicationUserDTO;
import com.mdud.bathymetryplatform.utility.JSONUtil;
import com.mdud.bathymetryplatform.utility.configuration.AppConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class RegistrationControllerTest {

    @InjectMocks
    private RegistrationController registrationController;

    @Mock
    private RegistrationService registrationService;

    @Spy
    private IPService ipService = new IPService();

    private MockMvc mockMvc;

    private ApplicationUserDTO applicationUserDTO;
    private ApplicationUser applicationUser;

    private String endpoint;

    @Before
    public void before() {
        applicationUserDTO = new ApplicationUserDTO("user", "user", "testmail@gmail.com");
        applicationUser = new ApplicationUser(applicationUserDTO);
        mockMvc = MockMvcBuilders.standaloneSetup(registrationController).setControllerAdvice(MethodArgumentNotValidExceptionHandler.class).build();
        endpoint = "/api/register";
    }

    @Test
    public void registerAccount_RegisterValidAccount_ShouldRegisterAccount() throws Exception {
        RegistrationToken registrationToken = new RegistrationToken(applicationUser);
        registrationToken.setId(1L);
        when(registrationService.registerUser(applicationUserDTO)).thenReturn(registrationToken);

        String json = JSONUtil.convertObjectToJsonString(applicationUserDTO);
        mockMvc.perform(post(endpoint)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("code send")));

        verify(registrationService, times(1)).registerUser(applicationUserDTO);
    }

    @Test
    public void registerAccount_RegisterInvalidUsernameAccount_ShouldThrowException() throws Exception {
        ApplicationUserDTO invalidAccount = new ApplicationUserDTO("", "", "testmail");

        String json = JSONUtil.convertObjectToJsonString(invalidAccount);
        mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(allOf(containsString("username must not"),
                        containsString("password must not"),
                        containsString("email must"))));
    }

    @Test
    public void activateAccount_ActivateAccountWithExistingToken_ShouldActivateAccountAndRedirect() throws Exception {
        RegistrationToken registrationToken = new RegistrationToken();
        String redirectIp = ipService.getExternalIp();

        mockMvc.perform(get(endpoint)
                .param("token", registrationToken.getToken()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("http://" + redirectIp));

        verify(registrationService, times(1)).activateUser(registrationToken.getToken());
    }
}