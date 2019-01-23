package com.mdud.bathymetryplatform.user;

import com.mdud.bathymetryplatform.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ApplicationUserRepositoryTest {
    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Test
    public void findUserByUserName_AddUserThenFindUser_ShouldReturnSameUser() {
        HashSet<UserAuthority> userAuthorityHashSet = new HashSet<>();
        ApplicationUser applicationUser = new ApplicationUser("test", "test", userAuthorityHashSet);

        applicationUser = applicationUserRepository.save(applicationUser);
        ApplicationUser newUser = applicationUserRepository.findByUsername(applicationUser.getUsername()).orElse(null);

        assertEquals(applicationUser, newUser);
    }
}