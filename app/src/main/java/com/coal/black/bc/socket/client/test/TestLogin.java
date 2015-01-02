package com.coal.black.bc.socket.client.test;

import com.coal.black.bc.socket.client.ClientGlobal;
import com.coal.black.bc.socket.client.handlers.UserLoginHandler;
import com.coal.black.bc.socket.client.returndto.LoginResult;

public class TestLogin {
	public static void main(String[] args) {
		ClientGlobal.setMacAddress("34:23:BA:8E:4C:10");
		for (int i = 0; i < 10; i++) {
			UserLoginHandler userLogin = new UserLoginHandler();
			LoginResult loginResult = userLogin.login("xy", "123456");
			if (loginResult.isSuccess()) {
				System.out.println("Success, UserID is " + loginResult.getUserId());
			} else {
				if (loginResult.isBusException()) {
					System.out.println("Business Exception, exception code is " + loginResult.getBusinessErrorCode());
				} else {
					System.out.println("Other Exception, exception type is " + loginResult.getThrowable());
                    loginResult.getThrowable().printStackTrace();
				}
			}
		}
	}
}
