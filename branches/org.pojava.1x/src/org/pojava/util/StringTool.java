package org.pojava.util;

public class StringTool {

	/**
	 * True if a string matches /^[-]?[0-9]+$/
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isInteger(String s) {
		if (s == null || s.length() == 0)
			return false;
		char c = s.charAt(0);
		if (s.length() == 1)
			return c >= '0' && c <= '9';
		return (c == '-' || c >= '0' && c <= '9') && onlyDigits(s.substring(1));
	}

	/**
	 * True if a string has only digits in it.
	 * 
	 * @param s
	 * @return
	 */
	public static boolean onlyDigits(String s) {
		if (s == null || s.length() == 0)
			return false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c < '0' || c > '9')
				return false;
		}
		return true;
	}

	/**
	 * True if a string starts with a digit.
	 * 
	 * @param s
	 * @return
	 */
	public static boolean startsWithDigit(String s) {
		if (s == null || s.length() == 0)
			return false;
		char c = s.charAt(0);
		return (c >= '0' && c <= '9');
	}

	/**
	 * Parse an integer from left-to-right until non-digit reached
	 * 
	 * @param str
	 * @return
	 */
	public static int parseIntFragment(String str) {
		if (str==null) {
			return 0;
		}
		int parsed = 0;
		boolean isNeg = false;
		char[] strip = str.toCharArray();
		char c = strip[0];
		if (c == '-') {
			isNeg = true;
		} else if (c >= '0' && c <= '9') {
			parsed = c - '0';
		} else {
			return 0;
		}
		for (int i = 1; i < strip.length; i++) {
			c = strip[i];
			if (c >= '0' && c <= '9') {
				parsed = 10 * parsed + c - '0';
			} else {
				break;
			}
		}
		return isNeg ? -parsed : parsed;
	}

}
