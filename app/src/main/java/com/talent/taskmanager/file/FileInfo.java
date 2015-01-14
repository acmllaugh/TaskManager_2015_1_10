package com.talent.taskmanager.file;

/**
 * Created by chris on 14-12-30.
 */
public class FileInfo {
    private int userId;
    private int taskId;
    private String filePath;
    private boolean isPicture;
    private int uploadResult;
    private int taskFlowTimes;

    public FileInfo(int userId, int taskId) {
        this.userId = userId;
        this.taskId = taskId;
    }

    public FileInfo(int userId, int taskId, String filePath, boolean isPicture, int uploadResult, int taskFlowTimes) {
        this.userId = userId;
        this.taskId = taskId;
        this.filePath = filePath;
        this.isPicture = isPicture;
        this.uploadResult = uploadResult;
        this.taskFlowTimes = taskFlowTimes;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isPicture() {
        return isPicture;
    }

    public void setPicture(boolean isPicture) {
        this.isPicture = isPicture;
    }

    public int getUploadResult() {
        return uploadResult;
    }

    public void setUploadResult(int uploadResult) {
        this.uploadResult = uploadResult;
    }

    public int getTaskFlowTimes() {
        return taskFlowTimes;
    }

    public void setTaskFlowTimes(int taskFlowTimes) {
        this.taskFlowTimes = taskFlowTimes;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "userId=" + userId +
                ", taskId=" + taskId +
                ", filePath='" + filePath + '\'' +
                ", isPicture=" + isPicture +
                ", uploadResult=" + uploadResult +
                ", taskFlowTimes=" + taskFlowTimes +
                '}';
    }
}
