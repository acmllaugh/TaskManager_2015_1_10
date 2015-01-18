package com.coal.black.bc.socket.client.test;

import java.io.File;

import com.coal.black.bc.socket.client.ClientGlobal;
import com.coal.black.bc.socket.client.handlers.UploadFileHandler;
import com.coal.black.bc.socket.client.returndto.UploadFileResult;

public class TestUploadFile {
	public static void main(String[] args) {
		ClientGlobal.setUserId(16);
		ClientGlobal.setMacAddress("EC:CB:30:D6:1E:0C");
		// ClientGlobal.setUserId(11);
		File f = new File("D:\\Lighthouse1.jpg");
		UploadFileHandler uh = new UploadFileHandler();
		UploadFileResult result = uh.upload(f, 17, 1, true);
		if (result.isSuccess()) {
			System.out.println("Success, result is " + result.isUploadSuccess());
		} else {
			if (result.isBusException()) {
				System.out.println("Business Exception, exception code is " + result.getBusinessErrorCode());
			} else {
				System.out.println("Other Exception, exception type is " + result.getThrowable());
			}
		}
	}
}
