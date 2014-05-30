package com.xtouchme.ismis.data;

import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.xtouchme.utils.Strings;

public class Student {

	//Student details
	private String					internalId;
	private String					idNumber;
	private String					name;
	private String					prospectus;
	private int						yearLevel;
	private String					courseDetails;
	
	//Grades
	private Map<String, Semester>	semesters,
									prospectusList;
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
			this.name		= WordUtils.capitalizeFully(Strings.normalizeHTMLString(data.getString("Student Name")));
			this.prospectus	= data.getString("Student Prospectus");
			this.yearLevel	= Integer.parseInt(data.getString("Year Level"));
		} catch(JSONException e) {
			System.err.printf("Malformed data: %s%n", e.getMessage());
		} catch(NumberFormatException nfe) {
			this.yearLevel	= 0;
		}
	}

	/**
	 * Checks if the student contains valid data
	 * @return true if data is valid, otherwise false
	 */
	public boolean validate() {
		return (!idNumber.isEmpty() || !name.startsWith(","));
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
	
	public String prospectus() {
		return prospectus;
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
	
	public void setCourseDetails(String courseDetails) {
		this.courseDetails = courseDetails;
	}
	
	public String courseDetails() {
		return courseDetails;
	}
	
	public void addSemester(String identifier, Semester semester) {
		if(semesters == null) semesters = new Hashtable<String, Semester>();
		semesters.put(identifier, semester);
	}
	
	public Semester getSemester(String identifier) {
		if(semesters == null) semesters = new Hashtable<String, Semester>();
		return semesters.get(identifier);
	}
	
	public String[] semesterIdentifiers() {
		if(semesters == null) return null;
		return semesters.keySet().toArray(new String[] {});
	}
	
	public void setProspectus(Map<String, Semester> prospectusList) {
		this.prospectusList = prospectusList;
	}
	
	public Semester getProspectusSemester(String sem, String year) {
		if(prospectusList == null) return null;
		return prospectusList.get(String.format("%s %s", sem, year));
	}
	
	public Map<String, Semester> prospectusList() {
		return prospectusList;
	}
	
	public void setGWA(float gwa) {
		this.generalWeightedAverage = gwa;
	}
	
	public float gwa() {
		return generalWeightedAverage;
	}
	
	@Override
	public String toString() {
		return String.format("[%s] %s %s %s", idNumber, name, prospectus, yearLevelString());
	}
	
}
