package com.coal.black.bc.socket.client.test;

import com.coal.black.bc.socket.client.ClientGlobal;
import com.coal.black.bc.socket.client.handlers.ApkQueryHandler;
import com.coal.black.bc.socket.client.returndto.ApkQueryResult;

public class TestApkQuery {
	public static void main(String[] args) {
		// ClientGlobal.setUserId(1);
		// ClientGlobal.setMacAddress("7C:E9:D3:EF:FA:10");
		ClientGlobal.setUserId(2);
		ClientGlobal.setMacAddress("EC:CB:30:D6:1E:0C");

		ApkQueryHandler handler = new ApkQueryHandler();
		ApkQueryResult result = handler.queryLastedVersion();
		if (result.isSuccess()) {
			System.out.println(result.getVersionDto());
		} else {
			if (result.isBusException()) {
				System.out.println("Business Exception, exception code is " + result.getBusinessErrorCode());
			} else {
				System.out.println("Other Exception, exception type is " + result.getThrowable());
			}
		}
	}
}
