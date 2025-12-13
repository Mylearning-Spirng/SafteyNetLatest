package com.openclassroom.safteynetalertsrefactor.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

/* DTO representing first responders information including persons,
   number of adults and number of children.
*/
public class FirstResponderDto {

    private List<PersonDto> persons;
    private int numberOfAdults;
    private int numberOfChildren;

}
