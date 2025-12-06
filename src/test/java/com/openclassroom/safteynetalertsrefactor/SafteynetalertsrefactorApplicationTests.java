package com.openclassroom.safteynetalertsrefactor;

import com.openclassroom.safteynetalertsrefactor.controller.FirstResponderController;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class SafteynetalertsrefactorApplicationTests {

    @Autowired
    private FirstResponderController controller;

    @Test
    void contextLoads() throws Exception{
        assertThat(controller).isNotNull();
    }

}
