package com.coal.black.bc.socket.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import com.coal.black.bc.socket.Constants;
import com.coal.black.bc.socket.IDtoBase;
import com.coal.black.bc.socket.client.deal.ChangePwdDeal;
import com.coal.black.bc.socket.client.deal.CommitTaskDeal;
import com.coal.black.bc.socket.client.deal.LoginDeal;
import com.coal.black.bc.socket.client.deal.SignInDeal;
import com.coal.black.bc.socket.client.deal.TaskQryUserNewTaskCountDeal;
import com.coal.black.bc.socket.client.deal.TaskQryUserNewTaskListDeal;
import com.coal.black.bc.socket.client.deal.TaskQueryByTaskIDDeal;
import com.coal.black.bc.socket.client.deal.TaskQueryDeal;
import com.coal.black.bc.socket.client.deal.UploadFileDeal;
import com.coal.black.bc.socket.client.deal.UserTaskStatusChangeDeal;
import com.coal.black.bc.socket.client.handlers.UploadFileHandler;
import com.coal.black.bc.socket.client.returndto.BasicResult;
import com.coal.black.bc.socket.client.returndto.TaskQueryResult;
import com.coal.black.bc.socket.dto.ClientInfoDto;
import com.coal.black.bc.socket.enums.OperateType;
import com.coal.black.bc.socket.exception.ExceptionBase;

/**
 * 客户端处理的适配器
 * 
 * @author wanghui-bc
 *
 */
public class SocketClient {
	/**
	 * handler是一个对象，主要可以用来记录对应的运行时候的一些状态，目前只有上传文件的时候会传递这个handler，
	 * 主要是在真正处理的时候会不住的更新UploadFIleHandler中的uploadeSuccess和serverReceivedLength两个变量
	 * 
	 * @param operateType
	 * @param userId
	 * @param objDtoList
	 * @param handler
	 * @return
	 */
	public BasicResult deal(OperateType operateType, int userId, List<IDtoBase> objDtoList, Object handler) {
		ClientInfoDto clientDto = new ClientInfoDto();
		clientDto.setBeginFlag(Constants.SERVER_BEGIN_FLAG);
		clientDto.setMac(ClientGlobal.getMacBytes());
		if (userId <= 0) {
			clientDto.setUserId(ClientGlobal.getUserId());
		} else {
			clientDto.setUserId(userId);
		}

		Socket socket = null;
		OutputStream out = null;
		InputStream in = null;
		try {
			socket = new Socket();
			socket.connect(ClientGlobal.address, ClientGlobal.getSocketTimeOut());
			socket.setSoLinger(true, 5);
			socket.setTcpNoDelay(true);
			socket.setSoTimeout(ClientGlobal.getSocketTimeOut());
			out = socket.getOutputStream();
			in = socket.getInputStream();
			switch (operateType) {
			case LoginIn:
				clientDto.setOperateType(OperateType.LoginIn);
				return new LoginDeal().deal(clientDto, objDtoList, in, out);// 发送登陆的信息
			case SingIn:
				clientDto.setOperateType(OperateType.SingIn);
				return new SignInDeal().deal(clientDto, objDtoList, in, out);
			case UploadFile:
				UploadFileHandler uploadHandler = (UploadFileHandler) handler;
				clientDto.setOperateType(OperateType.UploadFile);
				return new UploadFileDeal().deal(clientDto, objDtoList, in, out, uploadHandler);
			case TaskQuery:
				clientDto.setOperateType(OperateType.TaskQuery);
				TaskQueryResult result = new TaskQueryDeal().deal(clientDto, objDtoList, in, out);
				return result;
			case UserTaskStatusChange:
				clientDto.setOperateType(OperateType.UserTaskStatusChange);
				return new UserTaskStatusChangeDeal().deal(clientDto, objDtoList, in, out);
			case TaskQryUserNewTaskCount:
				clientDto.setOperateType(OperateType.TaskQryUserNewTaskCount);// 查询用户新任务数目
				return new TaskQryUserNewTaskCountDeal().deal(clientDto, objDtoList, in, out);
			case TaskQryUserNewTaskList:
				clientDto.setOperateType(OperateType.TaskQryUserNewTaskList);// 查询用户新任务列表
				return new TaskQryUserNewTaskListDeal().deal(clientDto, objDtoList, in, out);
			case TaskQryByID:
				clientDto.setOperateType(OperateType.TaskQryByID);
				return new TaskQueryByTaskIDDeal().deal(clientDto, objDtoList, in, out);
			case ChangePwd:
				clientDto.setOperateType(OperateType.ChangePwd);
				return new ChangePwdDeal().deal(clientDto, objDtoList, in, out);
			case CommitTask:
				clientDto.setOperateType(OperateType.CommitTask);
				return new CommitTaskDeal().deal(clientDto, objDtoList, in, out);
			default:
				break;
			}
		} catch (Throwable ex) {
			BasicResult basicResult = new BasicResult();
			basicResult.setSuccess(false);
			basicResult.setBusException(false);
			basicResult.setThrowable(ex);
			basicResult.setBusinessErrorCode((byte) -1);
			return basicResult;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
		BasicResult basicResult = new BasicResult();
		basicResult.setSuccess(false);
		basicResult.setBusException(true);
		basicResult.setBusinessErrorCode(ExceptionBase.CLIENT_NO_DEAL_HANDLER);
		return basicResult;
	}
}
