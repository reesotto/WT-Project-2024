package it.polimi.tiw.utilities;

public class PatternChecker {
	
	private PatternChecker() {
	}
	
	//All functions require a string, it can't be left empty!
	public static boolean isAlphaNumeric(String string) {
		String pattern = "^[a-zA-Z0-9]*$";
		return string.matches(pattern);
	}
	
	public static boolean isFolderPathValid(String string) {
		//String pattern = "^[a-zA-Z0-9]+(\\/[a-zA-Z0-9]+)*$";
		String pattern = "^[a-zA-Z0-9]+( [a-zA-Z0-9]+)*(\\/[a-zA-Z0-9]+( [a-zA-Z0-9]+)*)*$";
		return string.matches(pattern);
	}
	
	public static boolean isNameTypeValid(String string) {
		//String pattern = "^[a-zA-Z0-9]+\\.([a-zA-Z0-9]{1,4})";
		String pattern = "^[a-zA-Z0-9 ]+\\.[a-zA-Z0-9]{1,4}$";
		return string.matches(pattern);
	}
	
	public static boolean isAlphaNumericWithSpace(String string) {
		String pattern = "^[a-zA-Z0-9]+( [a-zA-Z0-9]+)*$";
		return string.matches(pattern);
	}
	
}
