package com.liftlabs.loadbalancer.exception;

public class UnsupportedHttpMethodException extends RuntimeException {

	public UnsupportedHttpMethodException(String message) {
		super(message);
	}
}
