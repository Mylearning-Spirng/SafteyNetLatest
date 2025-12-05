package com.openclassroom.safteynetalertsrefactor.repository;

import com.openclassroom.safteynetalertsrefactor.model.Person;
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

class PersonRepositoryTest {

    @Mock
    private JSONFileReaderRepository jsonFileReaderRepository;

    private PersonRepository personRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Person samplePerson(String firstName, String lastName) {
        Person p = new Person();
        p.setFirstName(firstName);
        p.setLastName(lastName);
        p.setAddress("123 Main St");
        p.setCity("Townsville");
        p.setZip("12345");
        p.setPhone("111-222-3333");
        p.setEmail(firstName.toLowerCase() + "." + lastName.toLowerCase() + "@mail.com");
        return p;
    }

    @Test
    void init_shouldLoadPersonsFromJsonFile() {
        List<Person> initialPersons = List.of(
                samplePerson("John", "Doe"),
                samplePerson("Jane", "Doe")
        );
        when(jsonFileReaderRepository.readList("persons", Person.class))
                .thenReturn(initialPersons);

        personRepository = new PersonRepository(jsonFileReaderRepository);
        personRepository.init();

        List<Person> all = personRepository.findAll();
        assertEquals(2, all.size());
        assertEquals("John", all.get(0).getFirstName());
    }

    @Test
    void add_shouldAddPersonAndPersistList() {
        when(jsonFileReaderRepository.readList("persons", Person.class))
                .thenReturn(new ArrayList<>()); // start empty

        personRepository = new PersonRepository(jsonFileReaderRepository);
        personRepository.init();

        Person p = samplePerson("John", "Doe");
        personRepository.add(p);

        List<Person> all = personRepository.findAll();
        assertEquals(1, all.size());
        assertEquals("John", all.get(0).getFirstName());

        // verify writeList called with updated list
        verify(jsonFileReaderRepository, times(1))
                .writeList(eq("persons"), anyList());
    }

    @Test
    void findByName_shouldReturnMatchingPerson() {
        List<Person> initialPersons = new ArrayList<>();
        initialPersons.add(samplePerson("John", "Doe"));
        initialPersons.add(samplePerson("Jane", "Doe"));

        when(jsonFileReaderRepository.readList("persons", Person.class))
                .thenReturn(initialPersons);

        personRepository = new PersonRepository(jsonFileReaderRepository);
        personRepository.init();

        Optional<Person> result = personRepository.findByName("Jane", "Doe");

        assertTrue(result.isPresent());
        assertEquals("Jane", result.get().getFirstName());
    }

    @Test
    void findByName_shouldReturnEmptyWhenNotFound() {
        when(jsonFileReaderRepository.readList("persons", Person.class))
                .thenReturn(new ArrayList<>());

        personRepository = new PersonRepository(jsonFileReaderRepository);
        personRepository.init();

        Optional<Person> result = personRepository.findByName("Nobody", "Here");

        assertTrue(result.isEmpty());
    }

    @Test
    void deletePerson_shouldRemovePersonAndPersist_whenExists() {
        Person john = samplePerson("John", "Doe");
        List<Person> initialPersons = new ArrayList<>();
        initialPersons.add(john);

        when(jsonFileReaderRepository.readList("persons", Person.class))
                .thenReturn(initialPersons);

        personRepository = new PersonRepository(jsonFileReaderRepository);
        personRepository.init();

        boolean deleted = personRepository.deletePerson("John", "Doe");

        assertTrue(deleted);
        assertEquals(0, personRepository.findAll().size());

        // capture list passed to writeList
        ArgumentCaptor<List<Person>> captor = ArgumentCaptor.forClass(List.class);
        verify(jsonFileReaderRepository, times(1))
                .writeList(eq("persons"), captor.capture());
        assertEquals(0, captor.getValue().size());
    }

    @Test
    void deletePerson_shouldReturnFalseAndNotPersist_whenNotFound() {
        when(jsonFileReaderRepository.readList("persons", Person.class))
                .thenReturn(new ArrayList<>());

        personRepository = new PersonRepository(jsonFileReaderRepository);
        personRepository.init();

        boolean deleted = personRepository.deletePerson("John", "Doe");

        assertFalse(deleted);
        verify(jsonFileReaderRepository, never())
                .writeList(eq("persons"), anyList());
    }

    @Test
    void persist_shouldWriteCurrentListToFile() {
        List<Person> initialPersons = new ArrayList<>();
        initialPersons.add(samplePerson("John", "Doe"));

        when(jsonFileReaderRepository.readList("persons", Person.class))
                .thenReturn(initialPersons);

        personRepository = new PersonRepository(jsonFileReaderRepository);
        personRepository.init();

        personRepository.persist();

        verify(jsonFileReaderRepository, times(1))
                .writeList(eq("persons"), anyList());
    }
}