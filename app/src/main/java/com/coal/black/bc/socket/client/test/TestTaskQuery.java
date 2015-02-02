package com.coal.black.bc.socket.client.test;

import com.coal.black.bc.socket.client.ClientGlobal;
import com.coal.black.bc.socket.client.handlers.TaskQueryHandler;
import com.coal.black.bc.socket.client.returndto.TaskQueryResult;

public class TestTaskQuery {
	public static void main(String[] args) {
		// ClientGlobal.setUserId(16);
		// ClientGlobal.setMacAddress("EC:CB:30:D6:1E:0C");
		ClientGlobal.setUserId(1);
		int[] status = new int[] { 1, 2, 3 };// 状态
		TaskQueryHandler taskQueryHandler = new TaskQueryHandler();
		TaskQueryResult result = taskQueryHandler.qryTasks(status);// 查询任务
		if (result.isSuccess()) {
			System.out.println("Success, result length is " + result.getTaskList().size() + " result is " + result.getTaskList());
		} else {
			if (result.isBusException()) {
				System.out.println("Business Exception, exception code is " + result.getBusinessErrorCode());
			} else {
				System.out.println("Other Exception, exception type is " + result.getThrowable());
			}
		}
	}
}
