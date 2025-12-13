package com.openclassroom.safteynetalertsrefactor.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
/* DTO representing a resident's detailed information including
   name, phone, age, medications, and allergies.
*/
public class ResidentDto {
    private String firstName;
    private String lastName;
    private String phone;
    private int age;
    private List<String> medicationList;
    private List<String> allergyList;

}
