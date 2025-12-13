// src/test/java/com/openclassroom/safteynetalertsrefactor/SafteynetalertsrefactorApplicationUnitTest.java
package com.openclassroom.safteynetalertsrefactor;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SafteynetalertsrefactorApplicationUnitTest {

    @Test
    void mainStartsWithoutThrowing() {
        String[] args = {"--spring.main.web-application-type=none"};
        assertDoesNotThrow(() -> SafteynetalertsrefactorApplication.main(args));
    }

}