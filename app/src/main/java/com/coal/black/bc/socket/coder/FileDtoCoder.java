package com.coal.black.bc.socket.coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.coal.black.bc.socket.Constants;
import com.coal.black.bc.socket.dto.UploadFileDto;
import com.coal.black.bc.socket.exception.BusinessException;

public class FileDtoCoder {
	public static byte[] toWire(UploadFileDto fileDto) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(out);
		try {
			dout.writeInt(fileDto.getTaskId());// 设置task的id
			dout.writeInt(fileDto.getTaskFlowTimes());
			dout.write(fileDto.getFileName().getBytes());
			dout.writeInt(fileDto.getFileLength());
			dout.write(fileDto.isPicture() ? 1 : 0);
			return out.toByteArray();
		} catch (IOException ex) {
			throw new BusinessException(Constants.FILE_CODER_TO_WIRE_ERROR, ex);
		}
	}

	public static UploadFileDto fromWire(byte[] bytes) {
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		DataInputStream din = new DataInputStream(in);
		try {
			int taskId = din.readInt();// 首先读出taskId
			int taskFlowTimes = din.readInt();// 其次读取流程次数
			int fileNameLength = bytes.length - 13;
			byte[] fileNameBytes = new byte[fileNameLength];
			din.read(fileNameBytes, 0, fileNameLength);
			String fileName = new String(fileNameBytes);
			int fileLength = din.readInt();
			boolean ispicture = din.read() == 1;
			UploadFileDto fdto = new UploadFileDto();
			fdto.setTaskId(taskId);// 设置taskId信息
			fdto.setFileLength(fileLength);
			fdto.setFileName(fileName);
			fdto.setTaskFlowTimes(taskFlowTimes);// 设置taskFlowTimes
			fdto.setPicture(ispicture);// 设置是否是图片信息
			return fdto;
		} catch (IOException ex) {
			throw new BusinessException(Constants.FILE_CODER_FORM_WIRE_ERROR, ex);
		}
	}
}
