package com.ecom.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@MapperScan("com.ecom.ware.dao")
@SpringBootApplication
@EnableDiscoveryClient
public class EcomWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcomWareApplication.class, args);
    }

}
