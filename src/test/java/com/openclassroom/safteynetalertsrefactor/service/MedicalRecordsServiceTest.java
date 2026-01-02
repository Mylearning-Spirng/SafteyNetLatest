package com.openclassroom.safteynetalertsrefactor.service;

import com.openclassroom.safteynetalertsrefactor.model.MedicalRecord;
import com.openclassroom.safteynetalertsrefactor.repository.MedicalRecordsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalRecordsServiceTest {

    @Mock
    private MedicalRecordsRepository medicalRecordsRepository;
    @InjectMocks
    private MedicalRecordsService medicalRecordsService;

    private MedicalRecord sample() {
        MedicalRecord medicalrecord = new MedicalRecord();
        medicalrecord.setFirstName("John");
        medicalrecord.setLastName("Doe");
        medicalrecord.setBirthdate("01/01/1990");
        medicalrecord.setMedications(List.of("med1"));
        medicalrecord.setAllergies(List.of("all1"));
        return medicalrecord;
    }

    @Test
    void getAllMedicalRecords_shouldDelegateToRepository() {
        MedicalRecord medicalrecord = sample();
        when(medicalRecordsRepository.findAll()).thenReturn(List.of(medicalrecord));

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
        MedicalRecord medicalrecord = sample();

        MedicalRecord returned = medicalRecordsService.addMedicalRecords(medicalrecord);

        assertSame(medicalrecord, returned);
        verify(medicalRecordsRepository, times(1)).add(medicalrecord);
    }

    @Test
    void addMedicalRecords_shouldPropagateExceptionFromRepository() {
        MedicalRecord medicalrecord = sample();
        doThrow(new RuntimeException("add-fail")).when(medicalRecordsRepository).add(medicalrecord);

        Exception ex = assertThrows(RuntimeException.class, () -> medicalRecordsService.addMedicalRecords(medicalrecord));
        assertEquals("add-fail", ex.getMessage());
        verify(medicalRecordsRepository, times(1)).add(medicalrecord);
    }

    @Test
    void updateMedicalRecord_shouldReturnTrueIfUpdated() {
        MedicalRecord medicalrecord = sample();
        when(medicalRecordsRepository.updateMedicalRecord("John", "Doe", medicalrecord)).thenReturn(true);

        boolean result = medicalRecordsService.updateMedicalRecord("John", "Doe", medicalrecord);

        assertTrue(result);
        verify(medicalRecordsRepository, times(1)).updateMedicalRecord("John", "Doe", medicalrecord);
    }

    @Test
    void updateMedicalRecord_shouldReturnFalseIfNotUpdated() {
        MedicalRecord medicalrecord = sample();
        when(medicalRecordsRepository.updateMedicalRecord("John", "Doe", medicalrecord)).thenReturn(false);

        boolean result = medicalRecordsService.updateMedicalRecord("John", "Doe", medicalrecord);

        assertFalse(result);
        verify(medicalRecordsRepository, times(1)).updateMedicalRecord("John", "Doe", medicalrecord);
    }

    @Test
    void updateMedicalRecord_shouldPropagateExceptionFromRepository() {
        MedicalRecord medicalrecord = sample();
        when(medicalRecordsRepository.updateMedicalRecord("John", "Doe", medicalrecord)).thenThrow(new RuntimeException("update-fail"));

        Exception ex = assertThrows(RuntimeException.class, () -> medicalRecordsService.updateMedicalRecord("John", "Doe", medicalrecord));
        assertEquals("update-fail", ex.getMessage());
        verify(medicalRecordsRepository, times(1)).updateMedicalRecord("John", "Doe", medicalrecord);
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