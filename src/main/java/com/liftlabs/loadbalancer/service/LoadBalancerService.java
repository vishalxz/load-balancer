package com.liftlabs.loadbalancer.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.liftlabs.loadbalancer.alogrithm.LoadBalancingAlgorithm;
import com.liftlabs.loadbalancer.alogrithm.RoundRobinAlgorithm;
import com.liftlabs.loadbalancer.exception.NoBackendServersAvailableException;
import com.liftlabs.loadbalancer.exception.RequestForwardingException;
import com.liftlabs.loadbalancer.exception.UnsupportedHttpMethodException;

@Service
public class LoadBalancerService {

	private static final Logger logger = LoggerFactory.getLogger(LoadBalancerService.class);

	private final List<String> backendServers = new ArrayList<>();
	private final Map<String, AtomicInteger> connectionCounts = new HashMap<>();

	private final ReentrantLock lock = new ReentrantLock();

	private LoadBalancingAlgorithm algorithm = new RoundRobinAlgorithm();

	private final RestTemplate restTemplate = new RestTemplate();

	private static final int MAX_RETRIES = 3;
	private final AtomicInteger totalRequests = new AtomicInteger(0);
	private final AtomicInteger failedRequests = new AtomicInteger(0);
	
	
	private final Set<String> unhealthyServers = new HashSet<>();

	public void registerServer(String server) {
		lock.lock();
		try {
			backendServers.add(server);
			connectionCounts.put(server, new AtomicInteger(0)); // Initialize connection count
			logger.info("Server registered: {}", server);
		} finally {
			lock.unlock();
		}
	}

	public void removeServer(String server) {
		lock.lock();
		try {
			backendServers.remove(server);
			connectionCounts.remove(server); // Remove connection count
			logger.info("Server removed: {}", server);
		} finally {
			lock.unlock();
		}
	}

	public List<String> getBackendServers() {
		lock.lock();
		try {
			return new ArrayList<>(backendServers);
		} finally {
			lock.unlock();
		}
	}

	public String getAlgorithmName() {
		return algorithm.getClass().getSimpleName();
	}

	public void setAlgorithm(LoadBalancingAlgorithm algorithm) {
		this.algorithm = algorithm;
		logger.info("Load balancing algorithm changed to: {}", algorithm.getClass().getSimpleName());
	}

	public Map<String, AtomicInteger> getConnectionCounts() {
		return connectionCounts;
	}

	public ResponseEntity<String> forwardHttpRequest(String path, String method, String body) {
		totalRequests.incrementAndGet(); // Increment total requests
		String server;
		lock.lock();
		try {
			if (backendServers.isEmpty()) {
				throw new NoBackendServersAvailableException("No backend servers available");
			}
			server = algorithm.selectServer(backendServers);
		} finally {
			lock.unlock();
		}

		String url = server + path;

		for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
			try {
				switch (method.toUpperCase()) {
				case "GET":
					return restTemplate.getForEntity(url, String.class);
				case "POST":
					return restTemplate.postForEntity(url, body, String.class);
				default:
					logger.error("Unsupported HTTP method: {}", method);
					throw new UnsupportedHttpMethodException("Unsupported HTTP method: " + method);
				}
			} catch (Exception e) {
				logger.error("Attempt {} failed for server: {}. Error: {}", attempt, server, e.getMessage());
				if (attempt == MAX_RETRIES) {
					failedRequests.incrementAndGet(); // Increment failed requests
					throw new RequestForwardingException(
							"Failed to forward request after " + MAX_RETRIES + " attempts");
				}
				try {
					Thread.sleep(100 * attempt); // Exponential backoff
				} catch (InterruptedException interruptedException) {
					Thread.currentThread().interrupt();
					throw new RequestForwardingException("Retry interrupted");
				}
			}
		}
		throw new RequestForwardingException("Unexpected error during request forwarding");
	}

	public Map<String, Integer> getMetrics() {
		Map<String, Integer> metrics = new HashMap<>();
		metrics.put("totalRequests", totalRequests.get());
		metrics.put("failedRequests", failedRequests.get());
		return metrics;
	}
	
	
	//@Scheduled(fixedRate = 10000) // Run every 10 seconds
	public void performHealthCheck() {
		lock.lock();
		try {
			for (String server : new ArrayList<>(backendServers)) {
				if (!isServerHealthy(server)) {
					backendServers.remove(server);
					unhealthyServers.add(server);
					logger.warn("Server marked as unhealthy: {}", server);
				}
			}

			for (String server : new HashSet<>(unhealthyServers)) {
				if (isServerHealthy(server)) {
					backendServers.add(server);
					unhealthyServers.remove(server);
					logger.info("Server marked as healthy again: {}", server);
				}
			}
		} finally {
			lock.unlock();
		}
	}

	private boolean isServerHealthy(String server) {
		try {
			restTemplate.getForEntity(server + "/health", String.class);
			return true;
		} catch (RestClientException e) {
			return false;
		}
	}

}
