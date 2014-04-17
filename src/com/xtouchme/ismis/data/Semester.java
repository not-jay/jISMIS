package com.xtouchme.ismis.data;

import java.util.List;

/**
 * Are basically containers for subjects
 * @author xtouchme
 *
 */
public class Semester {

	public enum Term { FIRST, SECOND, SUMMER };
	
	// View Lacking Subjects info
	private Term			term;
	private String			year;
	private int				units;
	private List<Subject>	subjects;
	private int				totalUnits;
	
	// View Grades info
	private float			gradePointAverage;
	
}
