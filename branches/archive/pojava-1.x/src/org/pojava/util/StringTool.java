package org.pojava.util;
/*
Copyright 2008 John Pile

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

public class StringTool {

	/**
	 * Returns a zero-based offset if the left-most characters of str match all
	 * of the characters of any member of list.
	 * 
	 * @param list
	 * @param str
	 * @return -1 if no match, else indexed offset
	 */
	public static int indexedStartMatch(String[] list, String str) {
		for (int i = 0; i < list.length; i++) {
			int len = list[i].length();
			if (str.length() >= len && list[i].equals(str.substring(0, len))) {
				return i;
			}
		}
		return -1;
	}

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
		if (str == null) {
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
	
	/**
	 * Capitalize the first character of a String.
	 * @param str
	 * @return
	 */
	public static String capitalize(String str) {
		char[] chars=str.toCharArray();
		chars[0]=Character.toUpperCase(chars[0]);
		return new String(chars);
	}
	
	/**
	 * Convert this_style to thisStyle.
	 * @param str
	 * @return
	 */
	public static String camelFromUnderscore(String str) {
		if (str==null) return null;
		StringBuffer sb=new StringBuffer();
		char[] a=str.toCharArray();
		boolean up=false;
		for (int i=0; i<a.length; i++) {
			char c=a[i];
			if (c=='_') {
				up=true;
			} else {
				if (up) {
					up=false;
					if (c>='0' && c<='9') {
						sb.append('_');
						sb.append(c);
					} else {
						sb.append(Character.toUpperCase(c));
					}
				} else {
					sb.append(c);
				}
			}
		}
		return sb.toString();
	}

}
