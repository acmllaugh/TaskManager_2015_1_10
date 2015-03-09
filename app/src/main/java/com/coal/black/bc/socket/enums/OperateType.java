package com.coal.black.bc.socket.enums;

public enum OperateType {
	Error((byte) 0), // 表示错误的状态
	LoginIn((byte) 1), // 登陆
	SingIn((byte) 2), // 签到
	TaskQuery((byte) 3), // 接收任务
	UploadFile((byte) 4), // 上传文件
	UserTaskStatusChange((byte) 5), // 用户任务的状态改变
	TaskQryUserNewTaskCount((byte) 6), // 查询用户新的任务的数量
	TaskQryUserNewTaskList((byte) 7), // 查询用户新的任务的列表
	TaskQryByID((byte) 8), // 根据用户Id查询任务列表信息
	ChangePwd((byte) 9), // 修改用户密码
	CommitTask((byte) 10), // 提交任务
	ApkLastedVersionQuery((byte) 11);// 查询Apk的最新版本

	private byte value;

	private OperateType(byte value) {
		this.value = value;
	}

	public byte getValue() {
		return value;
	}

	public static OperateType getOperateType(byte value) {
		switch (value) {
		case 1:
			return LoginIn;
		case 2:
			return SingIn;
		case 3:
			return TaskQuery;
		case 4:
			return UploadFile;
		case 5:
			return UserTaskStatusChange;
		case 6:
			return TaskQryUserNewTaskCount;
		case 7:
			return TaskQryUserNewTaskList;
		case 8:
			return TaskQryByID;
		case 9:
			return ChangePwd;
		case 10:
			return CommitTask;// 提交任务
		case 11:
			return ApkLastedVersionQuery;
		default:
			return Error;
		}
	}
}
