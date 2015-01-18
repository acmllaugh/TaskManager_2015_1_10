package com.coal.black.bc.socket.client.test;

import com.coal.black.bc.socket.client.ClientGlobal;
import com.coal.black.bc.socket.client.handlers.CommitTaskHandler;
import com.coal.black.bc.socket.client.returndto.CommitTaskResult;

public class TestCommitTask {
	public static void main(String[] args) {
		// ClientGlobal.setUserId(16);
		// ClientGlobal.setMacAddress("EC:CB:30:D6:1E:0C");
		ClientGlobal.setUserId(11);

		CommitTaskHandler handler = new CommitTaskHandler();
		CommitTaskResult result = handler.commitTask(4, false, "王慧最新的外访报告", "王慧", 1);
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
