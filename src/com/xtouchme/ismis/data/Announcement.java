package com.xtouchme.ismis.data;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.xtouchme.http.client.methods.HttpRequest;

public class Announcement {

	private int id;
	private String title;
	private String date;
	
	public Announcement(int id, String title, long date) {
		this.id = id;
		this.title = title;
		
		Date d = new Date(date);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
		this.date = sdf.format(d);
	}
	
	public String displayDetails(HttpRequest request) {
		return request.sendGet("http://ismis.usc.edu.ph/Announcement/DisplayAnnouncement?announcementID="+id);
	}
	
	@Override
	public String toString() {
		
		return String.format("[%d] '%s' (%s)%n", id, title, date);
	}
	
}
