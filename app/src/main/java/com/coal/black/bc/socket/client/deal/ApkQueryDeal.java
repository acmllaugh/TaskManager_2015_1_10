package com.coal.black.bc.socket.client.deal;

import java.io.InputStream;
import java.io.OutputStream;
import com.coal.black.bc.socket.client.returndto.ApkQueryResult;
import com.coal.black.bc.socket.coder.ApkLastedVersionDtoCoder;
import com.coal.black.bc.socket.coder.ClientInfoDtoCoder;
import com.coal.black.bc.socket.coder.ServerReturnFlagCoder;
import com.coal.black.bc.socket.dto.ApkLastedVersionDto;
import com.coal.black.bc.socket.dto.ClientInfoDto;
import com.coal.black.bc.socket.dto.ServerReturnFlagDto;
import com.coal.black.bc.socket.utils.InputStreamUtils;

public class ApkQueryDeal {
	public ApkQueryResult deal(ClientInfoDto clientDto, InputStream in, OutputStream out) {
		try {
			clientDto.setDataLength(0);
			byte[] clientBytes = ClientInfoDtoCoder.toWire(clientDto);
			out.write(clientBytes);
			out.flush();

			byte[] serverFlageBytes = InputStreamUtils.readFixedLengthData(ServerReturnFlagDto.bytesLength, in);// 获取返回结果
			ServerReturnFlagDto returnFlag = ServerReturnFlagCoder.fromWire(serverFlageBytes);
			ApkQueryResult changeResult = new ApkQueryResult();
			if (returnFlag.isSuccess()) {
				int dataLength = returnFlag.getDataLength();// 获取数据的长度
				byte data[] = InputStreamUtils.readFixedLengthData(dataLength, in);// 直接读取数据

				ApkLastedVersionDto version = ApkLastedVersionDtoCoder.fromWire(data);
				changeResult.setVersionDto(version);

				changeResult.setSuccess(true);
				changeResult.setBusException(false);
				changeResult.setBusinessErrorCode((byte) -1);
				changeResult.setThrowable(null);
			} else {
				changeResult.setSuccess(false);
				changeResult.setBusException(returnFlag.isBusinessException());
				changeResult.setBusinessErrorCode((byte) (changeResult.isBusException() ? returnFlag.getExceptionCode() : -1));
				changeResult.setThrowable(null);
				changeResult.setVersionDto(null);
			}
			return changeResult;
		} catch (Exception ex) {
			ApkQueryResult result = new ApkQueryResult();
			result.setSuccess(false);
			result.setBusException(false);
			result.setBusinessErrorCode((byte) -1);
			result.setThrowable(ex);
			result.setVersionDto(null);
			return result;
		}
	}
}
