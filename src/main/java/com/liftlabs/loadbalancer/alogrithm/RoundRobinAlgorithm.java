package com.liftlabs.loadbalancer.alogrithm;

import java.util.List;

public class RoundRobinAlgorithm implements LoadBalancingAlgorithm{

	private int index = 0;
	
	@Override
	public String selectServer(List<String> servers) {
		if (servers.isEmpty()) {
			throw new IllegalStateException("No servers available");
		}
		String server = servers.get(index);
		index = (index + 1) % servers.size();
		return server;
	}

}
