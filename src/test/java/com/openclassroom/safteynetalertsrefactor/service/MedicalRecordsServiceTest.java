package com.openclassroom.safteynetalertsrefactor.service;

import com.openclassroom.safteynetalertsrefactor.model.MedicalRecord;
import com.openclassroom.safteynetalertsrefactor.repository.MedicalRecordsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MedicalRecordsServiceTest {

    private MedicalRecordsRepository medicalRecordsRepository;
    private MedicalRecordsService medicalRecordsService;

    private MedicalRecord sampleRecord() {
        MedicalRecord mr = new MedicalRecord();
        mr.setFirstName("John");
        mr.setLastName("Doe");
        mr.setBirthdate("01/01/2000");
        return mr;
    }

    @BeforeEach
    void setUp() {
        medicalRecordsRepository = Mockito.mock(MedicalRecordsRepository.class);
        medicalRecordsService = new MedicalRecordsService(medicalRecordsRepository);
    }

    @Test
    void getAllMedicalRecords_shouldDelegateToRepository() {
        when(medicalRecordsRepository.findAll()).thenReturn(List.of(sampleRecord()));

        List<MedicalRecord> result = medicalRecordsService.getAllMedicalRecords();

        assertEquals(1, result.size());
        verify(medicalRecordsRepository, times(1)).findAll();
    }

    @Test
    void addMedicalRecords_shouldCallRepositoryAndReturnRecord() {
        MedicalRecord mr = sampleRecord();

        MedicalRecord returned = medicalRecordsService.addMedicalRecords(mr);

        assertEquals(mr, returned);
        verify(medicalRecordsRepository, times(1)).add(mr);
    }

    @Test
    void updateMedicalRecord_shouldReturnRepositoryResult() {
        when(medicalRecordsRepository.updateMedicalRecord(
                eq("John"), eq("Doe"), any(MedicalRecord.class)))
                .thenReturn(true);

        boolean ok = medicalRecordsService.updateMedicalRecord("John", "Doe", sampleRecord());

        assertTrue(ok);
        verify(medicalRecordsRepository, times(1))
                .updateMedicalRecord(eq("John"), eq("Doe"), any(MedicalRecord.class));
    }

    @Test
    void deleteMedicalRecord_shouldDelegateToRepository() {
        when(medicalRecordsRepository.deleteByName("John", "Doe")).thenReturn(true);

        boolean deleted = medicalRecordsService.deleteMedicalRecord("John", "Doe");

        assertTrue(deleted);
        verify(medicalRecordsRepository, times(1))
                .deleteByName("John", "Doe");
    }
}