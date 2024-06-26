package com.ecom.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@MapperScan("com.ecom.order.dao")
@SpringBootApplication
@EnableTransactionManagement
@EnableDiscoveryClient
public class EcomOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcomOrderApplication.class, args);
    }

}
