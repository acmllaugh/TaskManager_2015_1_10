package com.coal.black.bc.socket.client.handlers;

import java.util.ArrayList;
import java.util.List;
import com.coal.black.bc.socket.IDtoBase;
import com.coal.black.bc.socket.client.ClientGlobal;
import com.coal.black.bc.socket.client.SocketClient;
import com.coal.black.bc.socket.client.returndto.BasicResult;
import com.coal.black.bc.socket.client.returndto.TaskQueryByTaskIDResult;
import com.coal.black.bc.socket.dto.TaskQueryByTaskIDDto;
import com.coal.black.bc.socket.enums.OperateType;

public class TaskQueryByTaskIDHandler {
	public TaskQueryByTaskIDResult qryTaskById(int taskID, int taskFlowTimes) {
		SocketClient socketClient = new SocketClient();
		List<IDtoBase> lists = new ArrayList<IDtoBase>();
		TaskQueryByTaskIDDto queryDto = new TaskQueryByTaskIDDto();
		queryDto.setTaskId(taskID);
		queryDto.setTaskFlowTimes(taskFlowTimes);
		lists.add(queryDto);
		BasicResult result = socketClient.deal(OperateType.TaskQryByID, ClientGlobal.getUserId(), lists, this);
		if (result instanceof TaskQueryByTaskIDResult) {
			return (TaskQueryByTaskIDResult) result;
		} else {
			TaskQueryByTaskIDResult result1 = new TaskQueryByTaskIDResult(result);
			return result1;
		}
	}
}
