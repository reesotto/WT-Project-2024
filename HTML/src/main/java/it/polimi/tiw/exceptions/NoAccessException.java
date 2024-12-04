package it.polimi.tiw.exceptions;

public class NoAccessException extends Exception {
	private static final long serialVersionUID = 1L;
	public NoAccessException(String message) {
		super(message);
	}
}