package com.openclassroom.safteynetalertsrefactor.DTO;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public final class AgeCalculatorDto {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private AgeCalculatorDto() {}

        public static int calculateAge(String birthdate) {
            if (birthdate == null || birthdate.isEmpty()) return 0;
            LocalDate dob = LocalDate.parse(birthdate, FORMATTER);
            return Period.between(dob, LocalDate.now()).getYears();
            }
}
