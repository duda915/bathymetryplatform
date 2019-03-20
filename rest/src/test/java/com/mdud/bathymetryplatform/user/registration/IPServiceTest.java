package com.mdud.bathymetryplatform.user.registration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class IPServiceTest {

    @InjectMocks
    private IPService ipService;

    @Test
    public void getExternalIp() {
        System.out.println(ipService.getExternalIp());
    }
}