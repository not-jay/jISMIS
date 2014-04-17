package com.xtouchme.ismis.data;

/**
 * Contains information for a subject
 * Combined the information found on 'view
 * @author xtouchme
 *
 */
public class Subject {

	// View Grades info
	private String	courseCode;
	private String	courseTitle;
	private int		units;
	private float	midtermGrade;
	private float	finalGrade;
	
	// View Lacking Subjects info
	private int		semIndex;
	private Subject	preRequisite;
	private Subject	coRequisite;
	
}
