package com.liftlabs.loadbalancer.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.liftlabs.loadbalancer.service.LoadBalancerService;

@Configuration
public class ServiceConfig {
	
	
	@Bean
	public LoadBalancerService loadBalancerService(
			@Value("${loadbalancer.backend.servers:}") String backendServers) {
		LoadBalancerService loadBalancerService = new LoadBalancerService();
		Arrays.stream(backendServers.split(","))
				.filter(server -> !server.isBlank())
				.forEach(loadBalancerService::registerServer);
		return loadBalancerService;
	}

}
