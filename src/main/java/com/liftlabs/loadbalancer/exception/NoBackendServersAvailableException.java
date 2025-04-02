package com.liftlabs.loadbalancer.exception;

public class NoBackendServersAvailableException extends RuntimeException{
	
	public NoBackendServersAvailableException(String message) {
		super(message);
	}

}
