package com.mdud.bathymetryplatform.user;

import com.mdud.bathymetryplatform.user.authority.Authorities;
import com.mdud.bathymetryplatform.user.userauthority.UserAuthority;
import com.mdud.bathymetryplatform.user.userauthority.UserAuthorityProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ApplicationUserMappingTest {

    @Autowired
    private UserAuthorityProvider userAuthorityProvider;

    @Autowired
    private ApplicationUserRepository userRepository;

    @Test
    public void save_SaveUserWithoutAuthorities_ShouldSaveUserWithoutAuthorities() {
        Set<UserAuthority> userAuthoritySet = new HashSet<>();
        ApplicationUser applicationUser = new ApplicationUser("test", "test", userAuthoritySet);
        applicationUser = userRepository.save(applicationUser);

        boolean act = applicationUser.getId() != null &&
                applicationUser.getUserAuthorities().size() == 0;
        Assert.assertTrue(act);
    }

    @Test
    public void save_SaveUserWithThreeAuthorities_ShouldSaveUserAndAuthorities() {
        HashSet<UserAuthority> userAuthoritySet = new HashSet<>();
        Arrays.asList(Authorities.values()).forEach(authorityName -> {
            userAuthoritySet.add(userAuthorityProvider.getUserAuthority(authorityName));
        });

        ApplicationUser applicationUser = new ApplicationUser("test", "test", userAuthoritySet);

        applicationUser = userRepository.save(applicationUser);

        boolean act = applicationUser.getId() != null
                && applicationUser.getUserAuthorities().stream().allMatch(auth -> auth.getId() != null);

        Assert.assertTrue(act);

    }

}
