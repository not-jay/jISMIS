package com.xtouchme.ismis.data;

public class BlockStatus {

	private int studentId;
	private int blockedId;
	private int deptId;
	private String deptName;
	private String reason;
	private String status;
	
	public BlockStatus(int studentId, int blockedId, int deptId, String deptName, String reason, String status) {
		this.studentId = studentId;
		this.blockedId = blockedId;
		this.deptId = deptId;
		this.deptName = deptName;
		this.reason = reason;
		this.status = status;
	}
	
	public int getStudentID() {
		return studentId;
	}
	
	public int getBlockedID() {
		return blockedId;
	}
	
	public int getDepartmentID() {
		return deptId;
	}
	
	public String getDepartmentName() {
		return deptName;
	}
	
	public String getReason() {
		return reason;
	}
	
	public String getStatus() {
		return status;
	}
	
	@Override
	public String toString() {
		return String.format("[%s] Dept: %s%n%s", status, deptName, reason);
	}
}
