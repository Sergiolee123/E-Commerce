package com.ecom.thirdParty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class EcomThirdPartyApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcomThirdPartyApplication.class, args);
	}

}
