package com.xtouchme.ismis.data;

public class Student {

	private String studentID;
	private String idNumber;
	private String name;
	private String prospectus;
	private int yearLevel;
	
	/**
	 * This should only be instantiated within the com.xtouchme.ismis.Ismis class
	 * 
	 * @param ismisData - '\r\n' separated key:value pair data of the student
	 */
	public Student(String studentID, String ismisData) {
		this.studentID = studentID;
		
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
		
		//Properly capitalize the name, ALL CAPS is for cruise control lol
		String tempName[] = this.name.replaceAll("^ +| +$|( )+", "$1").split(" "); //Pheex double spaces
		this.name = "";
		for(String s : tempName) {
			s = s.replaceAll("&#209;", "N~"); //Temporarily replace
			s = s.substring(0, 1)+s.substring(1).toLowerCase();
			this.name += " "+s.trim();
		}
		this.name = this.name.substring(1).replaceAll("n~", "ñ").replaceAll("N~", "Ñ"); //Fix "Ñ and ñ"s
	}
	
	public String getIsmisID() {
		return studentID;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		String year = "";
		switch(yearLevel) {
		case 1:
			year = "1st";
		case 2:
			year = "2nd";
		case 3:
			year = "3rd";
		case 4:
			year = "4th";
		case 5:
			year = "5th";
		default:
			year = String.format("%dth", yearLevel);
		}
		year += " year";
		
		if(yearLevel == 0) year = "";
		
		return String.format("[%s] %s%n%s %s", idNumber, name, prospectus, year);
	}
	
}
