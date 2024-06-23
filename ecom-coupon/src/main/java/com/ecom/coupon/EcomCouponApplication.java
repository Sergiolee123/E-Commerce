package com.ecom.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@MapperScan("com.ecom.coupon.dao")
@RefreshScope
@SpringBootApplication
@EnableDiscoveryClient
@EnableTransactionManagement
public class EcomCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcomCouponApplication.class, args);
    }

}
