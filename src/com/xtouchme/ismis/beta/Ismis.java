package com.xtouchme.ismis.beta;

public class Ismis {
	/** HTTP URLs */
	public class HTTP {
		public static final String LOGOUT = "http://ismis.usc.edu.ph/Accounts/Logout/";
		public static final String HOME = "http://ismis.usc.edu.ph/";
		public static final String STUDENT_HOME = "http://ismis.usc.edu.ph/Home/Student/";
		public static final String STUDENT_DETAILS = "http://ismis.usc.edu.ph/Student/StudentDetails/";
		public static final String OFFERED_SUBJECTS = "http://ismis.usc.edu.ph/SubjectScheduleForStudent/Index/";
		public static final String UPDATE_YEARLEVEL = "http://ismis.usc.edu.ph/Student/CalculateYearLevel/";
		public static final String LACKING_SUBJECTS = "http://ismis.usc.edu.ph/SubjectsToTake/Index/"; //TODO
		/** 
		 * For schedule, issue a GET with docId = 1
		 * For exam permit, issue a GET with docId = 2
		 **/
		public static final String SCHEDULE = "http://ismis.usc.edu.ph/Student/EnrolledSubject/"; //TODO
		public static final String VIEW_GRADES = "http://ismis.usc.edu.ph/Grades/ViewGrades/"; //TODO
	}
	/** JSON Object URLs */
	public class JSON {
		public static final String ANNOUNCEMENTS = "http://ismis.usc.edu.ph/Announcement/_StudentAnnouncements";
		public static final String BLOCK_LIST = "http://ismis.usc.edu.ph/StudentBlocking/_BlockList";
	}
}
