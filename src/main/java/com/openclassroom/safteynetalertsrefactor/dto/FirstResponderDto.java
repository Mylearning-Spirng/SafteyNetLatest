package com.openclassroom.safteynetalertsrefactor.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FirstResponderDto {

    private List<PersonDto> persons;
    private int numberOfAdults;
    private int numberOfChildren;

}
