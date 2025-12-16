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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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

        Person alice = person("Alice", "Anderson", "100 Main St", "111-111-1111", "City", "a@x.com");
        Person bob = person("Bob", "Brown", "100 Main St", "222-222-2222", "City", "b@x.com");

        MedicalRecord mrAlice = mr("Alice", "Anderson", "01/01/2010", Collections.emptyList(), Collections.emptyList());
        MedicalRecord mrBob = mr("Bob", "Brown", "01/01/1980", Collections.emptyList(), Collections.emptyList());

        when(fireStationRepository.findAll()).thenReturn(Arrays.asList(station));
        when(personRepository.findAll()).thenReturn(Arrays.asList(alice, bob));
        when(medicalRecordRepository.findByName("Alice", "Anderson")).thenReturn(Optional.of(mrAlice));
        when(medicalRecordRepository.findByName("Bob", "Brown")).thenReturn(Optional.of(mrBob));

        FirstResponderDto result = service.getPersonsByStation(1);

        assertNotNull(result);
        assertEquals(2, result.getPersons().size());
        assertEquals(1, result.getNumberOfAdults());
        assertEquals(1, result.getNumberOfChildren());

        verify(fireStationRepository, times(1)).findAll();
        verify(personRepository, times(1)).findAll();
        verify(medicalRecordRepository, atLeast(2)).findByName(anyString(), anyString());
    }

    @Test
    void getChildrenByAddress_returnsChildWithOtherMembers() {
        Person child = person("Charlie", "Cole", "123 Elm St", "333-333-3333", "City", "c@x.com");
        Person parent = person("Paula", "Cole", "123 Elm St", "444-444-4444", "City", "p@x.com");

        MedicalRecord mrChild = mr("Charlie", "Cole", "01/01/2015", Collections.emptyList(), Collections.emptyList());
        MedicalRecord mrParent = mr("Paula", "Cole", "01/01/1985", Collections.emptyList(), Collections.emptyList());

        when(personRepository.findAll()).thenReturn(Arrays.asList(child, parent));
        when(medicalRecordRepository.findByName("Charlie", "Cole")).thenReturn(Optional.of(mrChild));
        when(medicalRecordRepository.findByName("Paula", "Cole")).thenReturn(Optional.of(mrParent));

        List<ChildResidentDto> children = service.getChildrenByAddress("123 Elm St");

        assertNotNull(children);
        assertEquals(1, children.size());
        ChildResidentDto dto = children.get(0);
        assertEquals("Charlie", dto.getFirstName());
        assertTrue(dto.getAge() <= 18);
        assertEquals(1, dto.getOtherHouseholdMembers().size());
        PersonDto other = dto.getOtherHouseholdMembers().get(0);
        assertEquals("Paula", other.getFirstName());

        verify(personRepository, times(1)).findAll();
        verify(medicalRecordRepository, atLeastOnce()).findByName(anyString(), anyString());
    }

    @Test
    void getPhoneAlert_returnsPhonesForStation_and_doesNotCallMedicalRepo() {
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
        verify(medicalRecordRepository, never()).findByName(anyString(), anyString());
    }

    @Test
    void getFireInfo_returnsResidentWithMedsAndAllergies() {
        Person dave = person("Dave", "Duke", "50 Pine St", "777-7777", "Town", "d@x.com");
        MedicalRecord mrDave = mr("Dave", "Duke", "01/01/1990", Arrays.asList("med1"), Arrays.asList("peanut"));

        when(personRepository.findAll()).thenReturn(Collections.singletonList(dave));
        when(medicalRecordRepository.findByName("Dave", "Duke")).thenReturn(Optional.of(mrDave));

        List<ResidentDto> residents = service.getFireInfo("50 Pine St");

        assertEquals(1, residents.size());
        ResidentDto r = residents.get(0);
        assertEquals("Dave", r.getFirstName());
        assertEquals("777-7777", r.getPhone());
        assertTrue(r.getMedicationList().contains("med1"));
        assertTrue(r.getAllergyList().contains("peanut"));

        verify(personRepository, times(1)).findAll();
        verify(medicalRecordRepository, atLeastOnce()).findByName("Dave", "Duke");
    }

    @Test
    void getCommunityEmail_returnsEmailsByCity_and_doesNotCallMedicalRepo() {
        Person p1 = person("Eve", "Evans", "X St", "000", "MyCity", "e@x.com");
        Person p2 = person("Fay", "Fox", "Y St", "111", "OtherCity", "f@x.com");

        when(personRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<String> emails = service.getCommunityEmail("MyCity");

        assertEquals(1, emails.size());
        assertTrue(emails.contains("e@x.com"));

        verify(personRepository, times(1)).findAll();
        verify(medicalRecordRepository, never()).findByName(anyString(), anyString());
    }

    @Test
    void getResidentsByLastName_returnsResidents_and_handlesEmptyInput() {
        Person p1 = person("Ann", "Duke", "A", "1", "C", "a@x.com");
        Person p2 = person("Ben", "Duke", "B", "2", "C", "b@x.com");

        MedicalRecord mr1 = mr("Ann", "Duke", "01/01/1992", Arrays.asList("mA"), Collections.emptyList());
        MedicalRecord mr2 = mr("Ben", "Duke", "01/01/1988", Arrays.asList("mB"), Arrays.asList("peanut"));

        when(personRepository.findAll()).thenReturn(Arrays.asList(p1, p2));
        when(medicalRecordRepository.findByName("Ann", "Duke")).thenReturn(Optional.of(mr1));
        when(medicalRecordRepository.findByName("Ben", "Duke")).thenReturn(Optional.of(mr2));

        List<ResidentDto> residents = service.getResidentsByLastName("Duke");
        assertEquals(2, residents.size());

        // empty/blank input
        assertTrue(service.getResidentsByLastName(" ").isEmpty());
        verify(medicalRecordRepository, atLeast(2)).findByName(anyString(), eq("Duke"));
    }

    @Test
    void getFloodInfo_returnsAddressBlocks_withResidents() {
        FireStation f1 = fs("Addr1", 1);
        FireStation f2 = fs("Addr2", 2);

        Person p1 = person("P1", "L1", "Addr1", "111", "C", "p1@x.com");
        Person p2 = person("P2", "L2", "Addr2", "222", "C", "p2@x.com");
        Person p3 = person("P3", "L3", "Addr1", "333", "C", "p3@x.com");

        MedicalRecord mr1 = mr("P1", "L1", "01/01/1990", Collections.emptyList(), Collections.emptyList());
        MedicalRecord mr2 = mr("P2", "L2", "01/01/2000", Collections.emptyList(), Collections.emptyList());
        MedicalRecord mr3 = mr("P3", "L3", "01/01/2010", Collections.emptyList(), Collections.emptyList());

        when(fireStationRepository.findAll()).thenReturn(Arrays.asList(f1, f2));
        when(personRepository.findAll()).thenReturn(Arrays.asList(p1, p2, p3));
        when(medicalRecordRepository.findByName("P1", "L1")).thenReturn(Optional.of(mr1));
        when(medicalRecordRepository.findByName("P2", "L2")).thenReturn(Optional.of(mr2));
        when(medicalRecordRepository.findByName("P3", "L3")).thenReturn(Optional.of(mr3));

        List<Object> flood = service.getFloodInfo(Arrays.asList("1", "2"));

        // expecting two address blocks (Addr1, Addr2) order may vary
        assertEquals(2, flood.size());

        // validate contents: each block is a List with [address, residents]
        Set<String> addressesFound = new HashSet<>();
        for (Object blockObj : flood) {
            assertTrue(blockObj instanceof List);
            List<?> block = (List<?>) blockObj;
            assertEquals(2, block.size());
            Object addr = block.get(0);
            Object residentsObj = block.get(1);
            assertTrue(addr instanceof String);
            addressesFound.add((String) addr);
            assertTrue(residentsObj instanceof List);
            List<?> resList = (List<?>) residentsObj;
            // residents list non-empty for our sample addresses
            assertFalse(resList.isEmpty());
        }
        assertTrue(addressesFound.contains("Addr1"));
        assertTrue(addressesFound.contains("Addr2"));

        verify(fireStationRepository, times(1)).findAll();
        verify(personRepository, times(1)).findAll();
        verify(medicalRecordRepository, atLeast(3)).findByName(anyString(), anyString());
    }
}
