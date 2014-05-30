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
	
	/** Instantiates a session with only an HttpRequest object */
	protected IsmisSession() {
		this.request	= new HttpRequest();
		currentUser		= null;
		announcements	= null;
		blockList		= null;
	}
	
	protected void logout() {
		request			= null;
		currentUser		= null;
		announcements	= null;
		blockList		= null;
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
	
	protected String sendJSONPost(String url, String data) {
		return request.sendJSONPost(url, data);
	}
	
	protected String sendPost(String url, String data) {
		return request.sendPost(url, data);
	}
	
	protected String sendGet(String url) {
		return request.sendGet(url);
	}
}
