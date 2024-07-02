package com.ecom.ware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients("com.ecom.ware.feign")
@SpringBootApplication
@EnableDiscoveryClient
public class EcomWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcomWareApplication.class, args);
    }

}
