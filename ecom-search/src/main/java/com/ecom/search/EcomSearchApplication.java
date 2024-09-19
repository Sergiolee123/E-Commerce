package com.ecom.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class EcomSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcomSearchApplication.class, args);
    }

}