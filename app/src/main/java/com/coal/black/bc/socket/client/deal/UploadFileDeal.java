package com.coal.black.bc.socket.client.deal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.List;

import com.coal.black.bc.socket.IDtoBase;
import com.coal.black.bc.socket.client.handlers.UploadFileHandler;
import com.coal.black.bc.socket.client.returndto.UploadFileResult;
import com.coal.black.bc.socket.coder.ClientInfoDtoCoder;
import com.coal.black.bc.socket.coder.FileDtoCoder;
import com.coal.black.bc.socket.coder.ServerReturnFlagCoder;
import com.coal.black.bc.socket.dto.ClientInfoDto;
import com.coal.black.bc.socket.dto.UploadFileDto;
import com.coal.black.bc.socket.dto.ServerReturnFlagDto;
import com.coal.black.bc.socket.exception.ExceptionBase;
import com.coal.black.bc.socket.utils.DataUtil;
import com.coal.black.bc.socket.utils.InputStreamUtils;

public class UploadFileDeal {
	public UploadFileResult deal(ClientInfoDto clientDto, List<IDtoBase> dtoList, InputStream in, OutputStream out, UploadFileHandler uploadHandler)
			throws Throwable {
		UploadFileDto fileDto = (UploadFileDto) dtoList.get(0);
		byte[] fileBytes = FileDtoCoder.toWire(fileDto);
		clientDto.setDataLength(fileBytes.length);

		byte[] clientBytes = ClientInfoDtoCoder.toWire(clientDto);

		out.write(clientBytes);// 向客户端写入用户的信息
		out.write(fileBytes);// 向客户端写入UploadFileDto信息

		ServerReturnFlagDto srf = readSrf(in);// 读取服务器端的回应
		if (srf == null) {// 读取回应除了问题，直接返回
			return generateSrfLengthErrorResult();
		}

		int existLength = 0;
		if (srf.isSuccess()) {
			byte isExists = (byte) in.read();// 表示是否存在
			if (isExists == 1) {// 表示已经存在
				byte hasFinished = (byte) in.read();
				if (hasFinished == 1) {// 表示传输已经完成了，不需要再传输了
					uploadHandler.uploadedFinished = true;
					return generateHasUploadedFinishedResult(true);// 表示客户端不仅仅存在，并且已经传输完成，那么直接返回传输完成的结果
				} else {// 表示客户端存在，但是还没有上传完成的情况
					existLength = getHasReceviedBytesLength(in);
					if (existLength <= -1) {
						return generateGetReceivedLengthErrorUploadResult();
					}
				}
			} else {
				existLength = 0;
			}
			uploadHandler.uploadedFinished = false;
			uploadHandler.serverReceivedLength = existLength;// 设置服务器端已经接收的长度大小
		} else {// 表示服务器端返回的结果是exception，则直接返回异常信息
			return generateServerReturnNotSuccessUploadResult(srf);
		}

		RandomAccessFile randomAccessFile = null;
		try {
			randomAccessFile = new RandomAccessFile(fileDto.getClientFile(), "r");
			byte[] fileDataLengthBytes = DataUtil.int2Bytes(fileDto.getFileLength() - existLength);

			out.write(fileDataLengthBytes);// 写一下总共还需要上传的文件大小
			randomAccessFile.skipBytes(existLength);// 跳过前面的existLength
			byte[] bytes = new byte[20480];
			int length = 0;
			while (true) {// 2K一次发送
				if ((length = randomAccessFile.read(bytes)) != -1) {
					out.write(bytes, 0, length);
					out.flush();
				}

				srf = readSrf(in);// 读取服务器端的回应
				if (srf == null) {// 读取回应除了问题，直接返回
					return generateSrfLengthErrorResult();
				}
				if (srf.isSuccess()) {// 返回结果
					byte hasSuccess = (byte) in.read();// 获取是否已经上传成功
					byte uploadFinished = (byte) in.read();// 是否上传完成
					int receivedLength = getHasReceviedBytesLength(in);// 获取真正接收到的长度
					if (receivedLength <= -1) {// 如果接收到的长度<=-1
						return generateGetReceivedLengthErrorUploadResult();
					}
					uploadHandler.uploadedFinished = false;
					uploadHandler.serverReceivedLength = receivedLength;// 设置服务器端已经接收的长度大小
					if (uploadFinished == 1) {// 如果接收已经完成
						return generateHasUploadedFinishedResult(hasSuccess == 1);// 直接返回接收完成的信息
					}
				} else {// 表示服务器端返回的结果是exception，则直接返回异常信息
					return generateServerReturnNotSuccessUploadResult(srf);
				}

			}
		} finally {
			if (randomAccessFile != null) {
				try {
					randomAccessFile.close();
				} catch (Exception ex) {
				}
			}
		}
	}

	/**
	 * 获取已经接收的bytes的长度信息
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static int getHasReceviedBytesLength(InputStream in) throws IOException {
		byte[] lengthBytes = InputStreamUtils.readFixedLengthData(4, in);
		int existLength = DataUtil.bytes2Int(lengthBytes);
		return existLength;
	}

	/**
	 * 返回产生服务器返回结果不成功而导致的上传的异常信息
	 * 
	 * @return
	 */
	private static UploadFileResult generateServerReturnNotSuccessUploadResult(ServerReturnFlagDto srf) {
		UploadFileResult result = new UploadFileResult();
		result.setUploadSuccess(false);
		result.setSuccess(false);
		result.setBusException(srf.isBusinessException());
		result.setBusinessErrorCode(srf.getExceptionCode());
		result.setThrowable(null);
		return result;
	}

	/**
	 * 产生获取已经接收的长度结果出错的异常结果
	 * 
	 * @return
	 */
	private static UploadFileResult generateGetReceivedLengthErrorUploadResult() {
		UploadFileResult result = new UploadFileResult();
		result.setUploadSuccess(false);
		result.setSuccess(false);
		result.setBusException(false);
		result.setBusinessErrorCode(ExceptionBase.NONE_EXCEPTION);
		result.setThrowable(new RuntimeException("Error on recieve has uploaded length, the received bytes length < 4"));
		return result;
	}

	/**
	 * 获取服务器端的返回结果
	 */
	private static ServerReturnFlagDto readSrf(InputStream in) throws IOException {
		byte[] serverFlageBytes = InputStreamUtils.readFixedLengthData(ServerReturnFlagDto.bytesLength, in);
		ServerReturnFlagDto srf = ServerReturnFlagCoder.fromWire(serverFlageBytes);// 否则转换为ServerReturnFlagDto
		return srf;
	}

	/**
	 * 产生读取的ServiceReturnFlag长度出错的UploadFileResult结果返回
	 * 
	 * @return
	 */
	private static UploadFileResult generateSrfLengthErrorResult() {
		UploadFileResult result = new UploadFileResult();
		result.setUploadSuccess(false);
		result.setSuccess(false);
		result.setBusException(false);
		result.setThrowable(new RuntimeException("no return result been found exception."));
		result.setThrowable(null);
		return result;
	}

	/**
	 * 产生上传失败的结果
	 * 
	 * @return
	 */
	// private static UploadFileResult generateUploadFileErrorResult() {
	// UploadFileResult result = new UploadFileResult();
	// result.setUploadSuccess(false);
	// result.setSuccess(false);
	// result.setBusException(false);
	// result.setBusinessErrorCode((byte) -1);
	// result.setThrowable(new RuntimeException("Upload Fail."));
	// return result;
	// }

	/**
	 * 产生上传完成的结果的UploadFileResult返回
	 * 
	 * @return
	 */
	private static UploadFileResult generateHasUploadedFinishedResult(boolean hasSuccess) {
		UploadFileResult result = new UploadFileResult();
		result.setUploadSuccess(true);
		result.setSuccess(hasSuccess);
		result.setBusException(false);
		result.setBusinessErrorCode((byte) -1);
		result.setThrowable(null);
		return result;
	}
}
