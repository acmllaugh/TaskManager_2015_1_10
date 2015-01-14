package com.coal.black.bc.socket.dto;

import java.io.File;

import com.coal.black.bc.socket.IDtoBase;

public class UploadFileDto extends IDtoBase {
	private static final long serialVersionUID = 4405947686571455956L;

	private File clientFile;

	private int taskId;
	private int taskFlowTimes;
	private String fileName;
	private int fileLength;
	private boolean isPicture;

	public File getClientFile() {
		return clientFile;
	}

	public void setClientFile(File clientFile) {
		this.clientFile = clientFile;
		fileName = clientFile.getName();
		fileLength = (int) clientFile.length();
	}

	public String getFileName() {
		return fileName;
	}

	public int getFileLength() {
		return fileLength;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setFileLength(int fileLength) {
		this.fileLength = fileLength;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public int getTaskFlowTimes() {
		return taskFlowTimes;
	}

	public void setTaskFlowTimes(int taskFlowTimes) {
		this.taskFlowTimes = taskFlowTimes;
	}

	public boolean isPicture() {
		return isPicture;
	}

	public void setPicture(boolean isPicture) {
		this.isPicture = isPicture;
	}
}
