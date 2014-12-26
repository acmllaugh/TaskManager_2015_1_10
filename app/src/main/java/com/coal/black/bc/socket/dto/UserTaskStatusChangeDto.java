package com.coal.black.bc.socket.dto;

import com.coal.black.bc.socket.IDtoBase;

public class UserTaskStatusChangeDto extends IDtoBase {
	private static final long serialVersionUID = 4870349044208872852L;

	private int taskId;
	private int userId;
	private int userTaskStatus;

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getUserTaskStatus() {
		return userTaskStatus;
	}

	public void setUserTaskStatus(int userTaskStatus) {
		this.userTaskStatus = userTaskStatus;
	}
}
