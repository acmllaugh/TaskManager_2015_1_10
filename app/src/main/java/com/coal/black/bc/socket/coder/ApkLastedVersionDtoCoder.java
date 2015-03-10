package com.coal.black.bc.socket.coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.coal.black.bc.socket.Constants;
import com.coal.black.bc.socket.dto.ApkLastedVersionDto;
import com.coal.black.bc.socket.exception.BusinessException;
import com.coal.black.bc.socket.utils.InputStreamUtils;
import com.coal.black.bc.socket.utils.StringUtil;

public class ApkLastedVersionDtoCoder {
	public static byte[] toWire(ApkLastedVersionDto apkLastedVersionDto) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(out);
		try {
			dout.writeInt(apkLastedVersionDto.getVersionNum());
			byte[] strVersions = apkLastedVersionDto.getVersionStr().getBytes("UTF-8");
			dout.writeInt(strVersions.length);
			dout.write(strVersions);

			String desc = apkLastedVersionDto.getDesc();
			if (StringUtil.isEmpty(desc)) {
				dout.writeInt(0);
			} else {
				byte[] descBytes = desc.getBytes("UTF-8");
				dout.writeInt(descBytes.length);
				dout.write(descBytes);
			}
		} catch (IOException ex) {
			throw new BusinessException(Constants.APK_LASTED_VERSION_CODER_TO_WIRE_ERROR, ex);
		}
		return out.toByteArray();
	}

	public static ApkLastedVersionDto fromWire(byte[] bytes) {
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		DataInputStream din = new DataInputStream(in);
		try {
			ApkLastedVersionDto dto = new ApkLastedVersionDto();
			dto.setVersionNum(din.readInt());

			int versionStrLength = din.readInt();// 获取versionStr的length
			byte[] versionStrBytes = InputStreamUtils.readFixedLengthData(versionStrLength, din);// 读取实际的外访人
			String versionStr = new String(versionStrBytes, "UTF-8");
			dto.setVersionStr(versionStr);

			int descStrLength = din.readInt();
			if (descStrLength > 0) {
				byte[] descStrBytes = InputStreamUtils.readFixedLengthData(descStrLength, din);
				String descStr = new String(descStrBytes, "UTF-8");
				dto.setDesc(descStr);
			} else {
				dto.setDesc("");
			}
			return dto;
		} catch (IOException ex) {
			throw new BusinessException(Constants.APK_LASTED_VERSION_CODER_FORM_WIRE_ERROR, ex);
		}
	}
}
