package com.coal.black.bc.socket.utils;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamUtils {
	/**
	 * 从in中读取定长的字节数组
	 * 
	 * @param dataLength
	 * @param in
	 * @return
	 */
	public static byte[] readFixedLengthData(int dataLength, InputStream in) throws IOException {
		if (dataLength <= 0) {
			return new byte[0];
		}
		byte[] data = new byte[dataLength];// 首先定义一个长度为dataLength的数组
		int length = 0;
		byte[] dataTemp = new byte[1024];// 定义一个1K的缓冲区
		int total = 0;// 总共接收到的数据的大小
		while (true) {
			if ((dataLength - total) >= 1024) {// 表示剩余的数据还是大于等于1024的
				length = in.read(dataTemp, 0, 1024);// 每次最多读取1024个字节
			} else {
				length = in.read(dataTemp, 0, (dataLength - total));// 否则只读取剩余的数量
			}
			if (length == -1) {
				break;
			}
			System.arraycopy(dataTemp, 0, data, total, length);
			total += length;
			if (total >= dataLength) {
				break;
			}
		}
		if (total < dataLength) {// 读取结束了的话，发现total和datalength相比要小，则抛出异常
			throw new IOException("The total readed bytes length is " + total + " is less than need read length " + dataLength);
		}
		return data;
	}
}
