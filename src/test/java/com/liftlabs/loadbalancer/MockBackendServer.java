package com.liftlabs.loadbalancer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
public class MockBackendServer {
	
	public static void main(String[] args) {
		SpringApplication.run(MockBackendServer.class, args);
	}

	

	@RestController
	@RequestMapping("/test")
	public static class TestController {
		@GetMapping
		public String testEndpoint() {
			return "Success";
		}
	}

}
