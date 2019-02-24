package com.mdud.bathymetryplatform.bathymetry;

import com.mdud.bathymetryplatform.exception.ResourceNotFoundException;
import com.mdud.bathymetryplatform.user.ApplicationUser;
import com.mdud.bathymetryplatform.user.ApplicationUserDTO;
import com.mdud.bathymetryplatform.user.ApplicationUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BathymetryDataSetServiceTest {

    @InjectMocks
    private BathymetryDataSetService bathymetryDataSetService;

    @Mock
    private BathymetryDataSetRepository bathymetryDataSetRepository;

    @Mock
    private ApplicationUserService applicationUserService;

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
        ApplicationUserDTO applicationUserDTO = new ApplicationUserDTO("", "", "");
        ApplicationUser applicationUser = new ApplicationUser(applicationUserDTO);

        when(bathymetryDataSetRepository.findAllByApplicationUser(applicationUser)).thenReturn(Optional.of(new ArrayList<>()));
        when(applicationUserService.getApplicationUser("")).thenReturn(applicationUser);

        bathymetryDataSetService.getDataSetsByUser("");

        verify(bathymetryDataSetRepository, times(1)).findAllByApplicationUser(applicationUser);
        verify(applicationUserService, times(1)).getApplicationUser("");
    }

}