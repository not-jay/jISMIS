package com.xtouchme.ismis.data;

import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class Student {

	//Student details
	private String					internalId;
	private String					idNumber;
	private String					name;
	private String					prospectus;
	private int						yearLevel;
	private String					courseDetails;
	
	//Grades
	private Map<String, Semester>	semesters;
	private float					generalWeightedAverage;
	
	/**
	 * Creates a student object using the details from a JSONobject
	 * @param data
	 * @throws JSONException if data is missing a key
	 */
	public Student(JSONObject data) {
		semesters = null;
		generalWeightedAverage = 0;
		
		try {
			this.internalId	= data.getString("Internal Id");
			this.idNumber	= data.getString("Student Id");
			this.name		= WordUtils.capitalizeFully(data.getString("Student Name"));
			this.prospectus	= data.getString("Student Prospectus");
			this.yearLevel	= data.getInt("Year Level");
		} catch(JSONException e) {
			System.err.printf("Malformed data: %s%n", e.getMessage());
		}
	}

	public void setCourseDetails(String courseDetails) {
		this.courseDetails = courseDetails;
	}
	
	public String courseDetails() {
		return courseDetails;
	}
	
	public Semester getSemester(String identifier) {
		if(semesters == null) semesters = new Hashtable<>();
		return semesters.get(identifier);
	}
	
	public void setGWA(float gwa) {
		this.generalWeightedAverage = gwa;
	}
	
	public float gwa() {
		return generalWeightedAverage;
	}
	
	public String internalId() {
		return internalId;
	}
	
	public String idNumber() {
		return idNumber;
	}
	
	public String name() {
		return name;
	}
	
	public String yearLevelString() {
		String year = "";
		
		switch(yearLevel) {
		case 1:	year = "1st"; break;
		case 2: year = "2nd"; break;
		case 3: year = "3rd"; break;
		case 4: year = "4th"; break;
		case 5: year = "5th"; break;
		default: year = String.format("%dth", yearLevel);
		}
		year += " year";
		
		if(yearLevel == 0) year = "";
		
		return year;
	}
	
	public int yearLevel() {
		return yearLevel;
	}
	
	public String prospectus() {
		return prospectus;
	}
	
	@Override
	public String toString() {
		return String.format("[%s] %s %s %s", idNumber, name, prospectus, yearLevelString());
	}
	
}
