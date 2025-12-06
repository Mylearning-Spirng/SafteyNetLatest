package com.openclassroom.safteynetalertsrefactor.service;

import com.openclassroom.safteynetalertsrefactor.DTO.*;
import com.openclassroom.safteynetalertsrefactor.model.FireStation;
import com.openclassroom.safteynetalertsrefactor.model.MedicalRecord;
import com.openclassroom.safteynetalertsrefactor.model.Person;
import com.openclassroom.safteynetalertsrefactor.repository.FireStationRepository;
import com.openclassroom.safteynetalertsrefactor.repository.MedicalRecordsRepository;
import com.openclassroom.safteynetalertsrefactor.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FirstResponderService {

    private final PersonRepository personRepository;
    private final FireStationRepository fireStationRepository;
    private final MedicalRecordsRepository medicalRecordRepository;

    public FirstResponderService(PersonRepository personRepository,
                                 FireStationRepository fireStationRepository,
                                 MedicalRecordsRepository medicalRecordRepository) {
        this.personRepository = personRepository;
        this.fireStationRepository = fireStationRepository;
        this.medicalRecordRepository = medicalRecordRepository;
    }
    //Helpers Class

    private MedicalRecord findMedicalRecord(String firstName, String lastName) {
        for (MedicalRecord mr : medicalRecordRepository.findAll()) {
            if (mr.getFirstName().equalsIgnoreCase(firstName)
                    && mr.getLastName().equalsIgnoreCase(lastName)) {
                return mr;
            }
        }
        return null;
    }

    private int ageOf(String firstName, String lastName) {
        MedicalRecord mr = findMedicalRecord(firstName, lastName);
        if (mr == null) return 0;
        return AgeCalculatorDto.calculateAge(mr.getBirthdate());
    }

//    ............................list of people covered by the corresponding fire station.............................

    public FireStationCoverageDto getPersonsByStation(int stationNumber) {
        List<PersonDto> personsCovered = new ArrayList<>();
        int adults = 0;
        int children = 0;

        // find all addresses served by this station (compare as strings to avoid type mismatch)
        List<String> addresses = new ArrayList<>();
        for (FireStation fs : fireStationRepository.findAll()) {
            if (fs != null && fs.getAddress() != null && stationNumber == fs.getStation()) {
                addresses.add(fs.getAddress());
            }
        }

        // for each person, check if address is in those addresses
        for (Person p : personRepository.findAll()) {
            if (addresses.contains(p.getAddress())) {
                int age = ageOf(p.getFirstName(), p.getLastName());
                // children if age 18 or younger -> adult if age > 18
                if (age > 18) {
                    adults++;
                } else {
                    children++;
                }

                personsCovered.add(
                        new PersonDto(
                                p.getFirstName(),
                                p.getLastName(),
                                p.getAddress(),
                                p.getPhone()
                        )
                );
            }
        }

        return new FireStationCoverageDto(personsCovered, adults, children);
    }
}