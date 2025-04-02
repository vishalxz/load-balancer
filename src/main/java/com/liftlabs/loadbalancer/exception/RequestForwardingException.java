package com.liftlabs.loadbalancer.exception;

public class RequestForwardingException extends RuntimeException {
	public RequestForwardingException(String message) {
		super(message);
	}
}
