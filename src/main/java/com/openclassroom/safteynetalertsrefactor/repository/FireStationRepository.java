package com.openclassroom.safteynetalertsrefactor.repository;

import com.openclassroom.safteynetalertsrefactor.model.FireStation;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository

/* Repository class for managing FireStation data.
 * It provides methods to load, add, update, delete, and persist fire station records.
 */
public class FireStationRepository {
    private static final String station = "firestations";
    private final JSONFileReaderRepository JSONFileReaderRepository;

    private final List<FireStation> firestations = new ArrayList<>();

    public FireStationRepository(JSONFileReaderRepository JSONFileReaderRepository) {
        this.JSONFileReaderRepository = JSONFileReaderRepository;
        log.info("FireStationRepository created for resource: {}", station);
    }

    @PostConstruct
        /* Initializes the repository by loading fire station data from a JSON resource. */
    void init() {
        log.info("Initializing FireStationRepository from resource: {}", station);
        List<FireStation> loaded = JSONFileReaderRepository.readList(station, FireStation.class);
        if (loaded != null) {
            firestations.addAll(loaded);
            log.info("Loaded {} fire stations", loaded.size());
        } else {
            log.warn("No fire stations loaded from resource: {}", station);
        }
    }

    /* Retrieves all fire station records. */
    public List<FireStation> findAll() {
        log.debug("findAll called, returning {} records", firestations.size());
        return new ArrayList<>(firestations);
    }

    /* Adds a new fire station record and persists the change. */
    public void add(FireStation newFireStation) {
        log.info("Adding fire station for address: {} -> station {}", newFireStation.getAddress(), newFireStation.getStation());
        firestations.add(0, newFireStation);
        persist();
    }

    /* Finds a fire station by its address. */
    public Optional<FireStation> findByAddress(String address) {
        log.debug("Searching for fire station at address: {}", address);
        for (FireStation fs : firestations) {
            if (fs.getAddress().equals(address)) {
                log.debug("Found fire station at address: {} -> station {}", address, fs.getStation());
                return Optional.of(fs);
            }
        }
        log.debug("No fire station found at address: {}", address);
        return Optional.empty();
    }

    /* Updates the station number for a fire station at the given address. */
    public boolean updateFireStation(String address, int stationNumber) {
        log.info("Updating fire station at address: {} to station {}", address, stationNumber);
        Optional<FireStation> fireStationToUpdate = findByAddress(address);
        if (fireStationToUpdate.isEmpty()) {
            log.warn("Cannot update - fire station not found at address: {}", address);
            return false;
        }
        fireStationToUpdate.get().setStation(stationNumber);
        persist();
        log.info("Updated fire station at address: {} to station {}", address, stationNumber);
        return true;
    }

    /* Deletes a fire station by its address. */
    public boolean deleteByAddress(String address) {
        log.info("Deleting fire station at address: {}", address);
        Optional<FireStation> fireStationToDelete = findByAddress(address);
        if (fireStationToDelete.isEmpty()) {
            log.warn("Cannot delete - fire station not found at address: {}", address);
            return false;
        }
        firestations.remove(fireStationToDelete.get());
        persist();
        log.info("Deleted fire station at address: {}", address);
        return true;
    }

    /* Deletes all fire stations associated with the given station number. */
    public boolean deleteByStationNumber(int stationNumber) {
        log.info("Deleting fire stations with station number: {}", stationNumber);
        boolean found = false;
        List<FireStation> toRemove = new ArrayList<>();
        for (FireStation fs : firestations) {
            if (fs.getStation() == stationNumber) {
                toRemove.add(fs);
                found = true;
            }
        }
        firestations.removeAll(toRemove);
        if (found) {
            persist();
            log.info("Deleted {} fire station(s) for station {}", toRemove.size(), stationNumber);
        } else {
            log.debug("No fire stations found for station {}", stationNumber);
        }
        return found;
    }

    /* Persists the current list of fire stations to the JSON resource. */
    public void persist() {
        log.debug("Persisting {} fire station(s) to resource: {}", firestations.size(), station);
        JSONFileReaderRepository.writeList(station, firestations);
    }
}
