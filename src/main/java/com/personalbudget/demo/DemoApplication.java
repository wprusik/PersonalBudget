package com.personalbudget.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

        public static String BASEURL = "http://localhost:8080/";
    
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}
