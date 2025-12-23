package com.openclassroom.safteynetalertsrefactor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdDto {

    private String address;
    private List<ResidentDto> residents;


}
