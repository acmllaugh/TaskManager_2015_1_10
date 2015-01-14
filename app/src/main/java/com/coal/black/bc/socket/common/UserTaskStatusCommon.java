package com.coal.black.bc.socket.common;

/**
 * 用户任务状态的Common
 * 
 * @author wanghui-bc
 *
 */
public class UserTaskStatusCommon {
	public static final int NOT_READ = 1;// 未阅读
	public static final int HAS_READED = 2;// 已经阅读
	public static final int IN_DEALING = 3;// 处理中
	public static final int HAS_COMMITED = 4;// 已经提交
	public static final int HAS_FINISHED = 5;// 已结束
	public static final int ROLL_BACK = 6;// 表示已经回退当前任务
}
