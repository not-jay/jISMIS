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
	 * TODO: Pending removal
	 * 
	 * This should only be instantiated within the com.xtouchme.ismis.Ismis class
	 * 
	 * @param ismisData - '\r\n' separated key:value pair data of the student
	 */
	public Student(String studentID, String ismisData) {
		this.internalId = studentID;
		
		String data[] = ismisData.split("\r\n");
		
		this.idNumber = data[0].split(":")[1];
		this.name = data[1].split(":")[1];
		
		//Only happens when Student Prospectus is empty
		//Which only happens on old? accounts
		if(!ismisData.contains("Student Prospectus:Year Level")) {
			this.prospectus = data[2].split(":")[1];
			this.yearLevel = Integer.parseInt(data[3].split(":")[1]);
		} else
			prospectus = "";
		
		//Properly capitalize the name, ALL CAPS is cruise control for cool lol
		String tempName[] = this.name.replaceAll("^ +| +$|( )+", "$1").split(" "); //Pheex double spaces
		this.name = "";
		for(String s : tempName) {
			s = s.replaceAll("&#209;", "N~"); //Temporarily replace
			s = s.substring(0, 1)+s.substring(1).toLowerCase();
			this.name += " "+s.trim();
		}
		this.name = this.name.substring(1).replaceAll("n~", "ñ").replaceAll("N~", "Ñ"); //Fix "Ñ and ñ"s
	}
	
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
