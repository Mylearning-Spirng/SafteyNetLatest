package com.openclassroom.safteynetalertsrefactor.service;

import com.openclassroom.safteynetalertsrefactor.model.Person;
import com.openclassroom.safteynetalertsrefactor.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    private  final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    public Person addPerson(Person person){
        personRepository.add(person);
        return person;
    }

    public boolean updatePerson(String firstName, String lastName, Person updatedPerson) {
        Optional<Person> existingPersonOpt = personRepository.findByName(firstName, lastName);
        if (existingPersonOpt.isEmpty()) {
            return false;
        }
        Person existingPerson = existingPersonOpt.get();
        existingPerson.setAddress(updatedPerson.getAddress());
        existingPerson.setCity(updatedPerson.getCity());
        existingPerson.setZip(updatedPerson.getZip());
        existingPerson.setPhone(updatedPerson.getPhone());
        existingPerson.setEmail(updatedPerson.getEmail());
        personRepository.persist();
        return true;
    }

    public boolean delete (String firstName, String lastName) {
        return personRepository.deletePerson(firstName, lastName);
    }
}