package com.openclassroom.safteynetalertsrefactor.controller;

import com.openclassroom.safteynetalertsrefactor.model.Person;
import com.openclassroom.safteynetalertsrefactor.service.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/persons")
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public List<Person> getAllPersons() {
        log.info("GET /persons - request received");
        List<Person> persons = personService.getAllPersons();
        return persons;
    }

    @PostMapping
    public Person addPerson(@RequestBody Person person) {
        return personService.addPerson(person);
    }

    @PutMapping("/{lastName}/{firstName}")
    public ResponseEntity<Boolean> updatePerson(@PathVariable String firstName,
                                                @PathVariable String lastName,
                                                @RequestBody Person updatedPerson) {
        boolean ok = personService.updatePerson(firstName, lastName, updatedPerson);
        log.warn("PUT /persons/{}/{} - person not found", lastName, firstName);
        return ok ? ResponseEntity.ok(true) : ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
    }

    @DeleteMapping("/{lastName}/{firstName}")
    public ResponseEntity<Boolean> deletePerson(@PathVariable String firstName,
                                                @PathVariable String lastName) {
        boolean deleted = personService.delete(firstName, lastName);
        log.info("DELETE /persons/{}/{} - deleted", lastName, firstName);
        return deleted ? ResponseEntity.ok(true) : ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
    }

}
