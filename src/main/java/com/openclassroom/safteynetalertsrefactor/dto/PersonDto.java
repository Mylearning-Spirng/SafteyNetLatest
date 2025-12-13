package com.openclassroom.safteynetalertsrefactor.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
/* DTO representing a person's basic information.
 */
public class PersonDto {
    private String firstName;
    private String lastName;
    private String address;
    private String phone;
}
