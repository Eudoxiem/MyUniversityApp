package com.myuniversity.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MyUniversityAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyUniversityAppApplication.class, args);
    }
}
