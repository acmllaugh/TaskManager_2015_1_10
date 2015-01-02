package com.coal.black.bc.socket.client;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import com.coal.black.bc.socket.utils.CommonUtils;

public class ClientGlobal {
	//private static String serverIp = "127.0.0.1";
	public static String serverIp = "223.68.133.15";
	private static int serverPort = 12323;
	private static int socketTimeOut = 30000;

	public static SocketAddress address;

	private static String macStr;// mac地址字符串（以":"分割）
	private static int userId = 0;
	private static byte[] macBytes;
	static {
		address = new InetSocketAddress(serverIp, serverPort);
		setMacAddress("7C:E9:D3:EF:FA:10");
	}

	public static void setUserId(int userID) {
		userId = userID;
	}

	public static void setMacAddress(String macAddress) {
		macStr = macAddress;
		macBytes = CommonUtils.macString2Bytes(macStr);
	}

	public static int getSocketTimeOut() {
		return socketTimeOut;
	}

	public static SocketAddress getAddress() {
		return address;
	}

	public static String getMacStr() {
		return macStr;
	}

	public static int getUserId() {
		return userId;
	}

	public static byte[] getMacBytes() {
		return macBytes;
	}
}
