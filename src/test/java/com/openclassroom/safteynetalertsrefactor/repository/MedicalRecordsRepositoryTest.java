package com.openclassroom.safteynetalertsrefactor.repository;

import com.openclassroom.safteynetalertsrefactor.model.MedicalRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class MedicalRecordsRepositoryTest {

    @Mock
    private JSONFileReaderRepository jsonFileReaderRepository;

    private MedicalRecordsRepository medicalRecordsRepository;

    private MedicalRecord sampleRecord(String firstName, String lastName) {
        MedicalRecord mr = new MedicalRecord();
        mr.setFirstName(firstName);
        mr.setLastName(lastName);
        mr.setBirthdate("01/01/2000");
        mr.setMedications(List.of("med1", "med2"));
        mr.setAllergies(List.of("allergy1"));
        return mr;
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void init_shouldLoadRecordsFromJson() {
        List<MedicalRecord> records = List.of(
                sampleRecord("John", "Doe"),
                sampleRecord("Jane", "Doe")
        );

        when(jsonFileReaderRepository.readList("medicalrecords", MedicalRecord.class))
                .thenReturn(records);

        medicalRecordsRepository = new MedicalRecordsRepository(jsonFileReaderRepository);
        medicalRecordsRepository.init();

        List<MedicalRecord> all = medicalRecordsRepository.findAll();
        assertEquals(2, all.size());
        assertEquals("John", all.get(0).getFirstName());
    }

    @Test
    void add_shouldAddRecordAndPersist() {
        when(jsonFileReaderRepository.readList("medicalrecords", MedicalRecord.class))
                .thenReturn(new ArrayList<>());

        medicalRecordsRepository = new MedicalRecordsRepository(jsonFileReaderRepository);
        medicalRecordsRepository.init();

        MedicalRecord mr = sampleRecord("John", "Doe");
        medicalRecordsRepository.add(mr);

        List<MedicalRecord> all = medicalRecordsRepository.findAll();
        assertEquals(1, all.size());
        assertEquals("John", all.get(0).getFirstName());

        verify(jsonFileReaderRepository, times(1))
                .writeList(eq("medicalrecords"), anyList());
    }

    @Test
    void findByName_shouldReturnRecordWhenExists() {
        List<MedicalRecord> records = List.of(sampleRecord("John", "Doe"));
        when(jsonFileReaderRepository.readList("medicalrecords", MedicalRecord.class))
                .thenReturn(records);

        medicalRecordsRepository = new MedicalRecordsRepository(jsonFileReaderRepository);
        medicalRecordsRepository.init();

        Optional<MedicalRecord> result = medicalRecordsRepository.findByName("John", "Doe");

        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
    }

    @Test
    void findByName_shouldReturnEmptyWhenNotFound() {
        when(jsonFileReaderRepository.readList("medicalrecords", MedicalRecord.class))
                .thenReturn(new ArrayList<>());

        medicalRecordsRepository = new MedicalRecordsRepository(jsonFileReaderRepository);
        medicalRecordsRepository.init();

        Optional<MedicalRecord> result = medicalRecordsRepository.findByName("John", "Doe");
        assertTrue(result.isEmpty());
    }

    @Test
    void updateMedicalRecord_shouldUpdateAndPersistWhenFound() {
        MedicalRecord existing = sampleRecord("John", "Doe");
        List<MedicalRecord> records = new ArrayList<>();
        records.add(existing);

        when(jsonFileReaderRepository.readList("medicalrecords", MedicalRecord.class))
                .thenReturn(records);

        medicalRecordsRepository = new MedicalRecordsRepository(jsonFileReaderRepository);
        medicalRecordsRepository.init();

        MedicalRecord updated = new MedicalRecord();
        updated.setBirthdate("02/02/2010");
        updated.setMedications(List.of("newMed"));
        updated.setAllergies(List.of("newAllergy"));

        boolean ok = medicalRecordsRepository.updateMedicalRecord("John", "Doe", updated);

        assertTrue(ok);
        assertEquals("02/02/2010", existing.getBirthdate());
        assertEquals(List.of("newMed"), existing.getMedications());
        assertEquals(List.of("newAllergy"), existing.getAllergies());

        verify(jsonFileReaderRepository, times(1))
                .writeList(eq("medicalrecords"), anyList());
    }

    @Test
    void updateMedicalRecord_shouldReturnFalseWhenNotFound() {
        when(jsonFileReaderRepository.readList("medicalrecords", MedicalRecord.class))
                .thenReturn(new ArrayList<>());

        medicalRecordsRepository = new MedicalRecordsRepository(jsonFileReaderRepository);
        medicalRecordsRepository.init();

        boolean ok = medicalRecordsRepository.updateMedicalRecord(
                "John", "Doe", sampleRecord("John", "Doe"));

        assertFalse(ok);
        verify(jsonFileReaderRepository, never())
                .writeList(eq("medicalrecords"), anyList());
    }

    @Test
    void deleteByName_shouldRemoveRecordAndPersist() {
        MedicalRecord existing = sampleRecord("John", "Doe");
        List<MedicalRecord> records = new ArrayList<>();
        records.add(existing);

        when(jsonFileReaderRepository.readList("medicalrecords", MedicalRecord.class))
                .thenReturn(records);

        medicalRecordsRepository = new MedicalRecordsRepository(jsonFileReaderRepository);
        medicalRecordsRepository.init();

        boolean deleted = medicalRecordsRepository.deleteByName("John", "Doe");

        assertTrue(deleted);
        assertEquals(0, medicalRecordsRepository.findAll().size());

        verify(jsonFileReaderRepository, times(1))
                .writeList(eq("medicalrecords"), anyList());
    }

    @Test
    void deleteByName_shouldReturnFalseWhenNotFound() {
        when(jsonFileReaderRepository.readList("medicalrecords", MedicalRecord.class))
                .thenReturn(new ArrayList<>());

        medicalRecordsRepository = new MedicalRecordsRepository(jsonFileReaderRepository);
        medicalRecordsRepository.init();

        boolean deleted = medicalRecordsRepository.deleteByName("John", "Doe");

        assertFalse(deleted);
        verify(jsonFileReaderRepository, never())
                .writeList(eq("medicalrecords"), anyList());
    }

    @Test
    void persist_shouldWriteCurrentList() {
        List<MedicalRecord> records = new ArrayList<>();
        records.add(sampleRecord("John", "Doe"));

        when(jsonFileReaderRepository.readList("medicalrecords", MedicalRecord.class))
                .thenReturn(records);

        medicalRecordsRepository = new MedicalRecordsRepository(jsonFileReaderRepository);
        medicalRecordsRepository.init();

        medicalRecordsRepository.persist();

        verify(jsonFileReaderRepository, times(1))
                .writeList(eq("medicalrecords"), anyList());
    }
}