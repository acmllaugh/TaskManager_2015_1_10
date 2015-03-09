package com.coal.black.bc.socket.dto;

import com.coal.black.bc.socket.IDtoBase;

public class ApkLastedVersionDto extends IDtoBase {
	private static final long serialVersionUID = 8328670246856980953L;

	private int versionNum;
	private String versionStr;
	private String desc;

	public int getVersionNum() {
		return versionNum;
	}

	public void setVersionNum(int versionNum) {
		this.versionNum = versionNum;
	}

	public String getVersionStr() {
		return versionStr;
	}

	public void setVersionStr(String versionStr) {
		this.versionStr = versionStr;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Override
	public String toString() {
		return "ApkLastedVersionDto [versionNum=" + versionNum + ", versionStr=" + versionStr + ", desc=" + desc + "]";
	}
}
