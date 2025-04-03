package com.liftlabs.loadbalancer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.liftlabs.loadbalancer.service.LoadBalancerService;

public class LoadBalancerServiceTest {
	
	
	private LoadBalancerService loadBalancerService;
	private RestTemplate restTemplate;

	@BeforeEach
	void setUp() {
		restTemplate = mock(RestTemplate.class);
		loadBalancerService = new LoadBalancerService();
		loadBalancerService.registerServer("http://localhost:8081");
		loadBalancerService.registerServer("http://localhost:8082");
	}
	
	
	/*
	 * @Test void testForwardHttpRequest() {
	 * when(restTemplate.getForEntity("http://localhost:8081/test", String.class))
	 * .thenReturn(ResponseEntity.ok("Success"));
	 * 
	 * ResponseEntity<String> response =
	 * loadBalancerService.forwardHttpRequest("/test", "GET", null);
	 * assertEquals("Success", response.getBody()); }
	 */

}
