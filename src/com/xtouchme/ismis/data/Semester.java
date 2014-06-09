package com.xtouchme.ismis.data;

import java.util.Hashtable;
import java.util.Map;

/**
 * Are basically containers for subjects
 * @author xtouchme
 *
 */
public class Semester {

	public enum Term { FIRST, SECOND, SUMMER };
	
	// View Lacking Subjects info
	private Term					term;
	private String					year;
	private Map<String, Subject>	subjects;
	private int						totalUnits;
	
	// View Grades info
	private int						creditedUnits;		//Total units for "PASSED" subjects
	private float					gradePointAverage;
	
	/**
	 * Creates a blank semester
	 * @param caption Table caption found in ISMIS, in the format "[Term Semester | Summer] Year"
	 */
	public Semester(String caption) {
		if(caption == null || caption.isEmpty()) {
			term = null;
			year = null;
		}
		
		if(caption.contains("First") || caption.contains("1st"))		term = Term.FIRST;
		else if(caption.contains("Second") || caption.contains("2nd"))	term = Term.SECOND;
		else if(caption.contains("Summer"))								term = Term.SUMMER;
		else 															term = null;
		
		year = caption.substring(caption.lastIndexOf(' ')).trim();
		
		subjects = null;
		totalUnits = 0;
		gradePointAverage = 0;
		creditedUnits = 0;
	}
	
	public String term() {
		switch(term) {
		case FIRST:		return "First Semester";
		case SECOND:	return "Second Semester";
		case SUMMER:	return "Summer";
		default:		return null;
		}
	}
	
	public String year() {
		return year;
	}
	
	public void addSubject(String code, Subject s) {
		if(subjects == null) subjects = new Hashtable<String, Subject>();
		subjects.put(code, s);
		totalUnits += s.units();
		if(s.isPassed()) creditedUnits += s.units();
	}
	
	public Subject getSubject(String code) {
		if(subjects == null) subjects = new Hashtable<String, Subject>();
		return subjects.get(code);
	}
	
	public Subject[] subjects() {
		return subjects.values().toArray(new Subject[] {});
	}
	
	public void setTotalUnits(int totalUnits) {
		this.totalUnits = totalUnits;
	}
	
	public int totalUnits() {
		return totalUnits;
	}
	
	public int creditedUnits() {
		return creditedUnits;
	}
	
	public void setGPA(float gpa) {
		this.gradePointAverage = gpa;
	}
	
	public float gpa() {
		return gradePointAverage;
	}
	
	public String toString() {
		String sem = String.format("%s %s%n", term(), year);
		if(subjects != null) {
			for(Subject s : subjects()) {
				sem += String.format("  %s%n", s);
			}
		}
		sem += String.format("GPA: %.2f", gradePointAverage);
		return sem;
	}
	
}
