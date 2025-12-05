package com.openclassroom.safteynetalertsrefactor.service;

import com.openclassroom.safteynetalertsrefactor.model.FireStation;
import com.openclassroom.safteynetalertsrefactor.repository.FireStationRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FireStationServiceTest {

    private FireStationRepository fireStationRepository;
    private FireStationService fireStationService;

    private FireStation sample() {
        FireStation fs = new FireStation();
        fs.setAddress("5665 Laurel pine rd");
        fs.setStation(5);
        return fs;
    }

    @BeforeEach
    void setup() {
        fireStationRepository = Mockito.mock(FireStationRepository.class);
        fireStationService = new FireStationService(fireStationRepository);
    }

    @Test
    void getAllFireStations_shouldDelegateToRepository() {
        when(fireStationRepository.findAll()).thenReturn(List.of(sample()));

        List<FireStation> result = fireStationService.getAllFireStations();

        assertEquals(1, result.size());
        verify(fireStationRepository, times(1)).findAll();
    }

    @Test
    void addFireStation_shouldCallRepository() {
        FireStation fs = sample();
        fireStationService.addFireStation(fs);

        verify(fireStationRepository, times(1)).add(fs);
    }

    @Test
    void updateFireStation_shouldReturnTrueIfUpdated() {
        when(fireStationRepository.updateFireStation("ABC", 9))
                .thenReturn(true);

        FireStation updated = sample();
        updated.setStation(9);

        boolean result = fireStationService.updateFireStation("ABC", updated);

        assertTrue(result);
        verify(fireStationRepository, times(1))
                .updateFireStation("ABC", 9);
    }

    @Test
    void deleteByAddress_shouldDelegate() {
        when(fireStationRepository.deleteByAddress("ABC")).thenReturn(true);

        assertTrue(fireStationService.deleteByAddress("ABC"));
        verify(fireStationRepository, times(1)).deleteByAddress("ABC");
    }

    @Test
    void deleteByStationNumber_shouldCallRepository() {
        when(fireStationRepository.deleteByStationNumber(5)).thenReturn(true);

        assertTrue(fireStationService.deleteByStationNumber(5));
        verify(fireStationRepository, times(1))
                .deleteByStationNumber(5);
    }
}