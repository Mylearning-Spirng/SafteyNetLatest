package com.openclassroom.safteynetalertsrefactor.repository;


import com.openclassroom.safteynetalertsrefactor.model.Person;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PersonRepository {
    private static final String p = "persons";
    private final JSONFileReaderRepository JSONFileReaderRepository;

    private final List<Person> persons = new ArrayList<>();

    public PersonRepository(JSONFileReaderRepository JSONFileReaderRepository) {
        this.JSONFileReaderRepository = JSONFileReaderRepository;
    }

    @PostConstruct
    void init() {
            List<Person> loaded = JSONFileReaderRepository.readList(p, Person.class);
            if (loaded != null) {
                persons.addAll(loaded);
            }
    }

    public List<Person> findAll() {
        return new ArrayList<>(persons);
    }

    public void add(Person newPerson) {
        persons.add(0, newPerson);
        JSONFileReaderRepository.writeList(p, persons);
    }

    public Optional<Person> findByName(String firstName, String lastName) {
        for (Person p : persons) {
            if (p.getFirstName().equals(firstName) && p.getLastName().equals(lastName)) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }

    public boolean deletePerson (String firstName, String lastName) {
        Optional<Person> personToDelete = findByName(firstName, lastName);
        if (personToDelete.isEmpty()) {
            return false;
        }
        persons.remove(personToDelete.get());
        persist();
        return true;
    }

    public void persist() {
        JSONFileReaderRepository.writeList(p, persons);
    }}