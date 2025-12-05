package com.openclassroom.safteynetalertsrefactor.service;

import com.openclassroom.safteynetalertsrefactor.model.FireStation;
import com.openclassroom.safteynetalertsrefactor.repository.FireStationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FireStationService {

    private final FireStationRepository fireStationRepository;

    public FireStationService(FireStationRepository fireStationRepository) {
        this.fireStationRepository = fireStationRepository;
    }

    public List<FireStation> getAllFireStations() {
        log.info("GET all fire stations requested");
        try {
            List<FireStation> stations = fireStationRepository.findAll();
            log.debug("GET all fire stations - returning {} stations", stations == null ? 0 : stations.size());
            return stations;
        } catch (Exception e) {
            log.error("Error fetching fire stations", e);
            throw e;
        }
    }

    public FireStation addFireStation(FireStation fireStation) {
        log.info("Add fire station requested: address='{}', station='{}'", fireStation.getAddress(), fireStation.getStation());
        try {
            fireStationRepository.add(fireStation);
            log.debug("Add fire station - added: address='{}'", fireStation.getAddress());
            return fireStation;
        } catch (Exception e) {
            log.error("Error adding fire station address='{}'", fireStation.getAddress(), e);
            throw e;
        }
    }

    public boolean updateFireStation(String address, FireStation updated) {
        log.info("Update fire station requested for address='{}' -> station='{}'", address, updated.getStation());
        try {
            boolean updatedOk = fireStationRepository.updateFireStation(address, updated.getStation());
            if (updatedOk) {
                log.info("Update successful for address='{}'", address);
            } else {
                log.warn("Update failed - fire station not found for address='{}'", address);
            }
            return updatedOk;
        } catch (Exception e) {
            log.error("Error updating fire station for address='{}'", address, e);
            throw e;
        }
    }

    public boolean deleteByAddress(String address) {
        log.info("Delete fire station requested for address='{}'", address);
        try {
            boolean deleted = fireStationRepository.deleteByAddress(address);
            if (deleted) {
                log.info("Delete successful for address='{}'", address);
            } else {
                log.warn("Delete failed - data not found for address='{}'", address);
            }
            return deleted;
        } catch (Exception e) {
            log.error("Error deleting fire station for address='{}'", address, e);
            throw e;
        }
    }

    public boolean deleteByStationNumber(int stationNumber) {
        log.info("Delete fire station(s) requested for stationNumber={}", stationNumber);
        try {
            boolean deleted = fireStationRepository.deleteByStationNumber(stationNumber);
            if (deleted) {
                log.info("Delete successful for stationNumber={}", stationNumber);
            } else {
                log.warn("Delete failed - no entries for stationNumber={}", stationNumber);
            }
            return deleted;
        } catch (Exception e) {
            log.error("Error deleting fire stations for stationNumber={}", stationNumber, e);
            throw e;
        }
    }
}
