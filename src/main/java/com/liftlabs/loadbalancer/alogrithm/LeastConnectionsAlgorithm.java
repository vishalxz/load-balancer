package com.liftlabs.loadbalancer.alogrithm;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class LeastConnectionsAlgorithm implements LoadBalancingAlgorithm{

	private final Map<String, AtomicInteger> connectionCounts;

	public LeastConnectionsAlgorithm(Map<String, AtomicInteger> connectionCounts) {
		this.connectionCounts = connectionCounts;
	}

	@Override
	public String selectServer(List<String> servers) {
		if (servers.isEmpty()) {
			throw new IllegalStateException("No servers available");
		}
		return servers.stream()
				.min(Comparator.comparingInt(server -> connectionCounts.get(server).get()))
				.orElseThrow(() -> new IllegalStateException("No servers available"));
	}
}
