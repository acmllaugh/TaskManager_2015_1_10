package com.coal.black.bc.socket.client.test;

import com.coal.black.bc.socket.client.ClientGlobal;
import com.coal.black.bc.socket.client.handlers.TaskQryUserNewTaskHandler;
import com.coal.black.bc.socket.client.returndto.TaskQryUserNewTaskListResult;

public class TestQryNewTaskListHandler {
	public static void main(String[] args) {
		ClientGlobal.setUserId(16);
		ClientGlobal.setMacAddress("EC:CB:30:D6:1E:0C");
		TaskQryUserNewTaskHandler handler = new TaskQryUserNewTaskHandler();

		TaskQryUserNewTaskListResult contListResult = handler.qryNewTaskList(1419657487665L);
		if (contListResult.isSuccess()) {
			System.out.println("Success, result count is " + contListResult.getTaskList());
		} else {
			if (contListResult.isBusException()) {
				System.out.println("Business Exception, exception code is " + contListResult.getBusinessErrorCode());
			} else {
				System.out.println("Other Exception, exception type is " + contListResult.getThrowable());
			}
		}
	}
}
