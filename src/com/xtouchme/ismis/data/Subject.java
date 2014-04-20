package com.xtouchme.ismis.data;

import org.apache.commons.lang3.text.WordUtils;

/**
 * Contains information for a subject
 * Combined the information found on 'view lacking subjects'
 * @author xtouchme
 *
 */
public class Subject {

	// View Grades info
	private String	courseCode;
	private String	courseTitle;
	private int		units;
	private Grade	midtermGrade;
	private Grade	finalGrade;
	
	// View Lacking Subjects info
	private int		semIndex;
	private Subject	preRequisite;
	private Subject	coRequisite;
	
	public Subject(String courseCode, String courseTitle, String units, String midtermGrade, String finalGrade) {
		this.courseCode = courseCode;
		this.courseTitle = WordUtils.capitalizeFully(courseTitle.replaceAll("&amp;", "&"));
		this.midtermGrade = Grade.parse(midtermGrade.trim());
		this.finalGrade = Grade.parse(finalGrade.trim());
		
		if(units.isEmpty()) this.units = -1;
		else this.units = (int)Float.parseFloat(units);
		
		this.semIndex = -1;
		this.preRequisite = null;
		this.coRequisite = null;
	}
	
	public int units() {
		return units;
	}
	
	public boolean isPassed() {
		return finalGrade.isPassingGrade();
	}
	
	public String toString() {
		return String.format("%s %d %s %s", courseCode, units, midtermGrade, finalGrade);
	}
	
}
