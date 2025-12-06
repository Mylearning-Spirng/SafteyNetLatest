package com.openclassroom.safteynetalertsrefactor.service;

import com.openclassroom.safteynetalertsrefactor.model.Person;
import com.openclassroom.safteynetalertsrefactor.repository.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing Person records.
 */
@Slf4j
@Service
public class PersonService {

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * Get all Person Records
     *
     * @return List of all Persons
     */
    public List<Person> getAllPersons() {
        log.info("GET /persons - request received");
        try {
            List<Person> persons = personRepository.findAll();
            log.debug("GET /persons - returning {} persons", persons == null ? 0 : persons.size());
            return persons;
        } catch (Exception e) {
            log.error("Error fetching persons", e);
            throw e;
        }
    }

    /**
     * Add a new Person Record
     *
     * @param person Person to add
     * @return Added Person
     */
    public Person addPerson(Person person) {
        log.info("POST /persons - add requested for {} {}", person.getFirstName(), person.getLastName());
        try {
            personRepository.add(person);
            log.debug("POST /persons - added person {} {}", person.getFirstName(), person.getLastName());
            return person;
        } catch (Exception e) {
            log.error("Error adding person {} {}", person.getFirstName(), person.getLastName(), e);
            throw e;
        }
    }

    public boolean updatePerson(String firstName, String lastName, Person updatedPerson) {
        log.info("PUT /persons - update requested for {} {}", firstName, lastName);
        try {
            Optional<Person> existingPersonOpt = personRepository.findByName(firstName, lastName);
            if (existingPersonOpt.isEmpty()) {
                log.warn("PUT /persons - not found for {} {}", firstName, lastName);
                return false;
            }
            Person existingPerson = existingPersonOpt.get();
            existingPerson.setAddress(updatedPerson.getAddress());
            existingPerson.setCity(updatedPerson.getCity());
            existingPerson.setZip(updatedPerson.getZip());
            existingPerson.setPhone(updatedPerson.getPhone());
            existingPerson.setEmail(updatedPerson.getEmail());
            personRepository.persist();
            log.info("PUT /persons - update successful for {} {}", firstName, lastName);
            return true;
        } catch (Exception e) {
            log.error("Error updating person for {} {}", firstName, lastName, e);
            throw e;
        }
    }

    public boolean delete(String firstName, String lastName) {
        log.info("DELETE /persons - delete requested for {} {}", firstName, lastName);
        try {
            boolean deleted = personRepository.deletePerson(firstName, lastName);
            if (deleted) {
                log.info("DELETE /persons - delete successful for {} {}", firstName, lastName);
            } else {
                log.warn("DELETE /persons - not found for {} {}", firstName, lastName);
            }
            return deleted;
        } catch (Exception e) {
            log.error("Error deleting person for {} {}", firstName, lastName, e);
            throw e;
        }
    }
}
