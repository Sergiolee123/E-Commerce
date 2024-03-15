package com.ecom.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@MapperScan("com.ecom.member.dao")
@SpringBootApplication
@EnableDiscoveryClient
public class EcomMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcomMemberApplication.class, args);
    }

}
