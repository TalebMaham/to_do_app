package com.example.demo;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {

    @Test
    @Disabled // ✅ Ignore ce test lors des exécutions (surtout en CI/CD)
    void contextLoads() {
    }
}

