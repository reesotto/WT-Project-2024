package it.polimi.tiw.exceptions;

public class SameFileNameException extends Exception {
	private static final long serialVersionUID = 1L;
	public SameFileNameException(String message) {
		super(message);
	}
}
