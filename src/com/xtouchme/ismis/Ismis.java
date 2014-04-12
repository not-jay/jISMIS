package com.xtouchme.ismis;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xtouchme.http.client.methods.HttpRequest;
import com.xtouchme.ismis.data.Announcement;
import com.xtouchme.ismis.data.BlockStatus;
import com.xtouchme.ismis.data.Student;

public class Ismis {
	
	public boolean isVerbose	= false;
	public boolean noResponses	= true;
	/**
	 * Since ISMIS stores cookies we need, this needs to be persistent
	 * (or more specifically its HttpClient since it's the one storing
	 * the cookies)
	 */
	private HttpRequest request = null;
	/** The current user logged in */
	private Student currentUser = null;
	/** List of announcements currently */
	private List<Announcement> announcements = null;
	/** Blocked Status */
	private List<BlockStatus> blockList = null;
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
	
	public Ismis(HttpRequest request) {
		this.request = request;
	}
	
	public JSONArray getGrades() { //TODO
		if(currentUser == null) return null;
		
		if(isVerbose) System.out.println("-- Grades --");
		
		JSONArray grades = new JSONArray();
		
		return grades;
	}
	
	public BlockStatus[] getBlockList() {
		return blockList.toArray(new BlockStatus[] {});
	}
	
	public void checkBlockList() {
		//Can only check for block status when logged in
		if(currentUser == null) return;
		
		if(isVerbose) System.out.println("-- Block List/Status --");
		
		JSONObject jsonData = requestJSONPost(JSON.BLOCK_LIST, "studentId="+currentUser.getIsmisID()+"&page=1&size=5");
		
		int total = 0;
		if(jsonData != null) total = jsonData.getInt("total");
		else System.err.printf("Error: Invalid BlockList size, %d", total);
		
		if(total == 0 && jsonData != null) {
			System.out.println("There are no new entries in the BlockList");
			return;
		}
		
		if(blockList != null) blockList.clear();
		blockList = new ArrayList<>();
		
		JSONArray data = null;
		jsonData = requestJSONPost(JSON.BLOCK_LIST, "studentId="+currentUser.getIsmisID()+"&page=1&size="+total);
		if(jsonData != null) {
			data = jsonData.getJSONArray("data");
		} else {
			System.err.println("Error: Unable to fetch BlockList");
		}
		
		for(int i = 0; i < data.length(); i++) {
			JSONObject obj = data.getJSONObject(i);
			int studentId = obj.getInt("StudentId");
			int blockedId = obj.getInt("BlockedStudentId");
			int deptId = obj.getInt("DepartmentId");
			String deptName = obj.getString("DepartmentName");
			String reason = obj.getString("Reason");
			String status = obj.getString("Status");
			
			BlockStatus b = new BlockStatus(studentId, blockedId, deptId, deptName, reason, status);
			blockList.add(b);
			if(isVerbose) System.out.println("  "+b);
		}
		if(!isVerbose) System.out.printf("Your account is %s, with %d citation%s%n",
										(blockList.isEmpty())?"okay":"blocked",
										total, (total != 1)?"s":"");
	}
	
	public Announcement[] getAnnouncements() {
		return announcements.toArray(new Announcement[]{});
	}
	
	public void checkAnnouncements() {
		//Can only get announcements when logged in
		if(currentUser == null) return;
		
		if(isVerbose) System.out.println("-- Announcements --");
		
		JSONObject jsonData = requestJSONPost(JSON.ANNOUNCEMENTS, "page=1&size=10");
		
		int total = 0;
		if(jsonData != null) total = jsonData.getInt("total");
		else System.err.printf("Error: Invalid Announcement size, %d", total);
		
		if(total == 0 && jsonData != null) {
			System.out.println("There are no new Announcements");
			return;
		}
		
		if(announcements != null) announcements.clear();
		announcements = new ArrayList<>();
		
		JSONArray data = null;
		jsonData = requestJSONPost(JSON.ANNOUNCEMENTS, "page=1&size="+total);
		if(jsonData != null) {
			data = jsonData.getJSONArray("data");
		} else {
			System.err.println("Error: Unable to fetch Announcements");
		}
		
		for(int i = 0; i < data.length(); i++) {
			JSONObject obj = data.getJSONObject(i);
			int id = obj.getInt("AnnouncementId");
			String title = obj.getString("Title");
			String dateCreated = obj.getString("DateCreated");
			long dateMillis = Long.parseLong(dateCreated.substring(dateCreated.indexOf('(')+1,
																   dateCreated.indexOf(')')));
			
			Announcement a = new Announcement(id, title, dateMillis);
			announcements.add(a);
			if(isVerbose) System.out.println("  "+a);
		}
		if(!isVerbose) System.out.printf("%d Announcement%s received%n", total, (total != 1)?"s":"");
	}
	
	public void logout() {
		//Can only log out when logged in :P
		if(currentUser == null) return;
		
		requestGet(HTTP.LOGOUT);
		requestGet(HTTP.HOME);
		currentUser = null;
		
		System.out.println("Logged out!");
	}
	
	public Student updateYearLevel() {
		//Can only update when logged in
		if(currentUser == null) return null;
		
		String response = requestPost(HTTP.UPDATE_YEARLEVEL, "mango="+currentUser.getIsmisID());
		System.out.println(response);
		
		if(!response.isEmpty()) return getStudentDetails();
		
		return null;
	}
	
	public boolean login(String username, String password) {
		boolean result = loginAccount(username, password);
		if(!result)	return false; //Login was unsuccessful!
		
		//If login was successful, redirect to /home/student and then get student info
		result = getStudentDetails() != null;
		if(!result) return false;
		
		return result;
	}
	
	private Student getStudentDetails() {
		String response = requestGet(HTTP.STUDENT_HOME);
		/** Apparently, the mango value differs per account */
		Pattern mango = Pattern.compile("\\\"mango\\\": [0-9]*");
		Matcher matcher = mango.matcher(response);
		String id = "";
		if(matcher.find()) id = matcher.group().split(":")[1].trim();
		
		/**
		 * http://api.jquery.com/jQuery.ajax/ - read the part about 'data'
		 * ctrl+u'd ismis/home/student
		 * it seems they're doing a GET to /student/studentinformation
		 * with the data object as "mango"=id
		 * 
		 * So format the GET request URL to be URL?mango=id
		 */
		response = requestGet(HTTP.STUDENT_DETAILS+"?mango="+id);
		
		//Split and trim to an array
		//My reg-ex foo isn't strong enough to combine these to one reg-ex
		//I know you can, but, "</?[table|tr|td]>", doesn't work for me
		response = response.replaceAll("</?table>", "").trim(); //remove <table></table>
		response = response.replaceAll("</?tr>", ""); //remove <tr></tr>
		response = response.replaceAll("</?td>", ""); //remove <td></td>
		response = response.replaceAll("    ", "");
		
		String temp[] = response.split("[\r\n|\r|\n]"); //Split by cr+lf/cr/lf
		
		//Clean array by removing empty elements, and reformatting the data to
		//Key:Value,[Key:Value]
		String data = "";
		boolean isPaired = false; //pair flag for formatting
		for(int x = 0, y = 0; x < temp.length; x++) {
			if(temp[x].isEmpty()) continue;
			if(isPaired) {
				data += "\r\n"+temp[x];
				isPaired = false;
			}
			else {
				if(y%2 == 0 && y != 0) isPaired = true;
				data += temp[x];
				y++;
			}
			
		}
		
		//Save details
		System.out.print("Data: "+data);
		currentUser = new Student(id, data);
		if(isVerbose) System.out.println(currentUser);
		
		return currentUser;
	}
	
	private boolean loginAccount(String username, String password) {
		requestGet(HTTP.HOME);
		
		String data = String.format("Username=%s&Password=%s", username, password);
		
		String response = requestPost(HTTP.HOME, data);
		
		if(!isVerbose) return response.contains("href=\"/Home/Student\"");
		
		if(response.contains("href=\"/Home/Student\"")) {
			System.out.println("Login complete! Redirecting to /Home/Student/");
			return true;
		}
		else if(response.contains("href=\"/\"")) System.out.println("Credentials invalid!");
		else System.out.println("Unhandled response! Assuming log-in failed /!\\");
		
		//If we got this far, this means it entered one of the elses
		return false;
	}
	
	public Student getStudent() {
		return currentUser;
	}
	
	private JSONObject requestJSONPost(String url, String data) {
		if(isVerbose) System.out.println("Requesting POST to "+url);
		if(isVerbose) System.out.println("** Expecting JSON Object response");
		String response = request.sendJSONPost(url, data);
		if(response == null) response = "";
		else if(!noResponses) System.out.println(response);
		
		JSONObject json = new JSONObject(response);
		
		return json;
	}
	
	private String requestPost(String url, String data) {
		if(isVerbose) System.out.println("Requesting POST to "+url);
		String response = request.sendPost(url, data);
		if(response == null) response = "";
		else if(!noResponses) System.out.println(response);
		
		return response;
	}
	
	private String requestGet(String url) {
		if(isVerbose) System.out.println("Requesting GET to "+url);
		String response = request.sendGet(url);
		if(!noResponses) System.out.println("Response: "+response);
		
		return response;
	}
	
}
