package com.openclassroom.safteynetalertsrefactor.service;

import com.openclassroom.safteynetalertsrefactor.model.Person;
import com.openclassroom.safteynetalertsrefactor.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;
    @InjectMocks
    private PersonService personService;

    private Person samplePerson() {
        Person p = new Person();
        p.setFirstName("John");
        p.setLastName("Doe");
        p.setAddress("123 Main St");
        p.setCity("Townsville");
        p.setZip("12345");
        p.setPhone("111-222-3333");
        p.setEmail("john.doe@example.com");
        return p;
    }

    @Test
    void getAllPersons_shouldDelegateToRepository() {
        when(personRepository.findAll()).thenReturn(List.of(samplePerson()));

        List<Person> result = personService.getAllPersons();

        assertEquals(1, result.size());
        verify(personRepository, times(1)).findAll();
    }

    @Test
    void getAllPersons_shouldPropagateExceptionWhenRepositoryThrows() {
        when(personRepository.findAll()).thenThrow(new RuntimeException("boom"));

        assertThrows(RuntimeException.class, () -> personService.getAllPersons());
        verify(personRepository, times(1)).findAll();
    }

    @Test
    void addPerson_shouldCallRepositoryAndReturnPerson() {
        Person person = samplePerson();

        Person returned = personService.addPerson(person);

        assertEquals(person, returned);
        verify(personRepository, times(1)).add(person);
    }

    @Test
    void addPerson_shouldPropagateExceptionWhenRepositoryThrows() {
        Person person = samplePerson();
        doThrow(new RuntimeException("add fail")).when(personRepository).add(any());

        assertThrows(RuntimeException.class, () -> personService.addPerson(person));
        verify(personRepository, times(1)).add(person);
    }

    @Test
    void updatePerson_shouldUpdateExistingPersonAndPersist() {
        Person existing = samplePerson();
        Person updated = new Person();
        updated.setAddress("456 New St");
        updated.setCity("NewCity");
        updated.setZip("99999");
        updated.setPhone("999-888-7777");
        updated.setEmail("new.mail@example.com");

        when(personRepository.findByName("John", "Doe"))
                .thenReturn(Optional.of(existing));

        boolean ok = personService.updatePerson("John", "Doe", updated);

        assertTrue(ok);
        assertEquals("456 New St", existing.getAddress());
        assertEquals("NewCity", existing.getCity());
        assertEquals("99999", existing.getZip());
        assertEquals("999-888-7777", existing.getPhone());
        assertEquals("new.mail@example.com", existing.getEmail());

        verify(personRepository, times(1)).persist();
    }

    @Test
    void updatePerson_shouldReturnFalseWhenPersonNotFound() {
        when(personRepository.findByName("John", "Doe"))
                .thenReturn(Optional.empty());

        boolean ok = personService.updatePerson("John", "Doe", samplePerson());

        assertFalse(ok);
        verify(personRepository, never()).persist();
    }

    @Test
    void updatePerson_shouldPropagateExceptionWhenFindByNameThrows() {
        when(personRepository.findByName("John", "Doe")).thenThrow(new RuntimeException("find fail"));

        assertThrows(RuntimeException.class, () -> personService.updatePerson("John", "Doe", samplePerson()));
        verify(personRepository, times(1)).findByName("John", "Doe");
    }

    @Test
    void updatePerson_shouldPropagateExceptionWhenPersistThrows() {
        Person existing = samplePerson();
        when(personRepository.findByName("John", "Doe")).thenReturn(Optional.of(existing));
        doThrow(new RuntimeException("persist fail")).when(personRepository).persist();

        assertThrows(RuntimeException.class, () -> personService.updatePerson("John", "Doe", samplePerson()));
        verify(personRepository, times(1)).persist();
    }

    @Test
    void delete_shouldDelegateToRepository() {
        when(personRepository.deletePerson("John", "Doe")).thenReturn(true);

        boolean deleted = personService.delete("John", "Doe");

        assertTrue(deleted);
        verify(personRepository, times(1))
                .deletePerson("John", "Doe");
    }

    @Test
    void delete_shouldReturnFalseWhenNotFound() {
        when(personRepository.deletePerson("John", "Doe")).thenReturn(false);

        boolean deleted = personService.delete("John", "Doe");

        assertFalse(deleted);
        verify(personRepository, times(1)).deletePerson("John", "Doe");
    }

    @Test
    void delete_shouldPropagateExceptionWhenRepositoryThrows() {
        when(personRepository.deletePerson("John", "Doe")).thenThrow(new RuntimeException("delete fail"));

        assertThrows(RuntimeException.class, () -> personService.delete("John", "Doe"));
        verify(personRepository, times(1)).deletePerson("John", "Doe");
    }
}
