package com.openclassroom.safteynetalertsrefactor.dto;

import java.util.List;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChildResidentDto {

    private String firstName;
    private String lastName;
    private int age;
    private List<PersonDto> otherHouseholdMembers;
}
