package com.coal.black.bc.socket.dto;

import com.coal.black.bc.socket.IDtoBase;

public class CommitTaskDto extends IDtoBase {
	private static final long serialVersionUID = -6600315553385443735L;

	private int taskId;// 任务ID
	private int taskFlowTimes;// 任务的流程次数
	private boolean needVisitAgain;// 是否需要再次访问
	private String visitReport;// 外访报告
	private String realVisitUser;// 实际外访人的名称

	public boolean isNeedVisitAgain() {
		return needVisitAgain;
	}

	public void setNeedVisitAgain(boolean needVisitAgain) {
		this.needVisitAgain = needVisitAgain;
	}

	public String getVisitReport() {
		return visitReport;
	}

	public void setVisitReport(String visitReport) {
		this.visitReport = visitReport;
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

	public String getRealVisitUser() {
		return realVisitUser;
	}

	public void setRealVisitUser(String realVisitUser) {
		this.realVisitUser = realVisitUser;
	}
}
