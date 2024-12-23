package com.quoctoan.shoestore;

import jdk.jfr.Enabled;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class TechStoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(TechStoreApplication.class, args);
	}

}
