package com.xtouchme.ismis;

import java.util.ArrayList;
import java.util.List;

import com.xtouchme.http.client.methods.HttpRequest;
import com.xtouchme.ismis.data.Announcement;
import com.xtouchme.ismis.data.BlockStatus;
import com.xtouchme.ismis.data.Student;

public class IsmisSession {
	
	private Student				currentUser;
	private HttpRequest			request;
	private List<Announcement>	announcements;
	private List<BlockStatus>	blockList;
	private Error				error;
	
	
	/** Instantiates a session with only an HttpRequest object */
	protected IsmisSession() {
		logout();
		this.request	= new HttpRequest();
	}
	
	protected void logout() {
		request			= null;
		currentUser		= null;
		announcements	= null;
		blockList		= null;
		error			= null;
	}
	
	protected void addBlockStatus(List<BlockStatus> blockList) {
		if(blockList == null) blockList = new ArrayList<BlockStatus>();
		this.blockList.addAll(blockList);
	}
	
	protected List<BlockStatus> blockList() {
		if(blockList == null) blockList = new ArrayList<BlockStatus>();
		return blockList;
	}
	
	public BlockStatus[] getBlockList() {
		if(blockList == null) blockList = new ArrayList<BlockStatus>();
		return blockList.toArray(new BlockStatus[] {});
	}
	
	protected void addAnnouncements(List<Announcement> announcements) {
		if(announcements == null) announcements = new ArrayList<Announcement>();
		this.announcements.addAll(announcements);
	}
	
	protected List<Announcement> announcements() {
		if(announcements == null) announcements = new ArrayList<Announcement>();
		return announcements;
	}
	
	public Announcement[] getAnnouncements() {
		if(announcements == null) announcements = new ArrayList<Announcement>();
		return announcements.toArray(new Announcement[] {});
	}
	
	public boolean isLoggedIn() {
		return currentUser != null;
	}
	
	protected void setUser(Student user) {
		this.currentUser = user;
	}
	
	public Student user() {
		return currentUser;
	}
	
	public void setError(Error error) {
		this.error = error;
	}
	
	public boolean hasError() {
		return error != null;
	}
	
	public Error error() {
		return error;
	}
	
	protected String sendJSONPost(String url, String data) {
		return request.sendJSONPost(url, data);
	}
	
	protected String sendPost(String url, String data) {
		return request.sendPost(url, data);
	}
	
	protected String sendGet(String url) {
		return request.sendGet(url);
	}
	
	public static class Error {
		
		public static final Error	INVALID_CREDENTIALS = new Error("The username and/or password you entered is incorrect. Please try again.");
		public static final Error	UNHANDLED_RESPONSE	= new Error("Unhandled Response! Assuming Login has failed.");
		
		private String message;
		private Error(String message) {
			this.message = message;
		}
		@Override
		public String toString() {
			return message;
		}
	}
	
}
