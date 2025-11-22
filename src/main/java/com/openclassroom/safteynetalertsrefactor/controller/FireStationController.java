package com.openclassroom.safteynetalertsrefactor.controller;
import com.openclassroom.safteynetalertsrefactor.model.FireStation;
import com.openclassroom.safteynetalertsrefactor.service.FireStationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/firestations")
public class FireStationController {

    private final FireStationService fireStationService;

    public FireStationController(FireStationService fireStationService) {
        this.fireStationService = fireStationService;
    }

    @GetMapping
    public List<FireStation> getAllFireStations() {
        return fireStationService.getAllFireStations();
    }

    @PostMapping
    public FireStation addFireStation(@RequestBody FireStation fireStation){
        return fireStationService.addFireStation(fireStation);
    }

    @PutMapping("/{address}")
    public ResponseEntity<Boolean>updateFireStation(@PathVariable String address,
                              @RequestBody FireStation updated) {
        boolean ok = fireStationService.updateFireStation(address, updated);
        return ok ? ResponseEntity.ok(true) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/address/{address}")
    public ResponseEntity<Boolean> deleteByAddress(@PathVariable String address) {
        boolean deleted = fireStationService.deleteByAddress(address);
        return deleted ? ResponseEntity.ok(true) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/station/{stationNumber}")
    public ResponseEntity<Boolean> deleteByStationNumber(@PathVariable int stationNumber) {
        boolean deleted = fireStationService.deleteByStationNumber(stationNumber);
        return deleted ? ResponseEntity.ok(true) : ResponseEntity.notFound().build();
    }

}
