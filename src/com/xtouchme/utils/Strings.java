package com.xtouchme.utils;

public class Strings {

	private static String symbols[][] = {
		{ "&ntilde;",	"ñ" },
		{ "&amp;",		"&" },
		{ "&uuml;",		"ü" },
		{ "&quot;",		"\"" },
		{ "&eacute;",	"é" }
	};
	
	/**
	 * Replaces html escaped unicode characters with their proper symbol.
	 * <pre>
	 * {@code &amp; }
	 * would turn to
	 * {@code & }
	 * </pre>
	 * @param htmlSafeString String containing html escaped unicode characters
	 * @return A string with the escaped characters replaced with their symbol counterparts
	 */
	public static String normalizeHTMLString(String htmlSafeString) {
		for(String symbol[] : symbols) {
			htmlSafeString = htmlSafeString.replaceAll(symbol[0], symbol[1]);
		}
		
		return htmlSafeString;
	}
	
	/**
	 * Replaces symbols with their html escape codes
	 * @param htmlString String containing symbols
	 * @return A string with symbols escaped properly
	 */
	public static String escapeHTMLString(String htmlString) {
		for(String symbol[] : symbols) {
			htmlString = htmlString.replaceAll(symbol[1], symbol[0]);
		}
		
		return htmlString;
	}
	
}
