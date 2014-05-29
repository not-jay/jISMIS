package com.xtouchme.ismis.data;

public class Grade {

	public enum Type { NUMBER, LETTER };
	public enum LetterGrade { BLANK, NC, INC, NG };
	
	private Type		type;
	private float		grade;
	private LetterGrade	letterGrade;
	
	private Grade() {}
	
	public boolean isPassingGrade() {
		return (type == Type.LETTER)?false:(grade <= 3);
	}
	
	public String toString() {
		switch(type) {
		case LETTER:
			switch(letterGrade) {
			case BLANK:		return "";
			default:		return letterGrade.name();
			}
		default:
		case NUMBER:		return String.format("%.1f", grade);
		}
	}
	
	public static Grade parse(String gradeString) {		
		Grade grade = new Grade();
		
		try {
			grade.grade = Float.parseFloat(gradeString);
			grade.type = Type.NUMBER;
		} catch(NumberFormatException e) {
			if(gradeString.contains("INC"))			grade.letterGrade = LetterGrade.INC;
			else if(gradeString.contains("NC"))		grade.letterGrade = LetterGrade.NC;
			else if(gradeString.contains("NG"))		grade.letterGrade = LetterGrade.NG;
			else									grade.letterGrade = LetterGrade.BLANK;
			grade.type = Type.LETTER;
		}
		
		return grade;
	}

}
