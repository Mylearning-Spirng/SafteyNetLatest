package com.openclassroom.safteynetalertsrefactor.controller;

import com.openclassroom.safteynetalertsrefactor.dto.ChildResidentDto;
import com.openclassroom.safteynetalertsrefactor.dto.FirstResponderDto;
import com.openclassroom.safteynetalertsrefactor.dto.ResidentDto;
import com.openclassroom.safteynetalertsrefactor.service.FirstResponderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

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
        FirstResponderDto dto = Mockito.mock(FirstResponderDto.class);

        Mockito.when(service.getPersonsByStation(stationNumber)).thenReturn(dto);

        FirstResponderDto result = controller.getFirestation(stationNumber);

        assertSame(dto, result);
        verify(service, times(1)).getPersonsByStation(stationNumber);
    }

    @Test
    void getChildAlert_returnsList_and_callsService() {
        String address = "1509 Culver St";
        List<ChildResidentDto> dtoList = List.of(Mockito.mock(ChildResidentDto.class));

        Mockito.when(service.getChildrenByAddress(address)).thenReturn(dtoList);

        List<ChildResidentDto> result = controller.getChildAlert(address);

        assertSame(dtoList, result);
        verify(service, times(1)).getChildrenByAddress(address);
    }

    @Test
    void getPhoneAlert_returnsList_and_callsService() {
        int stationNumber = 3;
        List<String> phones = List.of("555-1234");

        Mockito.when(service.getPhoneAlert(stationNumber)).thenReturn(phones);

        List<String> result = controller.getPhoneAlert(stationNumber);

        assertSame(phones, result);
        verify(service, times(1)).getPhoneAlert(stationNumber);
    }

    @Test
    void getFire_returnsList_and_callsService() {
        String address = "29 15th St";
        List<ResidentDto> residents = List.of(Mockito.mock(ResidentDto.class));

        Mockito.when(service.getFireInfo(address)).thenReturn(residents);

        List<ResidentDto> result = controller.getFire(address);

        assertSame(residents, result);
        verify(service, times(1)).getFireInfo(address);
    }

    @Test
    void getCommunityEmail_returnsList_and_callsService() {
        String city = "Culver";
        List<String> emails = List.of("a@example.com");

        Mockito.when(service.getCommunityEmail(city)).thenReturn(emails);

        List<String> result = controller.getCommunityEmail(city);

        assertSame(emails, result);
        verify(service, times(1)).getCommunityEmail(city);
    }

    @Test
    void getPersonInfoByLastName_returnsList_and_callsService() {
        String lastName = "Doe";
        List<ResidentDto> residents = List.of(Mockito.mock(ResidentDto.class));

        Mockito.when(service.getResidentsByLastName(lastName)).thenReturn(residents);

        List<ResidentDto> result = controller.getPersonInfoByLastName(lastName);

        assertSame(residents, result);
        verify(service, times(1)).getResidentsByLastName(lastName);
    }

    @Test
    void getFloodInfo_returnsResponse_and_callsService() {
        List<String> stations = List.of("1", "2");
        List<Object> floodData = List.of(Map.of("station", "1", "residents", List.of()));

        Mockito.when(service.getFloodInfo(stations)).thenReturn(floodData);

        ResponseEntity<List<Object>> response = controller.getFloodInfo(stations);

        assertSame(floodData, response.getBody());
        verify(service, times(1)).getFloodInfo(stations);
    }

}
