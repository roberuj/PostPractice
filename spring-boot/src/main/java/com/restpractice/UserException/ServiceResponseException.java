package com.restpractice.UserException;

public class ServiceResponseException extends Exception {
	public ServiceResponseException() {
		super("Incorrect response from server");
	}
}
