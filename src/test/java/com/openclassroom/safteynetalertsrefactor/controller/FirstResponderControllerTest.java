package com.openclassroom.safteynetalertsrefactor.controller;

import com.openclassroom.safteynetalertsrefactor.DTO.FireStationCoverageDto;
import com.openclassroom.safteynetalertsrefactor.service.FirstResponderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class FirstResponderControllerTest {

    @Mock
    private FirstResponderService service;

    @InjectMocks
    private FirstResponderController controller;

    @Test
    void getFirestation_returnsDto_and_callsService() {
        int stationNumber = 2;
        FireStationCoverageDto dto = Mockito.mock(FireStationCoverageDto.class);

        Mockito.when(service.getPersonsByStation(stationNumber)).thenReturn(dto);

        FireStationCoverageDto result = controller.getFirestation(stationNumber);

        assertSame(dto, result);
        verify(service, times(1)).getPersonsByStation(stationNumber);
    }
}