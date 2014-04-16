package com.xtouchme.ismis.data;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class Student {

	private String internalId;
	private String idNumber;
	private String name;
	private String prospectus;
	private int yearLevel;
	
	/**
	 * Creates a student object using the details from a JSONobject
	 * @param data
	 * @throws JSONException if data is missing a key
	 */
	public Student(JSONObject data) {
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

	public String getInternalId() {
		return internalId;
	}
	
	public String getIdNumber() {
		return idNumber;
	}
	
	public String getName() {
		return name;
	}
	
	public String getYearLevelString() {
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
	
	public int getYearLevel() {
		return yearLevel;
	}
	
	public String getProspectus() {
		return prospectus;
	}
	
	@Override
	public String toString() {
		return String.format("[%s] %s %s %s", idNumber, name, prospectus, getYearLevelString());
	}
	
}
