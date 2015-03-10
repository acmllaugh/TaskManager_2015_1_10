package com.coal.black.bc.socket.client.test;

import java.io.File;

import com.coal.black.bc.socket.client.ClientGlobal;
import com.coal.black.bc.socket.client.handlers.UploadFileHandler;
import com.coal.black.bc.socket.client.returndto.UploadFileResult;

public class TestUploadFile {
	public static void main(String[] args) {
		ClientGlobal.setUserId(2);
		ClientGlobal.setMacAddress("EC:CB:30:D6:1E:0C");
		// ClientGlobal.setMacAddress("7C:E9:D3:EF:FA:10");
		// ClientGlobal.setUserId(1);
		String[] path = new String[] { "G:\\music\\但愿人长久.mp3", "G:\\music\\给自己的情书.mp3", "G:\\music\\何必在一起.mp3" };
		for (int i = 0; i < 3; i++) {
			File f = new File(path[i]);
			UploadFileHandler uh = new UploadFileHandler();
			UploadFileResult result = uh.upload(f, 15, 1, true);
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
}
