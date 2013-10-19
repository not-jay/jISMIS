package com.xtouchme.ismis;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xtouchme.http.client.methods.HttpRequest;
import com.xtouchme.ismis.data.Student;

public class Ismis {
	
	public boolean isVerbose = false;
	/**
	 * Since ISMIS stores cookies we need, this needs to be persistent
	 * (or more specifically its HttpClient since it's the one storing
	 * the cookies)
	 */
	private HttpRequest request = null;
	
	/**
	 * The current user logged in
	 */
	private Student currentUser = null;
	
	//HTTP request URLs
	public static final String LOGOUT = "http://ismis.usc.edu.ph/Accounts/Logout/";
	public static final String HOME = "http://ismis.usc.edu.ph/";
	public static final String STUDENT_HOME = "http://ismis.usc.edu.ph/Home/Student/";
	public static final String STUDENT_DETAILS = "http://ismis.usc.edu.ph/Student/StudentDetails/";
	public static final String OFFERED_SUBJECTS = "http://ismis.usc.edu.ph/SubjectScheduleForStudent/Index/";
	public static final String UPDATE_YEARLEVEL = "http://ismis.usc.edu.ph/Student/CalculateYearLevel/";
	public static final String LACKING_SUBJECTS = "http://http://ismis.usc.edu.ph/SubjectsToTake/Index/";
	
	//JSON Object request URLs
	public static final String ANNOUNCEMENTS = "http://ismis.usc.edu.ph/Announcement/_StudentAnnouncements";
	
	public Ismis(HttpRequest request) {
		this.request = request;
	}
	
	public void getAnnouncements() {
		//Can only get announcements when logged in
		if(currentUser == null) return;
		if(isVerbose) System.out.println("-- Announcements --");
		
		JSONObject jsonData = requestJSONPost(ANNOUNCEMENTS, "page=1&size=10");
		
		int total = 0;
		JSONArray announcements = null;
		if(jsonData != null) {
			total = jsonData.getInt("total");
			announcements = jsonData.getJSONArray("data");
		} else {
			System.err.println("Error: Unable to fetch Announcements");
		}
		
		if(isVerbose) {
			for(int i = 0; i < announcements.length(); i++) {
				JSONObject data = announcements.getJSONObject(i);
				int id = data.getInt("AnnouncementId");
				String title = data.getString("Title");
				String dateCreated = data.getString("DateCreated");
				long dateMillis = Long.parseLong(dateCreated.substring(dateCreated.indexOf('(')+1,
																	   dateCreated.indexOf(')')));
				Date date = new Date(dateMillis);
				SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
				
				System.out.printf("  [%d] '%s' (%s)%n", id, title, sdf.format(date));
			}
		} else
			System.out.printf("%d Announcement%s received%n", total, (total != 1)?"s":"");
	}
	
	public void logout() {
		//Can only log out when logged in :P
		if(currentUser == null) return;
		
		requestGet(LOGOUT);
		requestGet(HOME);
		
		System.out.println("Logged out!");
	}
	
	public Student updateYearLevel() {
		//Can only update when logged in
		if(currentUser == null) return null;
		
		String response = requestPost(UPDATE_YEARLEVEL, "mango=2821");
		
		if(!response.isEmpty()) return getStudentDetails();
		
		return null;
	}
	
	public boolean login(String username, String password) {
		boolean result = loginAccount(username, password);
		if(!result)	return false; //Login was unsuccessful!
		
		//If login was successful, get student info and redirect to /home/student
		result = getStudentDetails() != null;
		if(!result) return false;
		
		result = finalizeLogin() != null;
		
		return result;
	}
	
	private String finalizeLogin() {
		return requestGet(STUDENT_HOME);
	}
	
	private Student getStudentDetails() {
		/**
		 * http://api.jquery.com/jQuery.ajax/ - read the part about 'data'
		 * ctrl+u'd ismis/home/student
		 * it seems they're doing a GET to /student/studentinformation
		 * with the data object as "mango"=2821
		 * 
		 * So format the GET request URL to be URL?mango=2821
		 */
		String response = requestGet(STUDENT_DETAILS+"?mango=2821");
		
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
		currentUser = new Student(data);
		if(isVerbose) System.out.println(currentUser);
		
		return currentUser;
	}
	
	private boolean loginAccount(String username, String password) {
		requestGet(HOME);
		
		String data = String.format("Username=%s&Password=%s", username, password);
		
		String response = requestPost(HOME, data);
		
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
		else if(isVerbose) System.out.println(response);
		
		JSONObject json = new JSONObject(response);
		
		return json;
	}
	
	private String requestPost(String url, String data) {
		if(isVerbose) System.out.println("Requesting POST to "+url);
		String response = request.sendPost(url, data);
		if(response == null) response = "";
		else if(isVerbose) System.out.println(response);
		
		return response;
	}
	
	private String requestGet(String url) {
		if(isVerbose) System.out.println("Requesting GET to "+url);
		String response = request.sendGet(url);
		if(isVerbose) System.out.println("Response: "+response);
		
		return response;
	}
	
}
