package com.sw3.reservation_microservice;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class ReservationMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservationMicroserviceApplication.class, args);
	}

}
