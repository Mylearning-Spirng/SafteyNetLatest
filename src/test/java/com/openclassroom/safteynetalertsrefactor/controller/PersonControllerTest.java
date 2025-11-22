package com.openclassroom.safteynetalertsrefactor.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassroom.safteynetalertsrefactor.model.Person;
import com.openclassroom.safteynetalertsrefactor.service.PersonService;
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

class PersonControllerTest {

    private MockMvc mockMvc;
    private PersonService personService;
    private PersonController personController;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        personService = Mockito.mock(PersonService.class);
        personController = new PersonController(personService);
        mockMvc = MockMvcBuilders.standaloneSetup(personController).build();
        objectMapper = new ObjectMapper();
    }

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
    void getAllPersons_shouldReturnList() throws Exception {
        var person = samplePerson();
        Mockito.when(personService.getAllPersons()).thenReturn(List.of(person));

        MvcResult result = mockMvc.perform(get("/persons"))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
        String content = result.getResponse().getContentAsString();
        List<Person> persons = objectMapper.readValue(content, new TypeReference<List<Person>>() {});
        assertEquals(1, persons.size());
        assertEquals("John", persons.get(0).getFirstName());
        assertEquals("Doe", persons.get(0).getLastName());

        verify(personService, times(1)).getAllPersons();
    }

    @Test
    void addPerson_shouldReturnPerson() throws Exception {
        var person = samplePerson();
        Mockito.when(personService.addPerson(any(Person.class))).thenReturn(person);

        MvcResult result = mockMvc.perform(post("/persons")
                        .content(objectMapper.writeValueAsString(person))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
        String content = result.getResponse().getContentAsString();
        Person returned = objectMapper.readValue(content, Person.class);
        assertEquals("John", returned.getFirstName());
        assertEquals("john.doe@example.com", returned.getEmail());

        verify(personService, times(1)).addPerson(any(Person.class));
    }

    @Test
    void updatePerson_shouldReturnOk() throws Exception {
        var updated = samplePerson();
        updated.setAddress("456 New St");

        Mockito.when(personService.updatePerson(eq("John"), eq("Doe"), any(Person.class))).thenReturn(true);

        MvcResult result = mockMvc.perform(put("/persons/Doe/John")
                        .content(objectMapper.writeValueAsString(updated))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
        String content = result.getResponse().getContentAsString();
        Boolean ok = objectMapper.readValue(content, Boolean.class);
        assertTrue(ok);

        verify(personService, times(1)).updatePerson(eq("John"), eq("Doe"), any(Person.class));
    }

    @Test
    void deletePerson_shouldReturnOk() throws Exception {
        Mockito.when(personService.delete("John", "Doe")).thenReturn(true);

        MvcResult result = mockMvc.perform(delete("/persons/Doe/John"))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
        String content = result.getResponse().getContentAsString();
        Boolean deleted = objectMapper.readValue(content, Boolean.class);
        assertTrue(deleted);

        verify(personService, times(1)).delete("John", "Doe");
    }
}
