package com.mdud.bathymetryplatform.integration.user.registration;

import com.mdud.bathymetryplatform.controller.ResourceIdResponse;
import com.mdud.bathymetryplatform.user.ApplicationUser;
import com.mdud.bathymetryplatform.user.ApplicationUserDTO;
import com.mdud.bathymetryplatform.user.ApplicationUserService;
import com.mdud.bathymetryplatform.user.registration.RegistrationException;
import com.mdud.bathymetryplatform.user.registration.RegistrationRepository;
import com.mdud.bathymetryplatform.user.registration.RegistrationToken;
import com.mdud.bathymetryplatform.utility.JSONUtil;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class RegistrationControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private ApplicationUserService applicationUserService;

    private final String registerAPI = "/api/register";

    @Test
    public void registerAccount() throws Exception {
        ApplicationUserDTO applicationUserDTO = new ApplicationUserDTO("test", "test", "email2@email.com");

        String json = JSONUtil.convertObjectToJsonString(applicationUserDTO);

        String response = mockMvc.perform(post(registerAPI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        ResourceIdResponse resourceIdResponse = JSONUtil.convertJSONStringToObject(response, ResourceIdResponse.class);

        assertNotNull(registrationRepository.findById(resourceIdResponse.getId()).orElse(null));
    }

    @Test
    public void activateAccount_RegisterAndActivateAccount() throws Exception {
        ApplicationUserDTO applicationUserDTO = new ApplicationUserDTO("test", "test", "email2@email.com");

        String json = JSONUtil.convertObjectToJsonString(applicationUserDTO);

        String response = mockMvc.perform(post(registerAPI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        ResourceIdResponse resourceIdResponse = JSONUtil.convertJSONStringToObject(response, ResourceIdResponse.class);

        RegistrationToken registrationToken =
                registrationRepository.findById(resourceIdResponse.getId()).orElseThrow(() -> new RegistrationException("registered token not found"));

        mockMvc.perform(get(registerAPI)
                .param("token", registrationToken.getToken()));

        ApplicationUser applicationUser = applicationUserService.getApplicationUser("test");
        assertTrue(applicationUser.isActive());
    }
}