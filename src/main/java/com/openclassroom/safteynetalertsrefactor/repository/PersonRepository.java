package com.openclassroom.safteynetalertsrefactor.repository;

import com.openclassroom.safteynetalertsrefactor.model.Person;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Repository
/* Repository class for managing Person data.
 * It provides methods to load, add, find, delete, and persist person records.
 */
public class PersonRepository {
    private static final String p = "persons";
    private final JSONFileReaderRepository JSONFileReaderRepository;

    private final List<Person> persons = new ArrayList<>();

    public PersonRepository(JSONFileReaderRepository JSONFileReaderRepository) {
        this.JSONFileReaderRepository = JSONFileReaderRepository;
    }

    @PostConstruct
    void init() {
        log.info("Initializing PersonRepository, loading persons from JSON");
        List<Person> loaded = JSONFileReaderRepository.readList(p, Person.class);
        if (loaded != null) {
            persons.addAll(loaded);
            log.info("Loaded {} persons from JSON", loaded.size());
        } else {
            log.warn("No persons loaded from JSON");
        }
    }

    /* Retrieves all person records. */
    public List<Person> findAll() {
        log.debug("Returning all persons, count={}", persons.size());
        return new ArrayList<>(persons);
    }

    /* Adds a new person and persists the change. */
    public void add(Person newPerson) {
        log.info("Adding person: {} {}", newPerson.getFirstName(), newPerson.getLastName());
        persons.add(0, newPerson);
        JSONFileReaderRepository.writeList(p, persons);
        log.debug("Person added, new count={}", persons.size());
    }

    /* Finds a person by first and last name. */
    public Optional<Person> findByName(String firstName, String lastName) {
        for (Person p : persons) {
            if (Objects.equals(p.getFirstName(), firstName) && Objects.equals(p.getLastName(), lastName)) {
                log.debug("Found person {} {}", firstName, lastName);
                return Optional.of(p);
            }
        }
        log.debug("Person {} {} not found", firstName, lastName);
        return Optional.empty();
    }

    /* Deletes a person by first and last name and persists the change. */
    public boolean deletePerson(String firstName, String lastName) {
        Optional<Person> personToDelete = findByName(firstName, lastName);
        if (personToDelete.isEmpty()) {
            log.warn("Attempted to delete person {} {} but not found", firstName, lastName);
            return false;
        }
        log.info("Deleting person {} {}", firstName, lastName);
        persons.remove(personToDelete.get());
        persist();
        return true;
    }

    public void persist() {
        log.debug("Persisting {} persons to JSON", persons.size());
        JSONFileReaderRepository.writeList(p, persons);
    }
}