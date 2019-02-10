package com.mdud.bathymetryplatform.user;

import com.mdud.bathymetryplatform.exception.UserAlreadyExistsException;
import com.mdud.bathymetryplatform.exception.UserException;
import com.mdud.bathymetryplatform.exception.UserNotFoundException;
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
    public void addNewUser_AddNewUser_ShouldPersistNewUser() {
        ApplicationUser applicationUser = applicationUserService.addNewUser("test", "test", "");

        boolean act = applicationUser.getId() != null
                && applicationUser.getUserAuthorities().stream().allMatch(userAuthority -> userAuthority.getId() != null)
                && applicationUser.getUserAuthorities().size() == 2;
        assertTrue(act);
    }

    @Test(expected = UserAlreadyExistsException.class)
    public void addNewUser_AddUserWithExistingUsername_ShouldThrowServiceException() {
        applicationUserService.addNewUser("guest", "test", "");
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

    @Test
    public void addNewAuthority_AddNewAuthorityToUser_ShouldAddNewAuthority() {
        applicationUserService.addNewAuthority("guest", Authorities.ADMIN);
        ApplicationUser applicationUser = applicationUserService.getApplicationUser("guest");

        assertEquals(2, applicationUser.getUserAuthorities().size());
    }

    @Test(expected = UserException.class)
    public void addNewAuthority_AddExistingAuthority_ShouldThrowServiceException() {
        applicationUserService.addNewAuthority("guest", Authorities.READ);
    }

    @Test(expected = UserNotFoundException.class)
    public void addNewAuthority_AddAuthorityToNonExistentUser_ShouldThrowServiceException() {
        applicationUserService.addNewAuthority("read", Authorities.READ);
    }

    @Test
    public void removeAuthority_RemoveUserAuthority_ShouldRemoveUserAuthority() {
        applicationUserService.removeUserAuthority("guest", Authorities.READ);
        ApplicationUser applicationUser = applicationUserService.getApplicationUser("guest");

        assertEquals(0, applicationUser.getUserAuthorities().size());
    }

    @Test(expected = UserNotFoundException.class)
    public void removeAuthority_RemoveNonExistingUserAuthority_ShouldThrowServiceException(){
        applicationUserService.removeUserAuthority("test", Authorities.READ);
    }

    @Test(expected = UserNotFoundException.class)
    public void removeAuthority_RemoveAuthorityFromUserWhichDoNotHaveThisAuthority_ShouldThrowServiceException() {
        applicationUserService.removeUserAuthority("read", Authorities.ADMIN);
    }

    @Test(expected = UserNotFoundException.class)
    public void removeUser_RemoveExistingUser_ShouldRemoveUser() {
        applicationUserService.removeUser("guest");
        applicationUserService.getApplicationUser("guest");
    }

    @Test(expected = UserNotFoundException.class)
    public void removeUser_RemoveNonExistingUser_ShouldThrowServiceException() {
        applicationUserService.removeUser("test");
    }

}