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
        MedicalRecord medicalrecord = medicalRecordRepository.findByName(firstName, lastName).orElse(null);
        if (medicalrecord == null) {
            log.debug("Age calculation: no medical record for {} {}", firstName, lastName);
            return 0;
        }
        int age = medicalrecord.calculateAge();
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
        // collect addresses for the given station number
        for (FireStation fs : fireStationRepository.findAll()) {
            if (fs != null && fs.getAddress() != null && stationNumber == fs.getStation()) {
                addresses.add(fs.getAddress());
            }
        }
        log.debug("Found {} addresses for station {}", addresses.size(), stationNumber);

        // find all persons living at those addresses
        /* if so, determine age and classify as adult/child */
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
                MedicalRecord medicalrecord = medicalRecordRepository.findByName(p.getFirstName(), p.getLastName()).orElse(null);
                int age = calculateAgeOf(p.getFirstName(), p.getLastName());
                List<String> meds = medicalrecord != null ? medicalrecord.getMedications() : List.of();
                List<String> allergies = medicalrecord != null ? medicalrecord.getAllergies() : List.of();

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

    /**   Returns a list of ResidentDto for all residents with the specified last name,
     * including their medical information.
     *
     * @param lastName The last name to search for residents.
     * @return List of ResidentDto for residents with the last name.
     */
//    /* ================= /personInfoByLastName================= */

    public List<ResidentDto> getResidentsByLastName(String lastName) {
        log.info("getResidentsByLastName called with lastName='{}'", lastName);
        if (lastName == null || lastName.trim().isEmpty()) {
            log.debug("getResidentsByLastName: empty or null lastName -> returning empty list");
            return List.of();
        }

        String target = lastName.trim();
        List<ResidentDto> result = new ArrayList<>();

        List<Person> allPeople = personRepository.findAll();
        log.debug("getResidentsByLastName: scanning {} people for lastName='{}'", allPeople.size(), target);

        for (Person p : allPeople) {
            if (p == null) {
                continue;
            }
            if (p.getLastName() != null && p.getLastName().equalsIgnoreCase(target)) {
                log.debug("Match found: {} {}", p.getFirstName(), p.getLastName());

                Optional<MedicalRecord> mrOpt = medicalRecordRepository.findByName(p.getFirstName(), p.getLastName());
                MedicalRecord medicalrecord = mrOpt.orElse(null);
                if (medicalrecord == null) {
                    log.debug("No medical record for {} {}", p.getFirstName(), p.getLastName());
                }

                int age = calculateAgeOf(p.getFirstName(), p.getLastName());
                List<String> meds = medicalrecord != null && medicalrecord.getMedications() != null ? medicalrecord.getMedications() : List.of();
                List<String> allergies = medicalrecord != null && medicalrecord.getAllergies() != null ? medicalrecord.getAllergies() : List.of();

                ResidentDto dto = new ResidentDto(
                        p.getFirstName(),
                        p.getLastName(),
                        p.getPhone(),
                        age,
                        meds,
                        allergies
                );
                result.add(dto);
                log.debug("Added ResidentDto for {} {} (age={})", p.getFirstName(), p.getLastName(), age);
            }
        }

        log.info("getResidentsByLastName returning {} residents for lastName='{}'", result.size(), target);
        return result;
    }

    /**   Returns a list of HouseholdDto for all households served by the specified fire station numbers,
     * including resident medical information.
     *
     * @param stations The list of fire station numbers.
     * @return List of HouseholdDto for households served by the stations.
     */
    /* ================= /flood stations================= */
    public List<HouseholdDto> getFloodInfo(List<String> stations) {
        log.info("getFloodInfo called for stations={}", stations);
        if (stations == null || stations.isEmpty()) {
            log.debug("getFloodInfo: stations list is null or empty -> returning empty result");
            return List.of();
        }

        List<HouseholdDto> result = new ArrayList<>();
        List<String> addresses = collectAddresses(stations);

        // Cache all people once to avoid multiple repository calls
        List<Person> allPeople = personRepository.findAll();
        log.debug("getFloodInfo: loaded {} people from repository", allPeople.size());

        collectResidences(addresses, allPeople, result);

        log.info("getFloodInfo returning {} address blocks", result.size());
        return result;
    }

    private List<String> collectAddresses(List<String> stations) {
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
        log.debug("getFloodInfo: found {} addresses for stations {}", addresses.size(), stations);
        return addresses;
    }

    private void collectResidences(List<String> addresses, List<Person> allPeople, List<HouseholdDto> result) {
        for (String address : addresses) {
            log.debug("Processing address '{}'", address);
            List<ResidentDto> residents = new ArrayList<>();

            for (Person p : allPeople) {
                if (p == null || !address.equals(p.getAddress())) {
                    continue;
                }

                MedicalRecord medicalrecord = medicalRecordRepository
                        .findByName(p.getFirstName(), p.getLastName())
                        .orElse(null);

                if (medicalrecord == null) {
                    log.debug("No medical record for {} {} at address '{}'", p.getFirstName(), p.getLastName(), address);
                }

                int age = medicalrecord != null ? calculateAgeOf(p.getFirstName(), p.getLastName()) : 0;
                List<String> meds = medicalrecord != null ? medicalrecord.getMedications() : List.of();
                List<String> allergies = medicalrecord != null ? medicalrecord.getAllergies() : List.of();

                residents.add(new ResidentDto(
                        p.getFirstName(),
                        p.getLastName(),
                        p.getPhone(),
                        age,
                        meds,
                        allergies
                ));
                log.debug("Added resident {} {} for address '{}'", p.getFirstName(), p.getLastName(), address);
            }

            result.add(new HouseholdDto(address, residents));
            log.debug("Address '{}' block added with {} residents", address, residents.size());
        }
    }
}
