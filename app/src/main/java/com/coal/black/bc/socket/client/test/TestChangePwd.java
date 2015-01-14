package com.coal.black.bc.socket.client.test;

import com.coal.black.bc.socket.client.ClientGlobal;
import com.coal.black.bc.socket.client.handlers.ChangePwdHandler;
import com.coal.black.bc.socket.client.returndto.ChangePwdResult;

public class TestChangePwd {
	public static void main(String[] args) {
		ClientGlobal.setUserId(11);
		ClientGlobal.setMacAddress("7C:E9:D3:EF:FA:10");
		String oldPwd = "123456";
		String newPwd = "654321";
		ChangePwdHandler handler = new ChangePwdHandler();
		ChangePwdResult result = handler.changePwd(oldPwd, newPwd);
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
