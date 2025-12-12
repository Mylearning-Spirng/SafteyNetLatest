package com.openclassroom.safteynetalertsrefactor.controller;

import com.openclassroom.safteynetalertsrefactor.dto.ChildResidentDto;
import com.openclassroom.safteynetalertsrefactor.dto.ResidentDto;
import com.openclassroom.safteynetalertsrefactor.dto.FirstResponderDto;
import com.openclassroom.safteynetalertsrefactor.service.FirstResponderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class FirstResponderController {

    private final FirstResponderService service;

    public FirstResponderController(FirstResponderService service) {
        this.service = service;
    }

    @GetMapping("/firestation")
    public FirstResponderDto getFirestation(@RequestParam int stationNumber) {
        return service.getPersonsByStation(stationNumber);
    }

    @GetMapping("/childAlert")
    public List<ChildResidentDto> getChildAlert(@RequestParam String address) {
        return service.getChildrenByAddress(address);
    }

    @GetMapping("/phoneAlert")
    public List<String> getPhoneAlert(@RequestParam("firestation") int stationNumber) {
        return service.getPhoneAlert(stationNumber);
    }

    @GetMapping("/fire/{address}")
    public List<ResidentDto> getFire(@PathVariable String address) {
        return service.getFireInfo(address);
    }

//    @GetMapping("/fire")
//    public List<ResidentDto> getFire(@RequestParam("address") String address) {
//        return service.getFireInfo(address);
//    }

    @GetMapping("/communityEmail")
    public List<String> getCommunityEmail(@RequestParam String city) {
        return service.getCommunityEmail(city);
    }
}