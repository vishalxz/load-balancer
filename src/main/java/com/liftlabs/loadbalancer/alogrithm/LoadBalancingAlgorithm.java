package com.liftlabs.loadbalancer.alogrithm;

import java.util.List;

public interface LoadBalancingAlgorithm {
	String selectServer(List<String> servers);

}
