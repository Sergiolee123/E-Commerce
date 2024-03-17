package com.ecom.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


@MapperScan("com.ecom.member.dao")
@EnableFeignClients(basePackages = "com.ecom.member.feign")
@SpringBootApplication
@EnableDiscoveryClient
public class EcomMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcomMemberApplication.class, args);
    }

}
