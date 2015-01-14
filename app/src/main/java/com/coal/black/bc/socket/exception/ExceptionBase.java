package com.coal.black.bc.socket.exception;

public class ExceptionBase {
	public static byte NONE_EXCEPTION = 0;
	public static byte SERVER_INNER_EXCEPTION = 1;
	public static byte LOGIN_NOT_ALLOWNED = 2;
	public static byte DATA_LENGTH_NOT_SAME = 3;// 实际接收的数据的长度和ClientInfo中实际需要的数据长度不一致的情况下
	public static byte CLIENT_NO_DEAL_HANDLER = 4;// 客户端没有处理的handler，主要是值在SocketClient中没有加上此种类型的处理
	public static byte USER_TASK_NOT_VALID = 5;// 任务已经失效
	public static byte USER_TASK_TO_HAS_READED_NOT_FROM_NOT_READED = 6;// 变成已读的时候的状态不是从未读取过来的
	public static byte USER_TASK_TO_IN_DEALING_NOT_FROM_HAS_READED = 7;// 变成正在处理的时候不是从已经读取状态过来的
	public static byte USER_TASK_TO_HAS_COMMIT_NOT_FROM_IN_DEALING = 8;// 变成正提交完成的时候不是从正在处理中过来的
	public static byte COMMUNICATION_VALID_FAIL = 9;// 通信之前的验证失败（表示不能通信，失败的原因一般是mac地址和用户id不匹配）
	public static byte OLD_PWD_ERROR = 10;// 旧密码错误
	public static byte USER_TASK_CAN_NOT_CHANGE_TO_THE_STATUS = 11;// 用户状态不能转变为选中的状态（主要是andriod端传入的参数作为限制）
	public static byte USER_TASK_TO_ROLLBACK_NOT_FROM_IN_DEALING = 12;// 变成回退的时候不是从处理中状态过来的
}
