package com.openclassroom.safteynetalertsrefactor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassroom.safteynetalertsrefactor.dto.ChildResidentDto;
import com.openclassroom.safteynetalertsrefactor.dto.FirstResponderDto;
import com.openclassroom.safteynetalertsrefactor.dto.HouseholdDto;
import com.openclassroom.safteynetalertsrefactor.dto.ResidentDto;
import com.openclassroom.safteynetalertsrefactor.service.FirstResponderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class FirstResponderControllerTest {

    private MockMvc mockMvc;
    private FirstResponderService service;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        service = Mockito.mock(FirstResponderService.class);
        FirstResponderController controller = new FirstResponderController(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getFirestation_returnsDto_and_callsService() throws Exception {
        int stationNumber = 2;
        FirstResponderDto dto = new FirstResponderDto();

        when(service.getPersonsByStation(stationNumber)).thenReturn(dto);

        mockMvc.perform(get("/firestation")
                        .param("stationNumber", String.valueOf(stationNumber)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));

        verify(service, times(1)).getPersonsByStation(stationNumber);
    }

    @Test
    void getChildAlert_returnsList_and_callsService() throws Exception {
        String address = "1509 Culver St";
        List<ChildResidentDto> dtoList = List.of(new ChildResidentDto());

        when(service.getChildrenByAddress(address)).thenReturn(dtoList);

        mockMvc.perform(get("/childAlert")
                        .param("address", address))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtoList)));

        verify(service, times(1)).getChildrenByAddress(address);
    }

    @Test
    void getPhoneAlert_returnsList_and_callsService() throws Exception {
        int stationNumber = 3;
        List<String> phones = List.of("555-1234");

        when(service.getPhoneAlert(stationNumber)).thenReturn(phones);

        mockMvc.perform(get("/phoneAlert")
                        .param("firestation", String.valueOf(stationNumber)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(phones)));

        verify(service, times(1)).getPhoneAlert(stationNumber);
    }

    @Test
    void getFire_returnsList_and_callsService() throws Exception {
        String address = "29 15th St";
        List<ResidentDto> residents = List.of(new ResidentDto());

        when(service.getFireInfo(address)).thenReturn(residents);

        mockMvc.perform(get("/fire")
                        .param("address", address))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(residents)));

        verify(service, times(1)).getFireInfo(address);
    }

    @Test
    void getCommunityEmail_returnsList_and_callsService() throws Exception {
        String city = "Culver";
        List<String> emails = List.of("a@example.com");

        when(service.getCommunityEmail(city)).thenReturn(emails);

        mockMvc.perform(get("/communityEmail")
                        .param("city", city))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(emails)));

        verify(service, times(1)).getCommunityEmail(city);
    }

    @Test
    void getPersonInfoByLastName_returnsList_and_callsService() throws Exception {
        String lastName = "Doe";
        List<ResidentDto> residents = List.of(new ResidentDto());

        when(service.getResidentsByLastName(lastName)).thenReturn(residents);

        mockMvc.perform(get("/personInfo").param("lastName", "Doe"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(residents)));

        verify(service, times(1)).getResidentsByLastName(lastName);
    }

    @Test
    void getFloodInfo_returnsResponse_and_callsService() throws Exception {
        List<String> stations = List.of("1", "2");
        List<HouseholdDto> floodData = List.of(new HouseholdDto());

        when(service.getFloodInfo(stations)).thenReturn(floodData);

        mockMvc.perform(get("/flood/stations")
                        .param("stations", "1", "2"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(floodData)));

        verify(service, times(1)).getFloodInfo(stations);
    }
}
