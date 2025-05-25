package com.efecavusoglu.couriertracking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CourierTrackingApplication {

	public static void main(String[] args) {
		SpringApplication.run(CourierTrackingApplication.class, args);
	}

}
