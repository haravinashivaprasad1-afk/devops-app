package com.lab.exam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class DevopsAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(DevopsAppApplication.class, args);
    }

    @GetMapping("/")
    public String examHome() {
        return "<h1>DevOps Lab Deployed!</h1>";
    }
}
