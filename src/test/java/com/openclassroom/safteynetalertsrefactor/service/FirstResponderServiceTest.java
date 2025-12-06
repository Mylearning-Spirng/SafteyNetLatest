package com.openclassroom.safteynetalertsrefactor.service;

import com.openclassroom.safteynetalertsrefactor.DTO.FirstResponderDto;
import com.openclassroom.safteynetalertsrefactor.model.FireStation;
import com.openclassroom.safteynetalertsrefactor.model.MedicalRecord;
import com.openclassroom.safteynetalertsrefactor.model.Person;
import com.openclassroom.safteynetalertsrefactor.repository.FireStationRepository;
import com.openclassroom.safteynetalertsrefactor.repository.MedicalRecordsRepository;
import com.openclassroom.safteynetalertsrefactor.repository.PersonRepository;
import com.openclassroom.safteynetalertsrefactor.service.FirstResponderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FirstResponderServiceTest {

    private PersonRepository personRepository;
    private FireStationRepository fireStationRepository;
    private MedicalRecordsRepository medicalRecordRepository;
    private FirstResponderService service;

    private static FireStation createFireStation(String addr, int station) {
        FireStation fs = new FireStation();
        fs.setAddress(addr);
        fs.setStation(station);
        return fs;
    }

    private static Person createPerson(String first, String last, String addr, String phone) {
        Person p = new Person();
        p.setFirstName(first);
        p.setLastName(last);
        p.setAddress(addr);
        p.setPhone(phone);
        return p;
    }

    private static MedicalRecord createMedicalRecord(String first, String last, String birthdate) {
        MedicalRecord mr = new MedicalRecord();
        mr.setFirstName(first);
        mr.setLastName(last);
        mr.setBirthdate(birthdate);
        return mr;
    }

    @BeforeEach
    void setup() {
        personRepository = Mockito.mock(PersonRepository.class);
        fireStationRepository = Mockito.mock(FireStationRepository.class);
        medicalRecordRepository = Mockito.mock(MedicalRecordsRepository.class);
        service = new FirstResponderService(personRepository, fireStationRepository, medicalRecordRepository);
    }

    @Test
    void getPersonsByStation_shouldReturnCoverageAndCallRepositories() {
        FireStation fs1 = createFireStation("100 Main St", 1);
        FireStation fs2 = createFireStation("200 Oak St", 2);

        Person alice = createPerson("Alice", "Anderson", "100 Main St", "111-111-1111");
        Person bob = createPerson("Bob", "Brown", "200 Oak St", "222-222-2222");

        // Use explicit birthdates that represent a child (Alice) and an adult (Bob).
        MedicalRecord mrAlice = createMedicalRecord("Alice", "Anderson", "01/01/2010"); // intended child
        MedicalRecord mrBob = createMedicalRecord("Bob", "Brown", "01/01/1980"); // intended adult

        when(fireStationRepository.findAll()).thenReturn(Arrays.asList(fs1, fs2));
        when(personRepository.findAll()).thenReturn(Arrays.asList(alice, bob));
        when(medicalRecordRepository.findAll()).thenReturn(Arrays.asList(mrAlice, mrBob));

        FirstResponderDto result = service.getPersonsByStation(1);

        assertNotNull(result);
        assertEquals(1, result.getPersons().size(), "only Alice at station 1 address should be returned");
        assertEquals(0, result.getNumberOfAdults(), "Alice is expected to be a child in this test");
        assertEquals(1, result.getNumberOfChildren(), "Alice is expected to be a child in this test");

        verify(fireStationRepository, times(1)).findAll();
        verify(personRepository, times(1)).findAll();
        verify(medicalRecordRepository, times(1)).findAll();
    }

    @Test
    void getPersonsByStation_noMatches_returnsEmptyCoverage() {
        FireStation fs = createFireStation("100 Main St", 1);

        when(fireStationRepository.findAll()).thenReturn(Collections.singletonList(fs));
        when(personRepository.findAll()).thenReturn(Collections.emptyList());
        when(medicalRecordRepository.findAll()).thenReturn(Collections.emptyList());

        FirstResponderDto result = service.getPersonsByStation(99);

        assertNotNull(result);
        assertTrue(result.getPersons().isEmpty());
        assertEquals(0, result.getNumberOfAdults());
        assertEquals(0, result.getNumberOfChildren());

        verify(fireStationRepository, times(1)).findAll();
        verify(personRepository, times(1)).findAll();
        verify(medicalRecordRepository, never()).findAll();
    }
}
