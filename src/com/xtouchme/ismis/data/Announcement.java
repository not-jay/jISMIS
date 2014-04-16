package com.xtouchme.ismis.data;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.xtouchme.ismis.Ismis;
import com.xtouchme.ismis.IsmisSession;

public class Announcement {

	private int id;
	private String title;
	private String date;
	//private String message;
	//private String sender;
	//private String recipientDepartment;
	//private String recipientRole;
	
	public Announcement(int id, String title, long date) {
		this.id = id;
		this.title = title;
		
		Date d = new Date(date);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
		this.date = sdf.format(d);
	}
	
	public String getDate() {
		return date;
	}
	
	/**
	 * Get an announcement's details
	 * @param session Session used to retrieve the details
	 * @return a {@link JSONObject} that contains the "subject" and "body" of the announcement
	 */
	public JSONObject getDetails(IsmisSession session) {
		Document doc = Jsoup.parse(Ismis.Page.requestGet(session, Ismis.HTTP.ANNOUNCEMENT_DETAILS+"?announcementID="+id),
														 Ismis.HTTP.ANNOUNCEMENT_DETAILS+"?announcementID="+id);
		String subject = doc.getElementById("announcesub").getElementsByTag("h1").get(0).ownText();
		String body = doc.getElementById("announcebody").getElementsByTag("fieldset").get(0).text().replace("Message:", "");
		
		JSONObject details = new JSONObject();
		details.put("subject", subject);
		details.put("body", body);
		
		return details;
	}
	
	@Override
	public String toString() {
		return String.format("[%d] '%s' (%s)", id, title, date);
	}
	
}
