package com.coal.black.bc.socket.client.handlers;

import com.coal.black.bc.socket.client.ClientGlobal;
import com.coal.black.bc.socket.client.SocketClient;
import com.coal.black.bc.socket.client.returndto.ApkQueryResult;
import com.coal.black.bc.socket.client.returndto.BasicResult;
import com.coal.black.bc.socket.enums.OperateType;

public class ApkQueryHandler {
	public ApkQueryResult queryLastedVersion() {
		SocketClient client = new SocketClient();
		BasicResult basicResult = client.deal(OperateType.ApkLastedVersionQuery, ClientGlobal.getUserId(), null, this);
		if (basicResult instanceof ApkQueryResult) {
			return (ApkQueryResult) basicResult;
		} else {
			ApkQueryResult signInResult = new ApkQueryResult(basicResult);
			return signInResult;
		}
	}
}
