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
        ApplicationUser applicationUser = applicationUserService.getApplicationUser("read");
        assertNotNull(applicationUser);
    }

    @Test(expected = UserNotFoundException.class)
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

    @Test(expected = UserAlreadyExistsException.class)
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

    @Test(expected = UserException.class)
    public void changeUserPassword_ChangePasswordToTheSamePassword_ShouldThrowServiceException() {
        applicationUserService.changeUserPassword("read", "read");
    }

    @Test(expected = UserNotFoundException.class)
    public void changeUserPassword_ChangeNonExistentUserPassword_ShouldThrowServiceException() {
        applicationUserService.changeUserPassword("test", "test");
    }

    @Test
    public void addNewAuthority_AddNewAuthorityToUser_ShouldAddNewAuthority() {
        applicationUserService.addNewAuthority("read", Authorities.ADMIN);
        ApplicationUser applicationUser = applicationUserService.getApplicationUser("read");

        assertEquals(2, applicationUser.getUserAuthorities().size());
    }

    @Test(expected = UserException.class)
    public void addNewAuthority_AddExistingAuthority_ShouldThrowServiceException() {
        applicationUserService.addNewAuthority("read", Authorities.READ);
    }

    @Test(expected = UserNotFoundException.class)
    public void addNewAuthority_AddAuthorityToNonExistentUser_ShouldThrowServiceException() {
        applicationUserService.addNewAuthority("test", Authorities.READ);
    }

    @Test
    public void removeAuthority_RemoveUserAuthority_ShouldRemoveUserAuthority() {
        applicationUserService.removeUserAuthority("read", Authorities.READ);
        ApplicationUser applicationUser = applicationUserService.getApplicationUser("read");

        assertEquals(0, applicationUser.getUserAuthorities().size());
    }

    @Test(expected = UserNotFoundException.class)
    public void removeAuthority_RemoveNonExistingUserAuthority_ShouldThrowServiceException(){
        applicationUserService.removeUserAuthority("test", Authorities.READ);
    }

    @Test(expected = UserNotFoundException.class)
    public void removeAuthority_RemoveAuthorityFromUserWhichDoNotHaveThisAuthority_ShouldThrowServiceException() {
        applicationUserService.removeUserAuthority("rest", Authorities.ADMIN);
    }

    @Test(expected = UserNotFoundException.class)
    public void removeUser_RemoveExistingUser_ShouldRemoveUser() {
        applicationUserService.removeUser("read");
        applicationUserService.getApplicationUser("read");
    }

    @Test(expected = UserNotFoundException.class)
    public void removeUser_RemoveNonExistingUser_ShouldThrowServiceException() {
        applicationUserService.removeUser("test");
    }

}