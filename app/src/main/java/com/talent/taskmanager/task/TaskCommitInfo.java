package com.talent.taskmanager.task;

/**
 * Created by acmllaugh on 15-2-13.
 */
public class TaskCommitInfo {
    private int taskID;
    private int userID;
    private boolean needVisitAgain;
    private String visitReport;
    private String realVisitUser;

    public TaskCommitInfo(int taskID, int userID, boolean needVisitAgain, String visitReport, String realVisitUser) {
        this.taskID = taskID;
        this.userID = userID;
        this.needVisitAgain = needVisitAgain;
        this.visitReport = visitReport;
        this.realVisitUser = realVisitUser;
    }

    public int getTaskID() {
        return taskID;
    }

    public int getUserID() {
        return userID;
    }

    public boolean isNeedVisitAgain() {
        return needVisitAgain;
    }

    public String getVisitReport() {
        return visitReport;
    }

    public String getRealVisitUser() {
        return realVisitUser;
    }

}
