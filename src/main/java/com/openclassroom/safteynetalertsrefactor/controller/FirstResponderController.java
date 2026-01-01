package com.openclassroom.safteynetalertsrefactor.controller;

import com.openclassroom.safteynetalertsrefactor.dto.ChildResidentDto;
import com.openclassroom.safteynetalertsrefactor.dto.HouseholdDto;
import com.openclassroom.safteynetalertsrefactor.dto.ResidentDto;
import com.openclassroom.safteynetalertsrefactor.dto.FirstResponderDto;
import com.openclassroom.safteynetalertsrefactor.service.FirstResponderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FirstResponderController {

    private final FirstResponderService service;

    public FirstResponderController(FirstResponderService service) {
        this.service = service;
    }

    /* Endpoint mappings for First Responder functionalities */
    /* Retrieve persons covered by a fire station */
    @GetMapping("/firestation")
    public FirstResponderDto getFirestation(@RequestParam int stationNumber) {
        return service.getPersonsByStation(stationNumber);
    }

    /* Retrieve children at a given address */
    @GetMapping("/childAlert")
    public List<ChildResidentDto> getChildAlert(@RequestParam String address) {
        return service.getChildrenByAddress(address);
    }

    /* Retrieve phone numbers for a fire station */
    @GetMapping("/phoneAlert")
    public List<String> getPhoneAlert(@RequestParam("firestation") int stationNumber) {
        return service.getPhoneAlert(stationNumber);
    }

    /* Retrieve fire information for a given address */
    @GetMapping("/fire")
    public List<ResidentDto> getFire(@RequestParam("address") String address) {
        return service.getFireInfo(address);
    }

    /* Retrieve community emails for a given city */
    @GetMapping("/communityEmail")
    public List<String> getCommunityEmail(@RequestParam String city) {
        return service.getCommunityEmail(city);
    }

    /* Retrieve person information by last name */
    @GetMapping("/personInfo")
    public List<ResidentDto> getPersonInfo(@RequestParam("lastName") String lastName) {
        return service.getResidentsByLastName(lastName);
    }

    /* Retrieve flood information for given fire stations */
    @GetMapping("/flood/stations")
    public List<HouseholdDto> getFloodStations(@RequestParam("stations") List<String> stations) {
        return service.getFloodInfo(stations);
    }
}