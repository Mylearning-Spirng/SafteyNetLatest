package com.openclassroom.safteynetalertsrefactor.model;

import lombok.*;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class MedicalRecord {
    private String firstName;
    private String lastName;
    private String birthdate;
    private List<String> medications;
    private List<String> allergies;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public int calculateAge() {
        if (birthdate == null || birthdate.isEmpty()) return 0;
        LocalDate dob = LocalDate.parse(birthdate, FORMATTER);
        return Period.between(dob, LocalDate.now()).getYears();
    }
}