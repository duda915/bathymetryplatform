package com.mdud.bathymetryplatform.integration.user;

import com.mdud.bathymetryplatform.exception.UserAlreadyExistsException;
import com.mdud.bathymetryplatform.exception.UserException;
import com.mdud.bathymetryplatform.exception.UserNotFoundException;
import com.mdud.bathymetryplatform.user.ApplicationUser;
import com.mdud.bathymetryplatform.user.ApplicationUserService;
import com.mdud.bathymetryplatform.user.authority.Authorities;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ApplicationUserServiceTest {

    @Autowired
    private ApplicationUserService applicationUserService;

    @Test
    public void getApplicationUser_GetExistingUser_ShouldReturnUser() {
        ApplicationUser applicationUser = applicationUserService.getApplicationUser("guest");
        assertNotNull(applicationUser);
    }

    @Test(expected = UserNotFoundException.class)
    public void getApplicationUser_GetNonExistingUser_ShouldThrowServiceException() {
        applicationUserService.getApplicationUser("test");
    }

    @Test
    public void changeUserPassword_ChangeExistingUserPassword_ShouldChangeUserPassword() {
        ApplicationUser applicationUser = applicationUserService.changeUserPassword("guest", "newpass");
        ApplicationUser newPass = applicationUserService.getApplicationUser("guest");

        boolean act = ApplicationUser.PASSWORD_ENCODER.matches("newpass", newPass.getPassword());

        assertTrue(act);
    }

    @Test(expected = UserException.class)
    public void changeUserPassword_ChangePasswordToTheSamePassword_ShouldThrowServiceException() {
        applicationUserService.changeUserPassword("guest", "guest");
    }

    @Test(expected = UserNotFoundException.class)
    public void changeUserPassword_ChangeNonExistentUserPassword_ShouldThrowServiceException() {
        applicationUserService.changeUserPassword("test", "test");
    }

}