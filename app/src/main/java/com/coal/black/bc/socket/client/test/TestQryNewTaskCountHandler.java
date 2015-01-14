package com.coal.black.bc.socket.client.test;

import com.coal.black.bc.socket.client.ClientGlobal;
import com.coal.black.bc.socket.client.handlers.TaskQryUserNewTaskHandler;
import com.coal.black.bc.socket.client.returndto.TaskQryUserNewTaskCountResult;

public class TestQryNewTaskCountHandler {
	public static void main(String[] args) {
		ClientGlobal.setUserId(16);
		ClientGlobal.setMacAddress("EC:CB:30:D6:1E:0C");
		TaskQryUserNewTaskHandler handler = new TaskQryUserNewTaskHandler();
		TaskQryUserNewTaskCountResult countResult = handler.qryNewTaskCount(1419657487665L);
		if (countResult.isSuccess()) {
			System.out.println("Success, result count is " + countResult.getCount());
		} else {
			if (countResult.isBusException()) {
				System.out.println("Business Exception, exception code is " + countResult.getBusinessErrorCode());
			} else {
				System.out.println("Other Exception, exception type is " + countResult.getThrowable());
			}
		}
	}
}
