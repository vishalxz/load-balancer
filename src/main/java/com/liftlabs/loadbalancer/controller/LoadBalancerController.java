package com.liftlabs.loadbalancer.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.liftlabs.loadbalancer.alogrithm.LeastConnectionsAlgorithm;
import com.liftlabs.loadbalancer.alogrithm.RandomSelectionAlgorithm;
import com.liftlabs.loadbalancer.alogrithm.RoundRobinAlgorithm;
import com.liftlabs.loadbalancer.exception.NoBackendServersAvailableException;
import com.liftlabs.loadbalancer.exception.RequestForwardingException;
import com.liftlabs.loadbalancer.exception.UnsupportedHttpMethodException;
import com.liftlabs.loadbalancer.service.LoadBalancerService;

@RestController
@RequestMapping("/api/loadbalancer")
public class LoadBalancerController {

	private final LoadBalancerService loadBalancerService;

	@Autowired
	public LoadBalancerController(LoadBalancerService loadBalancerService) {
		this.loadBalancerService = loadBalancerService;
	}
	
	
	@PostMapping("/register")
	public ResponseEntity<String> registerServer(@RequestParam String server) {
		loadBalancerService.registerServer(server);
		return ResponseEntity.ok("Server registered: " + server);
	}

	@DeleteMapping("/remove")
	public ResponseEntity<String> removeServer(@RequestParam String server) {
		loadBalancerService.removeServer(server);
		return ResponseEntity.ok("Server removed: " + server);
	}
	
	@GetMapping("/servers")
	public ResponseEntity<List<String>> getServers() {
		return ResponseEntity.ok(loadBalancerService.getBackendServers());
	}
	
	@GetMapping("/state")
	public ResponseEntity<Map<String, Object>> getState() {
		Map<String, Object> state = new HashMap<>();
		state.put("backendServers", loadBalancerService.getBackendServers());
		state.put("currentAlgorithm", loadBalancerService.getAlgorithmName());
		return ResponseEntity.ok(state);
	}
	
	@PutMapping("/algorithm")
	public ResponseEntity<String> setAlgorithm(@RequestParam String algorithm) {
		switch (algorithm.toLowerCase()) {
			case "roundrobin":
				loadBalancerService.setAlgorithm(new RoundRobinAlgorithm());
				break;
			case "random":
				loadBalancerService.setAlgorithm(new RandomSelectionAlgorithm());
				break;
			case "leastconnections":
				loadBalancerService.setAlgorithm(new LeastConnectionsAlgorithm(loadBalancerService.getConnectionCounts()));
				break;
			default:
				return ResponseEntity.badRequest().body("Unsupported algorithm: " + algorithm);
		}
		return ResponseEntity.ok("Algorithm set to: " + algorithm);
	}
	
	
	@PostMapping("/forward")
	public ResponseEntity<String> forwardRequest(
			@RequestParam String path,
			@RequestParam String method,
			@RequestBody(required = false) String body) {
		try {
			ResponseEntity<String> response = loadBalancerService.forwardHttpRequest(path, method, body);
			return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
		} catch (NoBackendServersAvailableException e) {
			return ResponseEntity.status(503).body(e.getMessage());
		} catch (UnsupportedHttpMethodException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (RequestForwardingException e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}
	
	@GetMapping("/metrics")
	public ResponseEntity<Map<String, Integer>> getMetrics() {
		return ResponseEntity.ok(loadBalancerService.getMetrics());
	}

}
