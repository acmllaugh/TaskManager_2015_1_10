package com.coal.black.bc.socket;

public class Constants {
	public static final String BC_HOME_STR = "BC_HOME";

	public static byte[] CLIENT_BEGIN_FLAG = new byte[] { 64, 35, 94, 38 };// 就是@#^&
	public static byte[] SERVER_BEGIN_FLAG = new byte[] { 38, 94, 35, 64 };// 就是&^#@

	public static String SING_IN_CODER_TO_WIRE_ERROR = "Sign.In.Coder.To.Wire.Exception";
	public static String SING_IN_CODER_FORM_WIRE_ERROR = "Sign.In.Coder.from.Wire.Exception";

	public static String CLIENT_INFO_CODER_TO_WIRE_ERROR = "Client.Info.Coder.To.Wire.Exception";
	public static String CLIENT_INFO_CODER_FORM_WIRE_ERROR = "Client.Info.Coder.from.Wire.Exception";

	public static String MAC_BYTES_NOT_EFFECTIVE = "Mac.Bytes.Not.Effective";// 不是有效的MAC地址对应的字节序列
	public static String MAC_STRING_NOT_EFFECTIVE = "Mac.String.Not.Effective";// 不是有效的MAC地址对应的字节序列

	public static String SERVER_RETURN_FLAG_CODER_TO_WIRE_ERROR = "Server.Return.Coder.To.Wire.Exception";
	public static String SERVER_RETURN_FLAG_FORM_WIRE_ERROR = "Server.Return.Coder.from.Wire.Exception";

	public static String FILE_CODER_TO_WIRE_ERROR = "File.Coder.To.Wire.Exception";
	public static String FILE_CODER_FORM_WIRE_ERROR = "File.Coder.from.Wire.Exception";

	public static String TASK_CODER_TO_WIRE_ERROR = "Task.Coder.To.Wire.Exception";
	public static String TASK_CODER_FORM_WIRE_ERROR = "Task.Coder.from.Wire.Exception";

	public static String TASK_LIST_CODER_TO_WIRE_ERROR = "Task.List.Coder.To.Wire.Exception";
	public static String TASK_LIST_CODER_FORM_WIRE_ERROR = "Task.List.Coder.from.Wire.Exception";

	public static String TASK_QUERY_CODER_TO_WIRE_ERROR = "Task.Query.Coder.To.Wire.Exception";
	public static String TASK_QUERY_CODER_FORM_WIRE_ERROR = "Task.Query.Coder.from.Wire.Exception";

	public static String USER_TASK_STATUS_CHANGE_CODER_TO_WIRE_ERROR = "User.Task.Status.Change.Coder.To.Wire.Exception";
	public static String USER_TASK_STATUS_CHANGE_CODER_FORM_WIRE_ERROR = "User.Task.Status.Change.Coder.from.Wire.Exception";

	public static String TASK_QUERY_USER_NEW_TASK_CODER_TO_WIRE_ERROR = "Task.Query.User.New.Task.Coder.To.Wire.Exception";
	public static String TASK_QUERY_USER_NEW_TASK_CODER_FORM_WIRE_ERROR = "Task.Query.User.New.Task.Coder.from.Wire.Exception";

	public static String TASK_QUERY_BY_TASK_ID_CODER_TO_WIRE_ERROR = "Task.Query.By.Task.ID.Coder.To.Wire.Exception";
	public static String TASK_QUERY_BY_TASK_ID_CODER_FORM_WIRE_ERROR = "Task.Query.By.Task.ID.Coder.from.Wire.Exception";

	public static String CHANGE_PWD_CODER_TO_WIRE_ERROR = "Change.Pwd.Coder.To.Wire.Exception";
	public static String CHANGE_PWD_CODER_FORM_WIRE_ERROR = "Change.Pwd.Coder.from.Wire.Exception";

	public static String COMMIT_TASK_CODER_TO_WIRE_ERROR = "Commit.Task.Coder.To.Wire.Exception";
	public static String COMMIT_TASK_CODER_FORM_WIRE_ERROR = "Commit.Task.Coder.from.Wire.Exception";

	public static String APK_LASTED_VERSION_CODER_TO_WIRE_ERROR = "Apk.Lasted.Version.Coder.To.Wire.Exception";
	public static String APK_LASTED_VERSION_CODER_FORM_WIRE_ERROR = "Apk.Lasted.Version.from.Wire.Exception";
}
