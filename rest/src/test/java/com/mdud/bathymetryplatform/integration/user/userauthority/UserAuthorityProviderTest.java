package com.mdud.bathymetryplatform.integration.user.userauthority;

import com.mdud.bathymetryplatform.user.authority.Authorities;
import com.mdud.bathymetryplatform.user.authority.Authority;
import com.mdud.bathymetryplatform.user.authority.AuthorityRepository;
import com.mdud.bathymetryplatform.user.userauthority.UserAuthority;
import com.mdud.bathymetryplatform.user.userauthority.UserAuthorityProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserAuthorityProviderTest {

    @InjectMocks
    private UserAuthorityProvider userAuthorityProvider;

    @Mock
    public AuthorityRepository authorityRepository;

    @Test
    public void getUserAuthority_ShouldReturnAuthority() {
        when(authorityRepository.findAuthorityByAuthorityName(Authorities.READ)).thenReturn(new Authority(Authorities.READ));
        UserAuthority authority = userAuthorityProvider.getUserAuthority(Authorities.READ);

        assertEquals(Authorities.READ, authority.getAuthority().getAuthorityName());
        verify(authorityRepository, times(1)).findAuthorityByAuthorityName(Authorities.READ);
    }
}