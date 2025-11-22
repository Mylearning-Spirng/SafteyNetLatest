package com.openclassroom.safteynetalertsrefactor.controller;
import com.openclassroom.safteynetalertsrefactor.model.Person;
import com.openclassroom.safteynetalertsrefactor.service.PersonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/persons")
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public List<Person> getAllPersons() {
        return personService.getAllPersons();
    }

    @PostMapping
    public Person addPerson(@RequestBody Person person){
        return personService.addPerson(person);
    }

    @PutMapping("/{lastName}/{firstName}")
    public ResponseEntity<Boolean> updatePerson(@PathVariable String firstName,
                                                @PathVariable String lastName,
                                                @RequestBody Person updatedPerson) {

        boolean ok = personService.updatePerson(firstName, lastName, updatedPerson);
        return ok ? ResponseEntity.ok(true) : ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
    }

    @DeleteMapping("/{lastName}/{firstName}")
    public ResponseEntity<Boolean> deletePerson(@PathVariable String firstName,
                                          @PathVariable String lastName) {
        boolean deleted = personService.delete(firstName, lastName);
        return deleted ? ResponseEntity.ok(true) : ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
    }

}
