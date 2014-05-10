package com.xtouchme.ismis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.xtouchme.ismis.data.Announcement;
import com.xtouchme.ismis.data.BlockStatus;
import com.xtouchme.ismis.data.Semester;
import com.xtouchme.ismis.data.Student;
import com.xtouchme.ismis.data.Subject;
import com.xtouchme.utils.Lists;

public class Ismis {
	/** HTTP URLs */
	public class HTTP {
		public static final String LOGOUT = "http://ismis.usc.edu.ph/Accounts/Logout/";
		public static final String HOME = "http://ismis.usc.edu.ph/";
		public static final String STUDENT_HOME = "http://ismis.usc.edu.ph/Home/Student/";
		public static final String CHANGE_PASSWORD = "http://ismis.usc.edu.ph/Accounts/ChangePassword/";
		public static final String STUDENT_DETAILS = "http://ismis.usc.edu.ph/Student/StudentDetails/";
		public static final String OFFERED_SUBJECTS = "http://ismis.usc.edu.ph/SubjectScheduleForStudent/Index/";
		public static final String UPDATE_YEARLEVEL = "http://ismis.usc.edu.ph/Student/CalculateYearLevel/";
		public static final String LACKING_SUBJECTS = "http://ismis.usc.edu.ph/SubjectsToTake/Index/"; //TODO
		/** 
		 * For schedule, issue a GET with docId = 1
		 * For exam permit, issue a GET with docId = 2
		 **/
		public static final String SCHEDULE = "http://ismis.usc.edu.ph/Student/EnrolledSubject/"; //TODO
		public static final String VIEW_GRADES = "http://ismis.usc.edu.ph/Grades/ViewGrades/";
		
		public static final String ANNOUNCEMENT_DETAILS = "http://ismis.usc.edu.ph/Announcement/DisplayAnnouncement";
	}
	/** JSON Object URLs */
	public class JSON {
		public static final String ANNOUNCEMENTS = "http://ismis.usc.edu.ph/Announcement/_StudentAnnouncements";
		public static final String BLOCK_LIST = "http://ismis.usc.edu.ph/StudentBlocking/_BlockList";
	}
	/** Utility methods */
	public static class Page {
		public static JSONObject requestJSONPost(IsmisSession is, String url, String data) {
//			if(isVerbose) System.out.println("Requesting POST to "+url);
//			if(isVerbose) System.out.println("** Expecting JSON Object response");
			String response = is.sendJSONPost(url, data);
			if(response == null) response = "";
//			else if(!noResponses) System.out.println(response);
			
			JSONObject json = new JSONObject(response);
			
			return json;
		}
		public static String requestPost(IsmisSession is, String url, String data) {
//			if(isVerbose) System.out.println("Requesting POST to "+url);
			String response = is.sendPost(url, data);
			if(response == null) response = "";
//			else if(!noResponses) System.out.println(response);
			
			return response;
		}
		public static String requestGet(IsmisSession is, String url) {
//			if(isVerbose) System.out.println("Requesting GET to "+url);
			String response = is.sendGet(url);
//			if(!noResponses) System.out.println("Response: "+response);
			
			return response;
		}
	}
	
	//TODO List:
	 /*
	  * - Error checking
	  */
	
	/**
	 * Checks a pages availability
	 * @param pageURL URL of the page to test
	 * @return true if page is available, false otherwise
	 */
	public static boolean checkConnection(IsmisSession session, String pageURL) {
		Document docRef = Jsoup.parse(pageURL, pageURL);
		return !docRef.title().isEmpty() && !docRef.body().equals(pageURL);
	}
	
	/**
	 * Checks the view lacking subject grades
	 * @param session Session to use to view the page
	 */
	public static void viewLackingSubjects(IsmisSession session) {
		//Get remaining info from View Lacking Subjects
		Document doc = Jsoup.parse(Page.requestGet(session, HTTP.LACKING_SUBJECTS), HTTP.LACKING_SUBJECTS);
		Element grades = doc.getElementById("areaToPrint");
		
		Map<String, Semester> prospectus = new HashMap<>();
		Map<String, String> preReq = new HashMap<>();
		Map<String, String> coReq = new HashMap<>();
		Semester current = null;
		String id = "";
		
		for(Element e : grades.getElementsByTag("tbody").get(0).getElementsByTag("tr")) {
			Elements data = e.getElementsByTag("td");
			
			if(e.text().contains("Year") && (e.text().contains("SEMESTER") || e.text().contains("SUMMER"))) {
				String year = WordUtils.capitalizeFully(data.get(1).html().trim());
				String sem = WordUtils.capitalizeFully(data.get(2).html().trim());
				
				if(year.startsWith("First")) year = "1";
				else if(year.startsWith("Second")) year = "2";
				else if(year.startsWith("Third")) year = "3";
				else if(year.startsWith("Fourth")) year = "4";
				else if(year.startsWith("Fifth")) year = "5";
				else year = year.split(" ")[0];
				
				id = String.format("%s %s", sem, year);
				current = new Semester(String.format("%s %s", sem, year));
			} else if(e.text().contains("Total Units")) {
				current.setTotalUnits(Integer.parseInt(data.get(3).html().trim()));
				prospectus.put(id, current);
			} else if(!e.hasText() || e.text().contains("#") || e.text().equals(" "))
				continue;
			else {
				String index = data.get(0).html();
				String code = data.get(1).html();
				String title = data.get(2).html();
				String units = data.get(3).html();
				String pre = data.get(4).html();
				String co = data.get(5).html();
				String fg = data.get(6).html();
				
				if(!co.trim().isEmpty()) coReq.put(code, co);
				if(!pre.trim().isEmpty()) preReq.put(code, pre);
				
				Subject subject = new Subject(code, title, units, "", fg);
				subject.setIndex(index);
				
				current.addSubject(code, subject);
			}
		}
		
		session.user().setProspectus(prospectus);
	}
	
	/**
	 * Checks the grades
	 * @param session Session to use to view the grades
	 */
	public static void viewGrades(IsmisSession session) {
		if(!session.isLoggedIn()) return;
		
		//Parse View Grades
		Document doc = Jsoup.parse(Page.requestGet(session, HTTP.VIEW_GRADES), HTTP.VIEW_GRADES);
		
		Element error;
		if(!((error = doc.getElementById("main")) != null && error.text().contains("settle your dues"))) {
			Element grades = doc.getElementById("areaToPrint");
			
			for(Element e : grades.getElementsByClass("grdgradeTable")) {
				String semId = WordUtils.capitalizeFully(e.getElementsByTag("caption").get(0).html());
				if(semId.contains("Accredited Subjects")) continue;
				
				Semester sem = new Semester(semId);
				
				for(Element sub : e.getElementsByTag("tbody").get(0).getElementsByTag("tr")) {
					Elements data = sub.getElementsByTag("td");
					
					String code = data.get(0).html();
					String title = data.get(1).html();
					String units = data.get(2).html();
					String mg = data.get(3).html();
					String fg = data.get(4).html();
					
					if(code.isEmpty()) sem.setGPA(Float.parseFloat(data.get(4).html()));
					else sem.addSubject(code, new Subject(code, title, units, mg, fg));
				}
				session.user().addSemester(semId, sem);
			}
			
			//Gets the GWA from View Grades
			for(Element e : grades.getElementsByClass("grdSemGrades")) {
				if(e.getElementsByTag("table").size() != 0) continue;
				session.user().setGWA(Float.parseFloat(e.getElementsByTag("span").get(0).html()));
			}
		}
	}
	
	/**
	 * Checks ISMIS for new block list status, silently fails if it encounters any errors
	 * @param session Session to use to check for blocklist
	 */
	public static void checkBlockList(IsmisSession session) {
		if(!session.isLoggedIn()) return;
		
		JSONObject json = Page.requestJSONPost(session, JSON.BLOCK_LIST, "studentId="+session.user().internalId()+"&page=1&size=5");
		
		int total = 0;
		if(json != null) total = json.getInt("total");
		else return;
		if(total == 0 && json != null) return;
		
		List<BlockStatus> fetched = new ArrayList<>();
		JSONArray data = null;
		
		json = Page.requestJSONPost(session, JSON.BLOCK_LIST, "studentId="+session.user().internalId()+"&page=1&size="+total);
		if(json != null) data = json.getJSONArray("data");
		else return;
		
		for(int i = 0; i < data.length(); i++) {
			JSONObject obj = data.getJSONObject(i);
			int studentId = obj.getInt("StudentId");
			int blockedId = obj.getInt("BlockedStudentId");
			int deptId = obj.getInt("DepartmentId");
			String deptName = obj.getString("DepartmentName");
			String reason = obj.getString("Reason");
			String status = obj.getString("Status");
			
			BlockStatus b = new BlockStatus(studentId, blockedId, deptId, deptName, reason, status);
			fetched.add(b);
		}
		
		List<BlockStatus> newBlockList = Lists.arraylistDiff(fetched, session.blockList());
		if(newBlockList != null) session.addBlockStatus(newBlockList);
	}
	
	/**
	 * Checks ISMIS for new announcements, silently fails if it encounters any errors
	 * @param session Session to use to check for updates
	 */
	public static void checkAnnouncements(IsmisSession session) {
		if(!session.isLoggedIn()) return;
		
		JSONObject json = Page.requestJSONPost(session, JSON.ANNOUNCEMENTS, "page=1&size=10");
		
		int total = 0;
		if(json != null) total = json.getInt("total");
		else return;
		if(total == 0 && json != null) return;
		
		List<Announcement> fetched = new ArrayList<>();
		JSONArray data = null;
		
		json = Page.requestJSONPost(session, JSON.ANNOUNCEMENTS, "page=1&size="+total);
		if(json != null) data = json.getJSONArray("data");
		else return;
		
		for(int i = 0; i < data.length(); i++) {
			JSONObject obj = data.getJSONObject(i);
			int id = obj.getInt("AnnouncementId");
			String title = obj.getString("Title");
			String dateCreated = obj.getString("DateCreated");
			long dateMillis = Long.parseLong(dateCreated.substring(dateCreated.indexOf('(')+1,
																   dateCreated.indexOf(')')));
			
			Announcement a = new Announcement(id, title, dateMillis);
			fetched.add(a);
		}
		
		List<Announcement> newAnnouncements = Lists.arraylistDiff(fetched, session.announcements());
		if(newAnnouncements != null) session.addAnnouncements(newAnnouncements);
	}
	
	/**
	 * Attempts to update the user's year level. Updates the student object if there are updates
	 * @param session Session to update
	 * @return true if year level was updated, false otherwise
	 */
	public static boolean updateYearLevel(IsmisSession session) {
		if(!session.isLoggedIn()) return false;
		
		String response = Page.requestPost(session, HTTP.UPDATE_YEARLEVEL, "mango="+session.user().internalId());
		if(response.equalsIgnoreCase("\"No changes were made to the year level.\"")) return false;
		
		session.setUser(getStudentDetails(session));
		return true;
	}
	
	/**
	 * Changes the current user's password
	 * @param session User's session
	 * @param oldPassword
	 * @param newPassword
	 * @param confirmPassword
	 * @return true if password change was successful, false otherwise
	 */
	public static boolean changePassword(IsmisSession session, String oldPassword, String newPassword, String confirmPassword) {
		if(!session.isLoggedIn()) return false;
		
		Document doc = Jsoup.parse(Page.requestPost(session, HTTP.CHANGE_PASSWORD,
								   "oldPw="+oldPassword+"&newPw="+newPassword+"&confirmPw="+confirmPassword),
								   HTTP.CHANGE_PASSWORD);
		
		return doc.getElementsByClass("messageContainerSuccess").size() != 0;
	}
	
	/**
	 * Logs a session out
	 * @param session Session to logout
	 */
	public static void logout(IsmisSession session) {
		if(!session.isLoggedIn()) return;
		
		Page.requestGet(session, HTTP.LOGOUT);
		Page.requestGet(session, HTTP.HOME);
		session.logout();
	}
	
	/**
	 * Attempts to log-in to ISMIS
	 * @param username
	 * @param password
	 * @return an IsmisSession object for a successful login, null otherwise.
	 */
	public static IsmisSession login(String username, String password) {
		IsmisSession session = new IsmisSession();
		boolean result = loginAccount(session, username, password);
		if(!result) return null;
		
		Student user = getStudentDetails(session);
		if(user == null) return null;
		session.setUser(user);
		getCourseDetails(session);
		
		return session;
	}
	
	/**
	 * Get the current user's course details
	 * @param session Current user's session
	 */
	private static void getCourseDetails(IsmisSession session) {
		Document doc = Jsoup.parse(Page.requestGet(session, HTTP.VIEW_GRADES), HTTP.VIEW_GRADES);
		
		Element error;
		if((error = doc.getElementById("main")) != null && error.text().contains("settle your dues"))
			return;
		
		String courseTitleDetail = WordUtils.capitalizeFully(doc.getElementById("grdcourseDetails").getElementsByTag("p").get(0).html());
		courseTitleDetail = courseTitleDetail.replaceAll("Of", "of").replaceAll("In", "in");
		String courseTitleYear = doc.getElementById("grdcourseDetails").getElementsByTag("p").get(1).html();
		
		session.user().setCourseDetails(String.format("%s %s", courseTitleDetail, courseTitleYear));
	}
	
	/**
	 * Fetches the current user's details
	 * @param session Current user's session
	 * @return a student object for the current user
	 */
	private static Student getStudentDetails(IsmisSession session) {
		String response = Page.requestGet(session, HTTP.STUDENT_HOME);
		Pattern mango = Pattern.compile("\\\"mango\\\": [0-9]*");
		Matcher matcher = mango.matcher(response);
		String id = "";
		if(matcher.find()) id = matcher.group().split(":")[1].trim();
		if(id.isEmpty()) return null;
		
		Document doc = Jsoup.parse(Page.requestGet(session, HTTP.STUDENT_DETAILS+"?mango="+id), HTTP.STUDENT_DETAILS);
		JSONObject studentDetails = new JSONObject();
		studentDetails.put("Internal Id", id);
		
		for(Element e : doc.getElementsByTag("tr")) {
			Elements td = e.getElementsByTag("td");
			String key = td.get(0).html();
			String value = td.get(2).html();
			
			studentDetails.put(key, value);
		}
		
		Student user = new Student(studentDetails);
		
		return user;
	}
	
	/**
	 * Logs a session in
	 * @param session Session to log-in
	 * @param username Student ID Number
	 * @param password Password
	 * @return true if log-in was successful
	 * 		   otherwise, credentials are invalid or an error occurred
	 */
	private static boolean loginAccount(IsmisSession session, String username, String password) {
		Page.requestGet(session, HTTP.HOME);
		String data = String.format("Username=%s&Password=%s", username, password);
		
		Document doc = Jsoup.parse(Page.requestPost(session, HTTP.HOME, data), HTTP.HOME);
		Element redirect = doc.getElementsByTag("a").get(0);
		
		return HTTP.STUDENT_HOME.endsWith(redirect.attr("href")+"/");
	}
}
