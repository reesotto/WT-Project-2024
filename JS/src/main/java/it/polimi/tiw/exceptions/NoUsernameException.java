package it.polimi.tiw.exceptions;

public class NoUsernameException extends Exception {
	private static final long serialVersionUID = 1L;
	public NoUsernameException() {
		super("No user was found with such username.");
	}
}
