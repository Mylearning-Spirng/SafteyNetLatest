package com.openclassroom.safteynetalertsrefactor.view;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FirstResponderView {

    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private int age;
    private String birthdate;
    private String phoneNumber;

}
