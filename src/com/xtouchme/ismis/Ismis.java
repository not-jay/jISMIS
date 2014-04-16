package com.xtouchme.ismis;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.xtouchme.ismis.data.Announcement;
import com.xtouchme.ismis.data.BlockStatus;
import com.xtouchme.ismis.data.Student;
import com.xtouchme.utils.Lists;

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
	
	/**
	 * Checks ISMIS for new block list status, silently fails if it encounters any errors
	 * @param session Session to use to check for blocklist
	 */
	public static void checkBlockList(IsmisSession session) {
		if(!session.isLoggedIn()) return;
		
		JSONObject json = Page.requestJSONPost(session, JSON.BLOCK_LIST, "studentId="+session.getUser().getInternalId()+"&page=1&size=5");
		
		int total = 0;
		if(json != null) total = json.getInt("total");
		else return;
		if(total == 0 && json != null) return;
		
		List<BlockStatus> fetched = new ArrayList<>();
		JSONArray data = null;
		
		json = Page.requestJSONPost(session, JSON.BLOCK_LIST, "studentId="+session.getUser().getInternalId()+"&page=1&size="+total);
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
		
		String response = Page.requestPost(session, HTTP.UPDATE_YEARLEVEL, "mango="+session.getUser().getInternalId());
		if(response.equalsIgnoreCase("\"No changes were made to the year level.\"")) return false;
		
		session.setUser(getStudentDetails(session));
		return true;
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
		
		return session;
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
