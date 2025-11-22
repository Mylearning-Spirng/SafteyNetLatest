package com.openclassroom.safteynetalertsrefactor.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassroom.safteynetalertsrefactor.model.FireStation;
import com.openclassroom.safteynetalertsrefactor.service.FireStationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

class FirestationControllerTest {

    private MockMvc mockMvc;
    private FireStationService firestationService;
    private FireStationController firestationController;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        firestationService = Mockito.mock(FireStationService.class);
        firestationController = new FireStationController(firestationService);
        mockMvc = MockMvcBuilders.standaloneSetup(firestationController).build();
        objectMapper = new ObjectMapper();
    }

    private FireStation sampleFirestation() {
        FireStation f = new FireStation();
        f.setAddress("1509CulverSt");
        f.setStation(3);
        return f;
    }

    @Test
    void getAllFirestations_shouldReturnList() throws Exception {
        var fs = sampleFirestation();
        Mockito.when(firestationService.getAllFireStations()).thenReturn(List.of(fs));

        MvcResult result = mockMvc.perform(get("/firestations"))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
        String content = result.getResponse().getContentAsString();
        List<FireStation> stations = objectMapper.readValue(content, new TypeReference<List<FireStation>>() {});
        assertEquals(1, stations.size());
        assertEquals("1509CulverSt", stations.get(0).getAddress());
        assertEquals(3, stations.get(0).getStation());

        verify(firestationService, times(1)).getAllFireStations();
    }

    @Test
    void addFirestation_shouldReturnFirestation() throws Exception {
        var fs = sampleFirestation();
        Mockito.when(firestationService.addFireStation(any(FireStation.class))).thenReturn(fs);

        MvcResult result = mockMvc.perform(post("/firestations")
                        .content(objectMapper.writeValueAsString(fs))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
        String content = result.getResponse().getContentAsString();
        FireStation returned = objectMapper.readValue(content, FireStation.class);
        assertEquals("1509CulverSt", returned.getAddress());
        assertEquals(3, returned.getStation());

        verify(firestationService, times(1)).addFireStation(any(FireStation.class));
    }

    @Test
    void updateFirestation_shouldReturnOk() throws Exception {
        var updated = sampleFirestation();
        updated.setStation(4);

        Mockito.when(firestationService.updateFireStation(eq("1509CulverSt"), any(FireStation.class))).thenReturn(true);

        MvcResult result = mockMvc.perform(put("/firestations/1509CulverSt")
                        .content(objectMapper.writeValueAsString(updated))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
        String content = result.getResponse().getContentAsString();
        Boolean ok = objectMapper.readValue(content, Boolean.class);
        assertTrue(ok);

        verify(firestationService, times(1)).updateFireStation(eq("1509CulverSt"), any(FireStation.class));
    }

    @Test
    @Disabled
    void deleteFirestation_shouldReturnOk() throws Exception {
        Mockito.when(firestationService.deleteByAddress("1509CulverSt")).thenReturn(true);

        MvcResult result = mockMvc.perform(delete("/firestation")
                        .param("address", "1509CulverSt"))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
        String content = result.getResponse().getContentAsString();
        Boolean deleted = objectMapper.readValue(content, Boolean.class);
        assertTrue(deleted);

        verify(firestationService, times(1)).deleteByAddress("1509CulverSt");
    }
}
