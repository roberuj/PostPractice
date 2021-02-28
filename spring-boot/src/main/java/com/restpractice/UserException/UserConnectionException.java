package com.restpractice.UserException;

public class UserConnectionException extends Exception {
	public UserConnectionException() {
		super("The host or the internet connection is down");
	}
}
