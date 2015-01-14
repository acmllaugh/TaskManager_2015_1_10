package com.coal.black.bc.socket.coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import com.coal.black.bc.socket.Constants;
import com.coal.black.bc.socket.dto.TaskQueryByTaskIDDto;
import com.coal.black.bc.socket.exception.BusinessException;

public class TaskQueryByTaskIDDtoCoder {
	public static byte[] toWire(TaskQueryByTaskIDDto taskQryByIdDto) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(out);
		try {
			dout.writeInt(taskQryByIdDto.getTaskId());
			dout.writeInt(taskQryByIdDto.getTaskFlowTimes());
		} catch (IOException ex) {
			throw new BusinessException(Constants.TASK_QUERY_BY_TASK_ID_CODER_TO_WIRE_ERROR, ex);
		}
		return out.toByteArray();
	}

	public static TaskQueryByTaskIDDto fromWire(byte[] bytes) {
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		DataInputStream din = new DataInputStream(in);
		TaskQueryByTaskIDDto uts = new TaskQueryByTaskIDDto();
		try {
			int taskId = din.readInt();
			int taskFlowTimes = din.readInt();
			uts.setTaskId(taskId);
			uts.setTaskFlowTimes(taskFlowTimes);
		} catch (IOException ex) {
			throw new BusinessException(Constants.TASK_QUERY_BY_TASK_ID_CODER_FORM_WIRE_ERROR, ex);
		}
		return uts;
	}
}
