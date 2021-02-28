package com.restpractice.UserException;

public class UserFormatException extends Exception {
	public UserFormatException (String user) {
		super("User format not valid, " + user);
	}
}
