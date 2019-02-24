package com.mdud.bathymetryplatform.user;

import com.mdud.bathymetryplatform.exception.UserAlreadyExistsException;
import com.mdud.bathymetryplatform.exception.UserNotFoundException;
import com.mdud.bathymetryplatform.user.authority.Authorities;
import com.mdud.bathymetryplatform.user.authority.Authority;
import com.mdud.bathymetryplatform.user.userauthority.UserAuthority;
import com.mdud.bathymetryplatform.user.userauthority.UserAuthorityProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationUserServiceTest {

    @InjectMocks
    private ApplicationUserService applicationUserService;

    @Mock
    private ApplicationUserRepository applicationUserRepository;

    @Mock
    private UserAuthorityProvider authorityProvider;

    private ApplicationUser applicationUser;

    @Before
    public void before() {
        ApplicationUserDTO applicationUserDTO = new ApplicationUserDTO("user", "user", "user@mail.com");
        applicationUser = new ApplicationUser(applicationUserDTO);
    }

    @Test
    public void getApplicationUser_GetExistentApplicationUser_ShouldReturnApplicationUser() {
        when(applicationUserRepository.findByUsername(applicationUser.getUsername())).thenReturn(Optional.of(applicationUser));

        ApplicationUser getUser = applicationUserService.getApplicationUser("user");

        assertEquals(applicationUser, getUser);
        verify(applicationUserRepository, times(1)).findByUsername(applicationUser.getUsername());
    }

    @Test(expected = UserNotFoundException.class)
    public void getApplicationUser_GetNonExistentApplicationUser_ShouldThrowException() {
        when(applicationUserRepository.findByUsername("test")).thenReturn(Optional.empty());
        applicationUserService.getApplicationUser("test");

        verify(applicationUserRepository, times(1)).findByUsername("test");
    }

    @Test
    public void addNewUser_AddNewUser_ShouldAddNewUser() {
        when(applicationUserRepository.save(any(ApplicationUser.class)))
                .thenAnswer((Answer<ApplicationUser>) invocationOnMock -> (ApplicationUser) invocationOnMock.getArguments()[0]);

        ApplicationUserDTO applicationUserDTO = new ApplicationUserDTO("user", "user", "user@mail.com");
        ApplicationUser applicationUser = applicationUserService.addNewUser(applicationUserDTO);

        assertNotNull(applicationUser.getUserAuthorities());

        verify(applicationUserRepository, times(1)).save(applicationUser);
        verify(authorityProvider, times(1)).getUserAuthority(Authorities.READ);
        verify(authorityProvider, times(1)).getUserAuthority(Authorities.WRITE);
    }

    @Test(expected = UserAlreadyExistsException.class)
    public void addNewUser_TryAddUserWithExistentUsername_ShouldThrowException() {
        when(applicationUserRepository.findByUsername("newuser")).thenReturn(Optional.of(new ApplicationUser()));

        ApplicationUserDTO applicationUserDTO = new ApplicationUserDTO("newuser", "", "");

        applicationUserService.addNewUser(applicationUserDTO);
    }

    @Test(expected = UserAlreadyExistsException.class)
    public void addNewUser_TryAddUserWithExistentEmail_ShouldThrowException() {
        when(applicationUserRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(applicationUserRepository.findByEmail("newmail")).thenReturn(Optional.of(new ApplicationUser()));

        ApplicationUserDTO applicationUserDTO = new ApplicationUserDTO("newuser", "", "newmail");

        applicationUserService.addNewUser(applicationUserDTO);
    }


    @Test
    public void changeUserPassword_ChangeExistentUserPassword_ShouldChangePassword() {
        when(applicationUserRepository.save(any(ApplicationUser.class)))
                .thenAnswer((Answer<ApplicationUser>) invocationOnMock ->
                        invocationOnMock.getArgument(0)
                );

        when(applicationUserRepository.findByUsername(applicationUser.getUsername())).thenReturn(Optional.of(applicationUser));

        ApplicationUser newuser = applicationUserService.changeUserPassword(applicationUser.getUsername(), "newpassword");

        assertTrue(ApplicationUser.PASSWORD_ENCODER.matches("newpassword", newuser.getPassword()));
        verify(applicationUserRepository, times(1)).save(applicationUser);
        verify(applicationUserRepository, atLeastOnce()).findByUsername(applicationUser.getUsername());
    }

    @Test(expected = UserNotFoundException.class)
    public void changeUserPassword_ChangeNonExitentUserPassword_ShouldThrowException() {
        when(applicationUserRepository.findByUsername("test")).thenReturn(Optional.empty());
        applicationUserService.changeUserPassword("test", "");
    }

    @Test
    public void checkUserAuthority_AuthorityExists_ShouldReturnTrue() {
        HashSet<UserAuthority> authorities = new HashSet<>();
        authorities.add(new UserAuthority(new Authority(Authorities.READ)));
        applicationUser.setUserAuthorities(authorities);

        when(applicationUserRepository.findByUsername(applicationUser.getUsername())).thenReturn(Optional.of(applicationUser));

        boolean act = applicationUserService.checkUserAuthority(applicationUser.getUsername(), Authorities.READ);

        assertTrue(act);
    }

    @Test
    public void checkUserAuthority_AuthorityNotExists_ShouldReturnFalse() {
        applicationUser.setUserAuthorities(new HashSet<>());
        when(applicationUserRepository.findByUsername(applicationUser.getUsername())).thenReturn(Optional.of(applicationUser));

        boolean act = applicationUserService.checkUserAuthority(applicationUser.getUsername(), Authorities.READ);

        assertFalse(act);
    }

    @Test
    public void activateUser() {
        when(applicationUserRepository.findByUsername(applicationUser.getUsername())).thenReturn(Optional.of(applicationUser));
        when(applicationUserRepository.save(applicationUser))
                .thenAnswer((Answer<ApplicationUser>) invocationOnMock -> invocationOnMock.getArgument(0));

        ApplicationUser newuser = applicationUserService.activateUser(applicationUser.getUsername());

        assertTrue(newuser.isActive());
        verify(applicationUserRepository, times(1)).save(newuser);
    }
}