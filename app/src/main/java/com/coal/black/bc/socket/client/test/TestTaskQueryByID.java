package com.coal.black.bc.socket.client.test;

import com.coal.black.bc.socket.client.ClientGlobal;
import com.coal.black.bc.socket.client.handlers.TaskQueryByTaskIDHandler;
import com.coal.black.bc.socket.client.returndto.TaskQueryByTaskIDResult;

public class TestTaskQueryByID {
	public static void main(String[] args) {
		ClientGlobal.setUserId(16);
		ClientGlobal.setMacAddress("EC:CB:30:D6:1E:0C");
		TaskQueryByTaskIDHandler handler = new TaskQueryByTaskIDHandler();
		TaskQueryByTaskIDResult result = handler.qryTaskById(4, 1);
		if (result.isSuccess()) {
			System.out.println("Success, result length is " + result.getTaskDto());
		} else {
			if (result.isBusException()) {
				System.out.println("Business Exception, exception code is " + result.getBusinessErrorCode());
			} else {
				System.out.println("Other Exception, exception type is " + result.getThrowable());
			}
		}
	}
}
