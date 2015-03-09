package com.coal.black.bc.socket.client.test;

import com.coal.black.bc.socket.client.ClientGlobal;
import com.coal.black.bc.socket.client.handlers.UserTaskStatusChangeHandler;
import com.coal.black.bc.socket.client.returndto.UserTaskStatusChangeResult;
import com.coal.black.bc.socket.common.UserTaskStatusCommon;

public class TestUserStatusChangeHanlder {
	public static void main(String[] args) {
		ClientGlobal.setUserId(2);
		ClientGlobal.setMacAddress("EC:CB:30:D6:1E:0C");
		// ClientGlobal.setMacAddress("7C:E9:D3:EF:FA:10");
		// ClientGlobal.setUserId(1);
		UserTaskStatusChangeHandler handler = new UserTaskStatusChangeHandler();

		UserTaskStatusChangeResult result = handler.changeUserTaskStatus(15, UserTaskStatusCommon.IN_DEALING, 1);
		if (result.isSuccess()) {
			System.out.println("Success");
		} else {
			if (result.isBusException()) {
				System.out.println("Business Exception, exception code is " + result.getBusinessErrorCode());
			} else {
				System.out.println("Other Exception, exception type is " + result.getThrowable());
			}
		}
	}
}
