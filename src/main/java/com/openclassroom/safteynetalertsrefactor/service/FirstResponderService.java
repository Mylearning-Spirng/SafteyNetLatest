package com.openclassroom.safteynetalertsrefactor.service;

import com.openclassroom.safteynetalertsrefactor.dto.*;
import com.openclassroom.safteynetalertsrefactor.model.FireStation;
import com.openclassroom.safteynetalertsrefactor.model.MedicalRecord;
import com.openclassroom.safteynetalertsrefactor.model.Person;
import com.openclassroom.safteynetalertsrefactor.repository.FireStationRepository;
import com.openclassroom.safteynetalertsrefactor.repository.MedicalRecordsRepository;
import com.openclassroom.safteynetalertsrefactor.repository.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
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
        log.info("FirstResponderService initialized");
    }

    /* ================= Helper methods ================= */

    private int calculateAgeOf(String firstName, String lastName) {
        MedicalRecord mr = medicalRecordRepository.findByName(firstName, lastName).orElse(null);
        if (mr == null) {
            log.debug("Age calculation: no medical record for {} {}", firstName, lastName);
            return 0;
        }
        int age = mr.calculateAge();
        log.debug("Calculated age for {} {} = {}", firstName, lastName, age);
        return age;
    }

    /**   Returns a FirstResponderDto containing a list of persons covered by the specified fire station number,
     * along with counts of adults and children.
     *
     * @param stationNumber The fire station number.
     * @return FirstResponderDto with persons covered, adult count, and child count.
     */
    public FirstResponderDto getPersonsByStation(int stationNumber) {
        log.info("getPersonsByStation called for station {}", stationNumber);
        List<PersonDto> personsCovered = new ArrayList<>();
        int adults = 0;
        int children = 0;

        // find all addresses served by this station
        List<String> addresses = new ArrayList<>();
        for (FireStation fs : fireStationRepository.findAll()) {
            if (fs != null && fs.getAddress() != null && stationNumber == fs.getStation()) {
                addresses.add(fs.getAddress());
            }
        }
        log.debug("Found {} addresses for station {}", addresses.size(), stationNumber);

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

        log.info("Station {} covers {} persons (adults={}, children={})", stationNumber, personsCovered.size(), adults, children);
        return new FirstResponderDto(personsCovered, adults, children);
    }

    /* ================= List of children under 18 by address and other household in that address ================= */

    /** Returns a list of ChildResidentDto for children (age <= 18) living at the specified address,
     * along with other household members.
     *
     * @param address The address to search for children.
     * @return List of ChildResidentDto for children at the address.
     */
    public List<ChildResidentDto> getChildrenByAddress(String address) {
        log.info("getChildrenByAddress called for address '{}'", address);
        List<Person> peopleAtAddress = new ArrayList<>();
        List<ChildResidentDto> children = new ArrayList<>();

        // All people living at this address
        for (Person p : personRepository.findAll()) {
            if (p.getAddress() != null && address.equalsIgnoreCase(p.getAddress())) {
                peopleAtAddress.add(p);
            }
        }

        log.debug("Found {} people at address '{}'", peopleAtAddress.size(), address);

        if (peopleAtAddress.isEmpty()) {
            log.debug("No residents at address '{}'", address);
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

        log.info("Address '{}' has {} children", address, children.size());
        return children;
    }

    /* ================= List of phone numbers by station number ================= */
    /**   Returns a list of phone numbers for all persons covered by the specified fire station number.
     *
     * @param stationNumber The fire station number.
     * @return List of phone numbers for persons covered by the station.
     */
    public List<String> getPhoneAlert(int stationNumber) {
        log.info("getPhoneAlert called for station {}", stationNumber);
        List<String> addresses = new ArrayList<>();
        for (FireStation fs : fireStationRepository.findAll()) {
            if (fs.getStation() == stationNumber) {
                addresses.add(fs.getAddress());
            }
        }
        log.debug("Addresses for station {}: {}", stationNumber, addresses);

        List<String> phones = new ArrayList<>();
        for (Person p : personRepository.findAll()) {
            if (addresses.contains(p.getAddress())) {
                phones.add(p.getPhone());
            }
        }

        log.info("Found {} phone numbers for station {}", phones.size(), stationNumber);
        return phones;
    }

    /* ================= Fire info by address ================= */
    /**   Returns a list of ResidentDto for all residents at the specified address,
     * including their medical information.
     *
     * @param address The address to search for residents.
     * @return List of ResidentDto for residents at the address.
     */
    public List<ResidentDto> getFireInfo(String address) {
        log.info("getFireInfo called for address '{}'", address);
        List<ResidentDto> residents = new ArrayList<>();
        String targetAddress = address.trim().toLowerCase();
        for (Person p : personRepository.findAll()) {
            if (p.getAddress() != null && targetAddress.equalsIgnoreCase(p.getAddress().trim().toLowerCase())) {
                MedicalRecord mr = medicalRecordRepository.findByName(p.getFirstName(), p.getLastName()).orElse(null);
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
        log.info("Found {} residents for address '{}'", residents.size(), address);
        return residents;
    }

    /* ================= /community email by city ================= */
    /**   Returns a list of email addresses for all persons living in the specified city.
     *
     * @param city The city to search for email addresses.
     * @return List of email addresses for residents of the city.
     */
    public List<String> getCommunityEmail(String city) {
        log.info("getCommunityEmail called for city '{}'", city);
        List<String> emails = new ArrayList<>();
        for (Person p : personRepository.findAll()) {
            if (city.equalsIgnoreCase(p.getCity())) {
                emails.add(p.getEmail());
            }
        }
        log.info("Found {} emails for city '{}'", emails.size(), city);
        return emails;
    }

//    /* ================= /personInfo?lastName=Boyd ================= */

    public List<ResidentDto> getResidentsByLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            return List.of();
        }

        String target = lastName.trim();
        List<ResidentDto> result = new ArrayList<>();

        for (Person p : personRepository.findAll()) {
            if (p.getLastName() != null && p.getLastName().equalsIgnoreCase(target)) {
                Optional<MedicalRecord> mrOpt = medicalRecordRepository.findByName(p.getFirstName(), p.getLastName());
                MedicalRecord mr = mrOpt.orElse(null);

                int age = calculateAgeOf(p.getFirstName(), p.getLastName());
                List<String> meds = mr != null && mr.getMedications() != null ? mr.getMedications() : List.of();
                List<String> allergies = mr != null && mr.getAllergies() != null ? mr.getAllergies() : List.of();

                ResidentDto dto = new ResidentDto(
                        p.getFirstName(),
                        p.getLastName(),
                        p.getPhone(),
                        age,
                        meds,
                        allergies
                );
                result.add(dto);
            }
        }

        return result;
    }

    /* ================= /flood/stations?stations=1,2 ================= */
    // Simple version: map address -> list of FirstResponderDto

    // java
    public List<Object> getFloodInfo(List<String> stations) {

        List<Object> result = new ArrayList<>();

        // Collect addresses for given stations
        List<String> addresses = new ArrayList<>();
        for (FireStation fs : fireStationRepository.findAll()) {
            if (fs != null
                    && fs.getAddress() != null
                    && stations.contains(String.valueOf(fs.getStation()))) {

                if (!addresses.contains(fs.getAddress())) {
                    addresses.add(fs.getAddress());
                }
            }
        }

        // Cache all people once to avoid multiple repository calls
        List<Person> allPeople = personRepository.findAll();

        // For each address → collect residents
        for (String address : addresses) {

            List<ResidentDto> residents = new ArrayList<>();

            for (Person p : allPeople) {
                if (p == null || !address.equals(p.getAddress())) {
                    continue;
                }

                MedicalRecord mr = medicalRecordRepository
                        .findByName(p.getFirstName(), p.getLastName())
                        .orElse(null);

                int age = mr != null ? calculateAgeOf(p.getFirstName(), p.getLastName()) : 0;
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

            List<Object> addressBlock = new ArrayList<>();
            addressBlock.add(address);
            addressBlock.add(residents);

            result.add(addressBlock);
        }

        return result;
    }
}
