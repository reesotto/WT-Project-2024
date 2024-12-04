package it.polimi.tiw.exceptions;

public class WrongPasswordException extends Exception {
	private static final long serialVersionUID = 1L;
	public WrongPasswordException() {
		super("Wrong password.");
	}
}
