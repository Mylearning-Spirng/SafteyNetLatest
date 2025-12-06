package com.openclassroom.safteynetalertsrefactor.DTO;

import java.util.List;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FireStationCoverageDto {

    private List<PersonDto> persons;
    private int numberOfAdults;
    private int numberOfChildren;

}
