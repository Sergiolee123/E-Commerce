package com.ecom.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@SpringBootApplication
@EnableDiscoveryClient
public class EcomCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcomCartApplication.class, args);
    }

}
