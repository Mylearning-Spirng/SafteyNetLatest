package com.openclassroom.safteynetalertsrefactor.service;

import com.openclassroom.safteynetalertsrefactor.model.FireStation;
import com.openclassroom.safteynetalertsrefactor.repository.FireStationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FireStationService {

    private  final FireStationRepository fireStationRepository;

    public FireStationService(FireStationRepository fireStationRepository) {
        this.fireStationRepository = fireStationRepository;
    }

    public List<FireStation> getAllFireStations() {
        return fireStationRepository.findAll();
    }

    public FireStation addFireStation(FireStation fireStation){
        fireStationRepository.add(fireStation);
        return fireStation;
    }

    public boolean updateFireStation(String address, FireStation updated) {
        return fireStationRepository.updateFireStation(address, updated.getStation());
    }

    public boolean deleteByAddress(String address) {
        return fireStationRepository.deleteByAddress(address);
    }

    public boolean deleteByStationNumber(int stationNumber) {
        return fireStationRepository.deleteByStationNumber(stationNumber);
    }
}