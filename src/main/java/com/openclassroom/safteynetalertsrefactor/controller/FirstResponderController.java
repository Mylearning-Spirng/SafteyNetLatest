package com.openclassroom.safteynetalertsrefactor.controller;

import com.openclassroom.safteynetalertsrefactor.DTO.FireStationCoverageDto;
import com.openclassroom.safteynetalertsrefactor.service.FirstResponderService;
import org.springframework.web.bind.annotation.*;

@RestController
public class FirstResponderController {

    private final FirstResponderService service;

    public FirstResponderController(FirstResponderService service) {
        this.service = service;
    }

    @GetMapping("/firestation")
    public FireStationCoverageDto getFirestation(@RequestParam int stationNumber) {
        return service.getPersonsByStation(stationNumber);
    }

}