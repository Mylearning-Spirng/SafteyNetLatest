// java
package com.openclassroom.safteynetalertsrefactor.service;

import com.openclassroom.safteynetalertsrefactor.dto.ChildResidentDto;
import com.openclassroom.safteynetalertsrefactor.dto.FirstResponderDto;
import com.openclassroom.safteynetalertsrefactor.dto.PersonDto;
import com.openclassroom.safteynetalertsrefactor.dto.ResidentDto;
import com.openclassroom.safteynetalertsrefactor.model.FireStation;
import com.openclassroom.safteynetalertsrefactor.model.MedicalRecord;
import com.openclassroom.safteynetalertsrefactor.model.Person;
import com.openclassroom.safteynetalertsrefactor.repository.FireStationRepository;
import com.openclassroom.safteynetalertsrefactor.repository.MedicalRecordsRepository;
import com.openclassroom.safteynetalertsrefactor.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FirstResponderServiceTest {

    private PersonRepository personRepository;
    private FireStationRepository fireStationRepository;
    private MedicalRecordsRepository medicalRecordRepository;
    private FirstResponderService service;

    private static FireStation fs(String addr, int station) {
        FireStation f = new FireStation();
        f.setAddress(addr);
        f.setStation(station);
        return f;
    }

    private static Person person(String first, String last, String addr, String phone, String city, String email) {
        Person p = new Person();
        p.setFirstName(first);
        p.setLastName(last);
        p.setAddress(addr);
        p.setPhone(phone);
        p.setCity(city);
        p.setEmail(email);
        return p;
    }

    private static MedicalRecord mr(String first, String last, String birthdate, List<String> meds, List<String> allergies) {
        MedicalRecord m = new MedicalRecord();
        m.setFirstName(first);
        m.setLastName(last);
        m.setBirthdate(birthdate);
        m.setMedications(meds);
        m.setAllergies(allergies);
        return m;
    }

    @BeforeEach
    void setup() {
        personRepository = Mockito.mock(PersonRepository.class);
        fireStationRepository = Mockito.mock(FireStationRepository.class);
        medicalRecordRepository = Mockito.mock(MedicalRecordsRepository.class);
        service = new FirstResponderService(personRepository, fireStationRepository, medicalRecordRepository);
    }

    @Test
    void getPersonsByStation_returnsCounts_and_callsRepositories() {
        FireStation station = fs("100 Main St", 1);

        Person alice = person("Alice", "Anderson", "100 Main St", "111-111-1111", "City", "a@x.com"); // child (2010)
        Person bob = person("Bob", "Brown", "100 Main St", "222-222-2222", "City", "b@x.com"); // adult (1980)

        MedicalRecord mrAlice = mr("Alice", "Anderson", "01/01/2010", Collections.emptyList(), Collections.emptyList());
        MedicalRecord mrBob = mr("Bob", "Brown", "01/01/1980", Collections.emptyList(), Collections.emptyList());

        when(fireStationRepository.findAll()).thenReturn(Arrays.asList(station));
        when(personRepository.findAll()).thenReturn(Arrays.asList(alice, bob));
        when(medicalRecordRepository.findAll()).thenReturn(Arrays.asList(mrAlice, mrBob));

        FirstResponderDto result = service.getPersonsByStation(1);

        assertNotNull(result);
        assertEquals(2, result.getPersons().size());
        assertEquals(1, result.getNumberOfAdults());
        assertEquals(1, result.getNumberOfChildren());
        // ensure repositories were used
        verify(fireStationRepository, times(1)).findAll();
        verify(personRepository, times(1)).findAll();
        verify(medicalRecordRepository, atLeastOnce()).findAll();
    }

    @Test
    void getPersonsByStation_noMatches_doesNotCallMedicalRepo() {
        FireStation station = fs("100 Main St", 1);

        when(fireStationRepository.findAll()).thenReturn(Collections.singletonList(station));
        when(personRepository.findAll()).thenReturn(Collections.emptyList());

        FirstResponderDto result = service.getPersonsByStation(99);

        assertNotNull(result);
        assertTrue(result.getPersons().isEmpty());
        assertEquals(0, result.getNumberOfAdults());
        assertEquals(0, result.getNumberOfChildren());

        verify(fireStationRepository, times(1)).findAll();
        verify(personRepository, times(1)).findAll();
        verify(medicalRecordRepository, never()).findAll();
    }

    @Test
    void getChildrenByAddress_returnsChildWithOtherMembers() {
        Person child = person("Charlie", "Cole", "123 Elm St", "333-333-3333", "City", "c@x.com"); // child 2015
        Person parent = person("Paula", "Cole", "123 Elm St", "444-444-4444", "City", "p@x.com"); // adult 1985

        MedicalRecord mrChild = mr("Charlie", "Cole", "01/01/2015", Collections.emptyList(), Collections.emptyList());
        MedicalRecord mrParent = mr("Paula", "Cole", "01/01/1985", Collections.emptyList(), Collections.emptyList());

        when(personRepository.findAll()).thenReturn(Arrays.asList(child, parent));
        when(medicalRecordRepository.findAll()).thenReturn(Arrays.asList(mrChild, mrParent));

        List<ChildResidentDto> children = service.getChildrenByAddress("123 Elm St");

        assertNotNull(children);
        assertEquals(1, children.size());
        ChildResidentDto dto = children.get(0);
        assertEquals("Charlie", dto.getFirstName());
        assertTrue(dto.getAge() <= 18);
        // other household members should include parent
        assertEquals(1, dto.getOtherHouseholdMembers().size());
        PersonDto other = dto.getOtherHouseholdMembers().get(0);
        assertEquals("Paula", other.getFirstName());

        verify(personRepository, times(1)).findAll();
        verify(medical_record_repository_validation(), atLeastOnce()).findAll();
    }

    // small helper to avoid static import name collision in the previous test
    private MedicalRecordsRepository medical_record_repository_validation() {
        return medicalRecordRepository;
    }

    @Test
    void getPhoneAlert_returnsPhonesForStation() {
        FireStation fs1 = fs("A St", 2);
        FireStation fs2 = fs("B St", 1);

        Person p1 = person("One", "Two", "A St", "555-0001", "City", "o@x.com");
        Person p2 = person("Three", "Four", "B St", "555-0002", "City", "t@x.com");

        when(fireStationRepository.findAll()).thenReturn(Arrays.asList(fs1, fs2));
        when(personRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<String> phones = service.getPhoneAlert(2);

        assertEquals(1, phones.size());
        assertTrue(phones.contains("555-0001"));

        verify(fireStationRepository, times(1)).findAll();
        verify(personRepository, times(1)).findAll();
        verify(medicalRecordRepository, never()).findAll();
    }

    @Test
    void getFireInfo_returnsResidentWithMedsAndAllergies() {
        Person dave = person("Dave", "Duke", "50 Pine St", "777-7777", "Town", "d@x.com");
        MedicalRecord mrDave = mr("Dave", "Duke", "01/01/1990", Arrays.asList("med1"), Arrays.asList("peanut"));

        when(personRepository.findAll()).thenReturn(Collections.singletonList(dave));
        // FIX: call findAll() on the helper-returned repository mock
        when(medical_record_repository_findAll().findAll()).thenReturn(Collections.singletonList(mrDave));

        List<ResidentDto> residents = service.getFireInfo("50 Pine St");

        assertEquals(1, residents.size());
        ResidentDto r = residents.get(0);
        assertEquals("Dave", r.getFirstName());
        assertEquals("777-7777", r.getPhone());
        assertTrue(r.getMedicationList().contains("med1"));
        assertTrue(r.getAllergyList().contains("peanut"));

        verify(personRepository, times(1)).findAll();
        verify(medical_record_repository_findCall(), atLeastOnce()).findAll();
    }

    // helpers to avoid ambiguous static import names in this file
    private MedicalRecordsRepository medical_record_repository_findAll() {
        return medicalRecordRepository;
    }

    private MedicalRecordsRepository medical_record_repository_findCall() {
        return medicalRecordRepository;
    }

    @Test
    void getCommunityEmail_returnsEmailsByCity() {
        Person p1 = person("Eve", "Evans", "X St", "000", "MyCity", "e@x.com");
        Person p2 = person("Fay", "Fox", "Y St", "111", "OtherCity", "f@x.com");

        when(personRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<String> emails = service.getCommunityEmail("MyCity");

        assertEquals(1, emails.size());
        assertTrue(emails.contains("e@x.com"));

        verify(personRepository, times(1)).findAll();
        verify(medicalRecordRepository, never()).findAll();
    }
}
