package com.otp.zadanie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class ZadanieApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZadanieApplication.class, args);
    }

}
