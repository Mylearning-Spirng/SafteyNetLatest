package com.openclassroom.safteynetalertsrefactor.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassroom.safteynetalertsrefactor.model.MedicalRecord;
import com.openclassroom.safteynetalertsrefactor.service.MedicalRecordsService;
import org.junit.jupiter.api.BeforeEach;
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

class MedicalRecordControllerTest {

    private MockMvc mockMvc;
    private MedicalRecordsService medicalRecordsService;
    private MedicalRecordController medicalRecordController;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        medicalRecordsService = Mockito.mock(MedicalRecordsService.class);
        medicalRecordController = new MedicalRecordController(medicalRecordsService);
        mockMvc = MockMvcBuilders.standaloneSetup(medicalRecordController).build();
        objectMapper = new ObjectMapper();
    }

    private MedicalRecord sampleRecord() {
        MedicalRecord r = new MedicalRecord();
        r.setFirstName("John");
        r.setLastName("Doe");
        r.setBirthdate("01/01/1980");
        r.setMedications(List.of("med1:10mg"));
        r.setAllergies(List.of("peanut"));
        return r;
    }

    @Test
    void getAllMedicalRecords_shouldReturnList() throws Exception {
        var record = sampleRecord();
        Mockito.when(medicalRecordsService.getAllMedicalRecords()).thenReturn(List.of(record));

        MvcResult result = mockMvc.perform(get("/medicalRecords"))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
        String content = result.getResponse().getContentAsString();
        List<MedicalRecord> records = objectMapper.readValue(content, new TypeReference<List<MedicalRecord>>() {
        });
        assertEquals(1, records.size());
        assertEquals("John", records.get(0).getFirstName());
        assertEquals("Doe", records.get(0).getLastName());

        verify(medicalRecordsService, times(1)).getAllMedicalRecords();
    }

    @Test
    void addMedicalRecords_shouldReturnRecord() throws Exception {
        var record = sampleRecord();
        Mockito.when(medicalRecordsService.addMedicalRecords(any(MedicalRecord.class))).thenReturn(record);

        MvcResult result = mockMvc.perform(post("/medicalRecords")
                        .content(objectMapper.writeValueAsString(record))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
        String content = result.getResponse().getContentAsString();
        MedicalRecord returned = objectMapper.readValue(content, MedicalRecord.class);
        assertEquals("John", returned.getFirstName());
        assertEquals("peanut", returned.getAllergies().get(0));

        verify(medicalRecordsService, times(1)).addMedicalRecords(any(MedicalRecord.class));
    }

    @Test
    void updateMedicalRecord_shouldReturnOk() throws Exception {
        var updated = sampleRecord();
        updated.setBirthdate("02/02/1980");

        Mockito.when(medicalRecordsService.updateMedicalRecord(eq("John"), eq("Doe"), any(MedicalRecord.class))).thenReturn(true);

        MvcResult result = mockMvc.perform(put("/medicalRecords/Doe/John")
                        .content(objectMapper.writeValueAsString(updated))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
        String content = result.getResponse().getContentAsString();
        Boolean ok = objectMapper.readValue(content, Boolean.class);
        assertTrue(ok);

        verify(medicalRecordsService, times(1)).updateMedicalRecord(eq("John"), eq("Doe"), any(MedicalRecord.class));
    }

    @Test
    void deleteMedicalRecord_shouldReturnOk() throws Exception {
        Mockito.when(medicalRecordsService.deleteMedicalRecord("John", "Doe")).thenReturn(true);

        MvcResult result = mockMvc.perform(delete("/medicalRecords/Doe/John"))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
        String content = result.getResponse().getContentAsString();
        Boolean deleted = objectMapper.readValue(content, Boolean.class);
        assertTrue(deleted);

        verify(medicalRecordsService, times(1)).deleteMedicalRecord("John", "Doe");
    }
}
