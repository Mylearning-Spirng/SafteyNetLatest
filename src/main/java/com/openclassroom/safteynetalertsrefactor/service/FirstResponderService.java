package com.openclassroom.safteynetalertsrefactor.service;

import com.openclassroom.safteynetalertsrefactor.dto.*;
import com.openclassroom.safteynetalertsrefactor.model.FireStation;
import com.openclassroom.safteynetalertsrefactor.model.MedicalRecord;
import com.openclassroom.safteynetalertsrefactor.model.Person;
import com.openclassroom.safteynetalertsrefactor.repository.FireStationRepository;
import com.openclassroom.safteynetalertsrefactor.repository.MedicalRecordsRepository;
import com.openclassroom.safteynetalertsrefactor.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FirstResponderService {

    private final PersonRepository personRepository;
    private final FireStationRepository fireStationRepository;
    private final MedicalRecordsRepository medicalRecordRepository;

    @Autowired
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

    private int calculateAgeOf(String firstName, String lastName) {
        MedicalRecord mr = findMedicalRecord(firstName, lastName);
        if (mr == null) return 0;
        return mr.calculateAge();
    }

    /* ================= List of people covered by the corresponding fire station ================= */

    public FirstResponderDto getPersonsByStation(int stationNumber) {
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
                int age = calculateAgeOf(p.getFirstName(), p.getLastName());
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

        return new FirstResponderDto(personsCovered, adults, children);
    }

    /* ================= List of children under 18 by address and other household in that address ================= */

    public List<ChildResidentDto> getChildrenByAddress(String address) {

        List<Person> peopleAtAddress = new ArrayList<>();
        List<ChildResidentDto> children = new ArrayList<>();

        // collect all people living at this address
        for (Person p : personRepository.findAll()) {
            if (p.getAddress() != null && address.equalsIgnoreCase(p.getAddress())) {
                peopleAtAddress.add(p);
            }
        }

        if (peopleAtAddress.isEmpty()) {
            return Collections.emptyList();
        }

        // find children (<= 18) and add "other household members"
        for (Person child : peopleAtAddress) {
            int age = calculateAgeOf(child.getFirstName(), child.getLastName());

            if (age <= 18) {
                List<PersonDto> otherMembers = new ArrayList<>();

                for (Person other : peopleAtAddress) {
                    // skip the child itself
                    if (other.getFirstName().equalsIgnoreCase(child.getFirstName())
                            && other.getLastName().equalsIgnoreCase(child.getLastName())) {
                        continue;
                    }

                    otherMembers.add(new PersonDto(
                            other.getFirstName(),
                            other.getLastName(),
                            other.getAddress(),
                            other.getPhone()
                    ));
                }

                children.add(new ChildResidentDto(
                        child.getFirstName(),
                        child.getLastName(),
                        age,
                        otherMembers
                ));
            }
        }

        return children;
    }

    /* ================= List of phone numbers by station number ================= */

    public List<String> getPhoneAlert(int stationNumber) {

        List<String> addresses = new ArrayList<>();
        for (FireStation fs : fireStationRepository.findAll()) {
            if (fs.getStation() == stationNumber) {
                addresses.add(fs.getAddress());
            }
        }
        List<String> phones = new ArrayList<>();
        for (Person p : personRepository.findAll()) {
            if (addresses.contains(p.getAddress())) {
                phones.add(p.getPhone());
            }
        }

        return phones;
    }

    /* ================= Fire info by address ================= */

    public List<ResidentDto> getFireInfo(String address) {
        List<ResidentDto> residents = new ArrayList<>();
        String targetAddress = address.trim().toLowerCase();
        for (Person p : personRepository.findAll()) {
            if (p.getAddress() != null && targetAddress.equalsIgnoreCase(p.getAddress().trim().toLowerCase())) {
                MedicalRecord mr = findMedicalRecord(p.getFirstName(), p.getLastName());
                int age = calculateAgeOf(p.getFirstName(), p.getLastName());
                List<String> meds = mr != null ? mr.getMedications() : List.of();
                List<String> allergies = mr != null ? mr.getAllergies() : List.of();

                residents.add(new ResidentDto(
                        p.getFirstName(),
                        p.getLastName(),
                        p.getPhone(),
                        age,
                        meds,
                        allergies
                ));
            }
        }
        return residents;
    }

    /* ================= /community email by city ================= */

    public List<String> getCommunityEmail(String city) {
        List<String> emails = new ArrayList<>();
        for (Person p : personRepository.findAll()) {
            if (city.equalsIgnoreCase(p.getCity())) {
                emails.add(p.getEmail());
            }
        }
        return emails;
    }

}