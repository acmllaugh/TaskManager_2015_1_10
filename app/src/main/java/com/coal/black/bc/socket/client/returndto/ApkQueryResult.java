package com.coal.black.bc.socket.client.returndto;

import com.coal.black.bc.socket.dto.ApkLastedVersionDto;

public class ApkQueryResult extends BasicResult {
	private ApkLastedVersionDto versionDto;

	public ApkLastedVersionDto getVersionDto() {
		return versionDto;
	}

	public void setVersionDto(ApkLastedVersionDto versionDto) {
		this.versionDto = versionDto;
	}

	public ApkQueryResult() {
	}

	public ApkQueryResult(BasicResult basicResult) {
		setSuccess(basicResult.isSuccess());
		setBusException(basicResult.isBusException());
		setBusinessErrorCode(basicResult.getBusinessErrorCode());
		setThrowable(basicResult.getThrowable());
	}
}
