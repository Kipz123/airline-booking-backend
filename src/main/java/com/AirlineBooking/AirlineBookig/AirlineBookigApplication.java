package com.AirlineBooking.AirlineBookig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AirlineBookigApplication {

	public static void main(String[] args) {
		SpringApplication.run(AirlineBookigApplication.class, args);
	}

}
