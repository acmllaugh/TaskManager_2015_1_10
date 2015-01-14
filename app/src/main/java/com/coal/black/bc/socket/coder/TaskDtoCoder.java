package com.coal.black.bc.socket.coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

import com.coal.black.bc.socket.Constants;
import com.coal.black.bc.socket.dto.TaskDto;
import com.coal.black.bc.socket.exception.BusinessException;
import com.coal.black.bc.socket.utils.StringUtil;

public class TaskDtoCoder {
	public static byte[] toWire(TaskDto task) {
		if (task == null) {
			return new byte[0];
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(out);
		try {
			writeIntegerData(task.getId(), dout);// 写入用户id

			writeStringData(task.getBank(), dout);// 写入银行
			writeStringData(task.getCaseID(), dout);// 写入个案序列号
			writeStringData(task.getName(), dout);// 写名称
			writeStringData(task.getIdentityCard(), dout);// 写入身份证号
			writeStringData(task.getBankCard(), dout);// 写入银行卡号

			writeDoubleData(task.getCaseAmount(), dout);// 写入委案金额
			writeDoubleData(task.getHasPayed(), dout);// 写入已还款

			writeStringData(task.getNoticeStatement(), dout);// 催款状态
			writeStringData(task.getAddress(), dout);// 写入地址

			writeIntegerData(task.getVisitTimes(), dout);// 写入访问次数

			writeStringData(task.getVisitReason(), dout);// 写入外访原因
			writeStringData(task.getRequirement(), dout);// 写入要求
			writeStringData(task.getMemo(), dout);// 写入备注
			writeStringData(task.getContactInfo(), dout);// 写入所有联系方式
			writeStringData(task.getCompanyName(), dout);// 写入公司名称

			writeStringData(task.getEstimateVisitDate(), dout);// 写入预计访问日期

			writeIntegerData(task.getTaskStatus(), dout);// 写入实际访问人
			writeIntegerData(task.getUserTaskStatus(), dout);// 写入实际访问人

			writeLongData(task.getGrantTime(), dout);// 授权时间
			writeLongData(task.getOperateTime(), dout);// 操作时间

			writeStringData(task.getProvince(), dout);// 写入省份
			writeStringData(task.getCity(), dout);// 写入城市
			writeStringData(task.getRegion(), dout);// 写入地区
			writeBooleanData(task.isUrgent(), dout);// 是否是紧急的

			writeLongData(task.getBusinessID(), dout);// 写入业务ID

			writeIntegerData(task.getTaskFlowTimes(), dout);// 写入TaskFlowTimes
		} catch (IOException ex) {
			throw new BusinessException(Constants.TASK_CODER_TO_WIRE_ERROR, ex);
		}
		return out.toByteArray();
	}

	public static TaskDto fromWire(byte[] bytes) {
		if (bytes == null || bytes.length <= 0) {
			return null;
		}
		TaskDto taskDto = new TaskDto();
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		DataInputStream din = new DataInputStream(in);

		try {
			int taskId = din.readInt();
			taskDto.setId(taskId);

			taskDto.setBank(getStringData(din));// 读取银行信息
			taskDto.setCaseID(getStringData(din));// 读取个案序列号
			taskDto.setName(getStringData(din));// 读取名称
			taskDto.setIdentityCard(getStringData(din));// 读取身份证号码
			taskDto.setBankCard(getStringData(din));// 读取银行卡号

			double caseAmount = din.readDouble();
			taskDto.setCaseAmount(caseAmount);// 读取委案金额
			double hasPayed = din.readDouble();
			taskDto.setHasPayed(hasPayed);// 写入已还款

			taskDto.setNoticeStatement(getStringData(din));// 读取催款状态
			taskDto.setAddress(getStringData(din));// 读取地址

			int visitedTimes = din.readInt();
			taskDto.setVisitTimes(visitedTimes);

			taskDto.setVisitReason(getStringData(din));// 读取外访原因
			taskDto.setRequirement(getStringData(din));// 读取要求
			taskDto.setMemo(getStringData(din));// 读取备注
			taskDto.setContactInfo(getStringData(din));// 读取所有联系方式
			taskDto.setCompanyName(getStringData(din));// 读取公司名称

			taskDto.setEstimateVisitDate(getStringData(din));// 读取预计访问日期

			taskDto.setTaskStatus(din.readInt());// 设置任务的状态
			taskDto.setUserTaskStatus(din.readInt());// 设置用户任务状态
			taskDto.setGrantTime(din.readLong());
			taskDto.setOperateTime(din.readLong());

			taskDto.setProvince(getStringData(din));
			taskDto.setCity(getStringData(din));
			taskDto.setRegion(getStringData(din));
			taskDto.setUrgent(din.readBoolean());// 设置是否加急

			taskDto.setBusinessID(din.readLong());// 设置业务ID

			taskDto.setTaskFlowTimes(din.readInt());// 读取是否加急
		} catch (IOException ex) {
			throw new BusinessException(Constants.TASK_CODER_FORM_WIRE_ERROR, ex);
		}
		return taskDto;
	}

	private static void writeStringData(String value, DataOutputStream dout) throws IOException {
		int length = 0;
		byte[] bytes = null;
		if (StringUtil.isNotEmpty(value)) {
			bytes = value.trim().getBytes("UTF-8");
			length = bytes.length;
		}
		dout.writeInt(length);
		if (length > 0) {
			dout.write(bytes);
		}
	}

	@SuppressWarnings("unused")
	private static void writeDate(Date date, DataOutputStream dout) throws IOException {
		long time = 0;
		if (date != null) {
			time = date.getTime();
		}
		dout.writeLong(time);
	}

	private static void writeIntegerData(Integer value, DataOutputStream dout) throws IOException {
		if (value == null) {
			value = 0;
		}
		dout.writeInt(value);
	}

	private static void writeDoubleData(Double value, DataOutputStream dout) throws IOException {
		if (value == null) {
			value = 0D;
		}
		dout.writeDouble(value);
	}

	private static void writeLongData(Long value, DataOutputStream dout) throws IOException {
		if (value == null) {
			value = 0L;
		}
		dout.writeLong(value);
	}

	private static void writeBooleanData(Boolean value, DataOutputStream dout) throws IOException {
		if (value == null) {
			value = false;
		}
		dout.writeBoolean(value);
	}

	private static String getStringData(DataInputStream din) throws IOException {
		int length = din.readInt();
		if (length == 0) {
			return null;
		}
		byte[] bytes = new byte[length];
		if (length == din.read(bytes, 0, length)) {
			String s = new String(bytes, "UTF-8");
			return s;
		} else {
			throw new RuntimeException("The real bytes read length is not same with length " + length);
		}
	}

	@SuppressWarnings("unused")
	private static Date readDate(DataInputStream din) throws IOException {
		long time = din.readLong();
		if (time <= 0) {
			return null;
		} else {
			return new Date(time);
		}
	}
}
