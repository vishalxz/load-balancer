package com.liftlabs.loadbalancer.alogrithm;

import java.util.List;
import java.util.Random;

public class RandomSelectionAlgorithm implements LoadBalancingAlgorithm {

	private final Random random = new Random();

	@Override
	public String selectServer(List<String> servers) {
		if (servers.isEmpty()) {
			throw new IllegalStateException("No servers available");
		}
		return servers.get(random.nextInt(servers.size()));
	}
}
