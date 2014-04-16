package com.xtouchme.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Lists {

	/**
	 * Compares two lists and returns a new list containing elements not found in the other list
	 * @param a List A
	 * @param b List B
	 * @return null, if both lists are empty or null<br />
	 * 		   A,	 if b is empty or null<br />
	 * 		   B,	 if a is empty or null<br />
	 * 		   a new List containing both A and B's unique elements (ArrayList), otherwise
	 */
	public static <T> List<T> arraylistDiff(List<T> a, List<T> b) {
		if((a == null && b == null) || (a.isEmpty() && b.isEmpty())) return null;
		if(a == null || a.isEmpty()) return b;
		if(b == null || b.isEmpty()) return a;
		
		List<T> diff = new ArrayList<>();
		for(T element : a) {
			if(!b.contains(element)) diff.add(element);
		}
		for(T element : b) {
			if(!a.contains(element)) diff.add(element);
		}
		
		return diff;
	}
	
	/**
	 * Compares two lists and returns a new list containing elements not found in the other list
	 * @param a List A
	 * @param b List B
	 * @return null, if both lists are empty or null<br />
	 * 		   A,	 if b is empty or null<br />
	 * 		   B,	 if a is empty or null<br />
	 * 		   a new List containing both A and B's unique elements (LinkedList), otherwise
	 */
	public static <T> List<T> linkedlistDiff(List<T> a, List<T> b) {
		if((a == null && b == null) || (a.isEmpty() && b.isEmpty())) return null;
		if(a == null || a.isEmpty()) return b;
		if(b == null || b.isEmpty()) return a;
		
		List<T> diff = new LinkedList<>();
		for(T element : a) {
			if(!b.contains(element)) diff.add(element);
		}
		for(T element : b) {
			if(!a.contains(element)) diff.add(element);
		}
		
		return diff;
	}
	
}
