package com.openclassroom.safteynetalertsrefactor;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class SafteynetalertsrefactorApplicationTests {

    @Test
    void contextLoads() {
        assertDoesNotThrow(() -> {
            SafteynetalertsrefactorApplication.main(new String[]{});
        });
    }

}
