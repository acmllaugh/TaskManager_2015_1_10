package com.coal.black.bc.socket.client.handlers;

import java.util.ArrayList;
import java.util.List;
import com.coal.black.bc.socket.IDtoBase;
import com.coal.black.bc.socket.client.ClientGlobal;
import com.coal.black.bc.socket.client.SocketClient;
import com.coal.black.bc.socket.client.returndto.BasicResult;
import com.coal.black.bc.socket.client.returndto.CommitTaskResult;
import com.coal.black.bc.socket.dto.CommitTaskDto;
import com.coal.black.bc.socket.enums.OperateType;

public class CommitTaskHandler {
	public CommitTaskResult commitTask(int taskId, boolean needVisitAgain, String visitReport, String realVisitUser, int taskFlowTimes) {
		List<IDtoBase> lists = new ArrayList<IDtoBase>();

		CommitTaskDto dto = new CommitTaskDto();
		dto.setTaskId(taskId);
		dto.setNeedVisitAgain(needVisitAgain);
		dto.setVisitReport(visitReport);
		dto.setTaskFlowTimes(taskFlowTimes);
		dto.setRealVisitUser(realVisitUser);

		lists.add(dto);

		SocketClient socketClient = new SocketClient();
		BasicResult result = socketClient.deal(OperateType.CommitTask, ClientGlobal.getUserId(), lists, this);

		if (result instanceof CommitTaskResult) {
			return (CommitTaskResult) result;
		} else {
			return new CommitTaskResult(result);
		}
	}
}
