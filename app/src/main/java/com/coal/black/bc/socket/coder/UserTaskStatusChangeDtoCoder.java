package com.coal.black.bc.socket.coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.coal.black.bc.socket.Constants;
import com.coal.black.bc.socket.dto.UserTaskStatusChangeDto;
import com.coal.black.bc.socket.exception.BusinessException;

public class UserTaskStatusChangeDtoCoder {
	public static byte[] toWire(UserTaskStatusChangeDto userTaskStatusChangeDto) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(out);
		try {
			dout.writeInt(userTaskStatusChangeDto.getTaskId());
			dout.writeInt(userTaskStatusChangeDto.getUserId());
			dout.writeInt(userTaskStatusChangeDto.getUserTaskStatus());
			dout.writeInt(userTaskStatusChangeDto.getTaskFlowTimes());
		} catch (IOException ex) {
			throw new BusinessException(Constants.USER_TASK_STATUS_CHANGE_CODER_TO_WIRE_ERROR, ex);
		}
		return out.toByteArray();
	}

	public static UserTaskStatusChangeDto fromWire(byte[] bytes) {
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		DataInputStream din = new DataInputStream(in);
		UserTaskStatusChangeDto uts = new UserTaskStatusChangeDto();
		try {
			int taskId = din.readInt();
			int userId = din.readInt();
			int userTaskStatus = din.readInt();
			int taskFlowTimes = din.readInt();
			uts.setTaskId(taskId);
			uts.setUserId(userId);
			uts.setUserTaskStatus(userTaskStatus);
			uts.setTaskFlowTimes(taskFlowTimes);
		} catch (IOException ex) {
			throw new BusinessException(Constants.USER_TASK_STATUS_CHANGE_CODER_FORM_WIRE_ERROR, ex);
		}
		return uts;
	}
}
