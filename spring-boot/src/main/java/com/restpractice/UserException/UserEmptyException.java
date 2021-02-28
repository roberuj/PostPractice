package com.restpractice.UserException;

public class UserEmptyException extends Exception {
	public UserEmptyException() {
		super("user cannot be empty");
	}
}
