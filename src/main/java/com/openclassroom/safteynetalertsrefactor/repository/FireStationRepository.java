package com.openclassroom.safteynetalertsrefactor.repository;

import com.openclassroom.safteynetalertsrefactor.model.FireStation;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class FireStationRepository {
    private static final String station = "firestations";
    private final JSONFileReaderRepository JSONFileReaderRepository;

    private final List<FireStation> firestations = new ArrayList<>();

    public FireStationRepository(JSONFileReaderRepository JSONFileReaderRepository) {
        this.JSONFileReaderRepository = JSONFileReaderRepository;
    }

    @PostConstruct
    void init() {
        List<FireStation> loaded = JSONFileReaderRepository.readList(station, FireStation.class);
        if (loaded != null) {
            firestations.addAll(loaded);
        }
    }

    public List<FireStation> findAll() {
        return new ArrayList<>(firestations);
    }

    public void add(FireStation newFireStation) {
        firestations.add(0, newFireStation);
        persist();
    }

    public Optional <FireStation> findByAddress(String address) {
        for (FireStation fs : firestations) {
            if (fs.getAddress().equals(address)) {
                return Optional.of(fs);
            }
        }
        return Optional.empty();
    }

    public boolean updateFireStation(String address, int stationNumber) {
        Optional<FireStation> fireStationToUpdate = findByAddress(address);
        if (fireStationToUpdate.isEmpty()) {
            return false;
        }
        fireStationToUpdate.get().setStation(stationNumber);
        persist();
        return true;
    }

    public boolean deleteByAddress(String address) {
        Optional<FireStation> fireStationToDelete = findByAddress(address);
        if (fireStationToDelete.isEmpty()) {
            return false;
        }
        firestations.remove(fireStationToDelete.get());
        persist();
        return true;
    }

    public boolean deleteByStationNumber(int stationNumber) {
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
        }
        return found;
    }

    public void persist() {
        JSONFileReaderRepository.writeList(station, firestations);
    }
}