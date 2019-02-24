package com.mdud.bathymetryplatform.bathymetry;

import com.mdud.bathymetryplatform.exception.DataParsingException;
import com.mdud.bathymetryplatform.exception.ResourceNotFoundException;
import com.mdud.bathymetryplatform.user.ApplicationUser;
import com.mdud.bathymetryplatform.user.ApplicationUserDTO;
import com.mdud.bathymetryplatform.user.ApplicationUserService;
import com.mdud.bathymetryplatform.utility.SQLDateBuilder;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class BathymetryDataSetServiceTest {

    @InjectMocks
    private BathymetryDataSetService bathymetryDataSetService;

    @Mock
    private BathymetryDataSetRepository bathymetryDataSetRepository;

    @Mock
    private ApplicationUserService applicationUserService;

    @Value(value = "classpath:testdata/testdata.xyz")
    private Resource resource;

    @Value(value = "classpath:testdata/invalidtestdata.xyz")
    private Resource invalidDataResource;


    private ApplicationUserDTO applicationUserDTO = new ApplicationUserDTO("", "", "");
    private ApplicationUser applicationUser = new ApplicationUser(applicationUserDTO);

    @Before
    public void setup() {
        applicationUserDTO = new ApplicationUserDTO("", "", "");
        applicationUser = new ApplicationUser(applicationUserDTO);
    }

    @Test
    public void getDataSet_GetExistentDataSet_ShouldReturnDataset() {
        when(bathymetryDataSetRepository.findById(1L)).thenReturn(Optional.of(new BathymetryDataSet()));

        BathymetryDataSet bathymetryDataSet = bathymetryDataSetService.getDataSet(1L);

        assertNotNull(bathymetryDataSet);
        verify(bathymetryDataSetRepository, times(1)).findById(1L);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getDataSet_GetNonExistentDataSet_ShouldThrowException() {
        when(bathymetryDataSetRepository.findById(1L)).thenReturn(Optional.empty());
        bathymetryDataSetService.getDataSet(1L);
    }

    @Test
    public void getAllDataSets() {
        List<BathymetryDataSet> bathymetryDataSetList = new ArrayList<>();
        bathymetryDataSetList.add(new BathymetryDataSet());
        when(bathymetryDataSetRepository.findAll()).thenReturn(bathymetryDataSetList);
        bathymetryDataSetService.getAllDataSets();

        verify(bathymetryDataSetRepository, times(1)).findAll();
    }

    @Test
    public void getDataSetsByUser() {
        when(bathymetryDataSetRepository.findAllByApplicationUser(applicationUser)).thenReturn(Optional.of(new ArrayList<>()));
        when(applicationUserService.getApplicationUser("")).thenReturn(applicationUser);

        bathymetryDataSetService.getDataSetsByUser("");

        verify(bathymetryDataSetRepository, times(1)).findAllByApplicationUser(applicationUser);
        verify(applicationUserService, times(1)).getApplicationUser("");
    }

    @Test
    public void addData_AddValidData_ShouldAddData() throws IOException {
        ApplicationUserDTO applicationUserDTO = new ApplicationUserDTO("user", "user", "user");
        ApplicationUser applicationUser = new ApplicationUser(applicationUserDTO);
        BathymetryDataSetDTO bathymetryDataSetDTO = new BathymetryDataSetDTO(applicationUser, 4326, "newdata",
                SQLDateBuilder.now(), "me");
        byte[] file = IOUtils.toByteArray(resource.getURI());

        when(bathymetryDataSetRepository.nativeSave(any(BathymetryDataSet.class)))
                .thenAnswer((Answer<BathymetryDataSet>) answer -> answer.getArgument(0));

        BathymetryDataSet bathymetryDataSet = bathymetryDataSetService.addDataSet(bathymetryDataSetDTO, file);

        assertEquals(240, bathymetryDataSet.getMeasurements().size());
        verify(bathymetryDataSetRepository, times(1)).nativeSave(bathymetryDataSet);
    }

    @Test(expected = DataParsingException.class)
    public void addData_AddInvalidData_ShouldThrowException() throws IOException {
        ApplicationUserDTO applicationUserDTO = new ApplicationUserDTO("user", "user", "user");
        ApplicationUser applicationUser = new ApplicationUser(applicationUserDTO);
        BathymetryDataSetDTO bathymetryDataSetDTO = new BathymetryDataSetDTO(applicationUser, 4326, "newdata",
                SQLDateBuilder.now(), "me");
        byte[] file = IOUtils.toByteArray(invalidDataResource.getURI());

        bathymetryDataSetService.addDataSet(bathymetryDataSetDTO, file);
    }
}