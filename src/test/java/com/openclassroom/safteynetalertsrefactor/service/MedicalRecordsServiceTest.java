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

    private MedicalRecord sample() {
        MedicalRecord mr = new MedicalRecord();
        mr.setFirstName("John");
        mr.setLastName("Doe");
        mr.setBirthdate("01/01/1990");
        mr.setMedications(List.of("med1"));
        mr.setAllergies(List.of("all1"));
        return mr;
    }

    @BeforeEach
    void setup() {
        medicalRecordsRepository = Mockito.mock(MedicalRecordsRepository.class);
        medicalRecordsService = new MedicalRecordsService(medicalRecordsRepository);
    }

    @Test
    void getAllMedicalRecords_shouldDelegateToRepository() {
        MedicalRecord mr = sample();
        when(medicalRecordsRepository.findAll()).thenReturn(List.of(mr));

        List<MedicalRecord> result = medicalRecordsService.getAllMedicalRecords();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
        verify(medicalRecordsRepository, times(1)).findAll();
    }

    @Test
    void getAllMedicalRecords_shouldReturnNullWhenRepositoryReturnsNull() {
        when(medicalRecordsRepository.findAll()).thenReturn(null);

        List<MedicalRecord> result = medicalRecordsService.getAllMedicalRecords();

        assertNull(result);
        verify(medicalRecordsRepository, times(1)).findAll();
    }

    @Test
    void getAllMedicalRecords_shouldPropagateExceptionFromRepository() {
        when(medicalRecordsRepository.findAll()).thenThrow(new RuntimeException("Failed"));

        Exception ex = assertThrows(RuntimeException.class, () -> medicalRecordsService.getAllMedicalRecords());
        assertEquals("Failed", ex.getMessage());
        verify(medicalRecordsRepository, times(1)).findAll();
    }

    @Test
    void addMedicalRecords_shouldCallRepositoryAndReturnObject() {
        MedicalRecord mr = sample();

        MedicalRecord returned = medicalRecordsService.addMedicalRecords(mr);

        assertSame(mr, returned);
        verify(medicalRecordsRepository, times(1)).add(mr);
    }

    @Test
    void addMedicalRecords_shouldPropagateExceptionFromRepository() {
        MedicalRecord mr = sample();
        doThrow(new RuntimeException("add-fail")).when(medicalRecordsRepository).add(mr);

        Exception ex = assertThrows(RuntimeException.class, () -> medicalRecordsService.addMedicalRecords(mr));
        assertEquals("add-fail", ex.getMessage());
        verify(medicalRecordsRepository, times(1)).add(mr);
    }

    @Test
    void updateMedicalRecord_shouldReturnTrueIfUpdated() {
        MedicalRecord mr = sample();
        when(medicalRecordsRepository.updateMedicalRecord("John", "Doe", mr)).thenReturn(true);

        boolean result = medicalRecordsService.updateMedicalRecord("John", "Doe", mr);

        assertTrue(result);
        verify(medicalRecordsRepository, times(1)).updateMedicalRecord("John", "Doe", mr);
    }

    @Test
    void updateMedicalRecord_shouldReturnFalseIfNotUpdated() {
        MedicalRecord mr = sample();
        when(medicalRecordsRepository.updateMedicalRecord("John", "Doe", mr)).thenReturn(false);

        boolean result = medicalRecordsService.updateMedicalRecord("John", "Doe", mr);

        assertFalse(result);
        verify(medicalRecordsRepository, times(1)).updateMedicalRecord("John", "Doe", mr);
    }

    @Test
    void updateMedicalRecord_shouldPropagateExceptionFromRepository() {
        MedicalRecord mr = sample();
        when(medicalRecordsRepository.updateMedicalRecord("John", "Doe", mr)).thenThrow(new RuntimeException("update-fail"));

        Exception ex = assertThrows(RuntimeException.class, () -> medicalRecordsService.updateMedicalRecord("John", "Doe", mr));
        assertEquals("update-fail", ex.getMessage());
        verify(medicalRecordsRepository, times(1)).updateMedicalRecord("John", "Doe", mr);
    }

    @Test
    void deleteMedicalRecord_shouldReturnTrueWhenDeleted() {
        when(medicalRecordsRepository.deleteByName("John", "Doe")).thenReturn(true);

        boolean result = medicalRecordsService.deleteMedicalRecord("John", "Doe");

        assertTrue(result);
        verify(medicalRecordsRepository, times(1)).deleteByName("John", "Doe");
    }

    @Test
    void deleteMedicalRecord_shouldReturnFalseWhenNotFound() {
        when(medicalRecordsRepository.deleteByName("John", "Doe")).thenReturn(false);

        boolean result = medicalRecordsService.deleteMedicalRecord("John", "Doe");

        assertFalse(result);
        verify(medicalRecordsRepository, times(1)).deleteByName("John", "Doe");
    }

    @Test
    void deleteMedicalRecord_shouldPropagateExceptionFromRepository() {
        when(medicalRecordsRepository.deleteByName("John", "Doe")).thenThrow(new RuntimeException("delete-fail"));

        Exception ex = assertThrows(RuntimeException.class, () -> medicalRecordsService.deleteMedicalRecord("John", "Doe"));
        assertEquals("delete-fail", ex.getMessage());
        verify(medicalRecordsRepository, times(1)).deleteByName("John", "Doe");
    }
}