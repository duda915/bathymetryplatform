package com.mdud.bathymetryplatform.user;

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
        ApplicationUser applicationUser = applicationUserService.getApplicationUser("read");
        assertNotNull(applicationUser);
    }

    @Test(expected = ApplicationUserServiceException.class)
    public void getApplicationUser_GetNonExistingUser_ShouldThrowServiceException() {
        applicationUserService.getApplicationUser("test");
    }

    @Test
    public void addNewUser_AddNewUser_ShouldPersistNewUser() {
        ApplicationUser applicationUser = applicationUserService.addNewUser("test", "test");

        boolean act = applicationUser.getId() != null
                && applicationUser.getUserAuthorities().stream().allMatch(userAuthority -> userAuthority.getId() != null)
                && applicationUser.getUserAuthorities().size() == 2;
        assertTrue(act);
    }

    @Test(expected = ApplicationUserServiceException.class)
    public void addNewUser_AddUserWithExistingUsername_ShouldThrowServiceException() {
        applicationUserService.addNewUser("read", "test");
    }

    @Test
    public void changeUserPassword_ChangeExistingUserPassword_ShouldChangeUserPassword() {
        ApplicationUser applicationUser = applicationUserService.changeUserPassword("read", "newpass");
        ApplicationUser newPass = applicationUserService.getApplicationUser("read");

        boolean act = ApplicationUser.PASSWORD_ENCODER.matches("newpass", newPass.getPassword());

        assertTrue(act);
    }

    @Test(expected = ApplicationUserServiceException.class)
    public void changeUserPassword_ChangePasswordToTheSamePassword_ShouldThrowServiceException() {
        applicationUserService.changeUserPassword("read", "read");
    }

    @Test(expected = ApplicationUserServiceException.class)
    public void changeUserPassword_ChangeNonExistentUserPassword_ShouldThrowServiceException() {
        applicationUserService.changeUserPassword("test", "test");
    }



}