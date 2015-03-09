package com.coal.black.bc.socket.client.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.coal.black.bc.socket.client.ClientGlobal;
import com.coal.black.bc.socket.client.handlers.UserLoginHandler;
import com.coal.black.bc.socket.client.handlers.UserSignHandler;
import com.coal.black.bc.socket.client.returndto.LoginResult;
import com.coal.black.bc.socket.client.returndto.SignInResult;
import com.coal.black.bc.socket.dto.SignInDto;
import com.coal.black.bc.socket.enums.SignInType;

public class TestSign {
	private static final Random random = new Random();

	public static void main(String[] args) {
		ClientGlobal.setMacAddress("F8:A4:5F:1B:AA:7C");
		UserLoginHandler userLogin = new UserLoginHandler();
		LoginResult loginResult = userLogin.login("zb", "123456");
		if (loginResult.isSuccess()) {
			ClientGlobal.setUserId(loginResult.getUserId());

			for (int i = 0; i < 10; i++) {
				SignInDto signIn = new SignInDto();
				double jd = Math.abs(random.nextDouble() * 90);
				signIn.setLatitude(jd);
				double wd = Math.abs(random.nextDouble() * 180);
				signIn.setLongitude(wd);
				long time = System.currentTimeMillis();
				signIn.setTime(time);
				signIn.setType(SignInType.SignIn);

				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				SignInDto signIn1 = new SignInDto();
				double jd1 = Math.abs(random.nextDouble() * 90);
				signIn1.setLatitude(jd1);
				double wd1 = Math.abs(random.nextDouble() * 180);
				signIn1.setLongitude(wd1);
				long time1 = System.currentTimeMillis();
				signIn1.setTime(time1);
				signIn1.setType(SignInType.ReportPosition);

				List<SignInDto> list = new ArrayList<SignInDto>();
				list.add(signIn);
				list.add(signIn1);

				UserSignHandler userSign = new UserSignHandler();
				SignInResult result = userSign.signIn(list);
				if (result.isSuccess()) {
					System.out.println("Success, result is " + result.getResultList());
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
}
