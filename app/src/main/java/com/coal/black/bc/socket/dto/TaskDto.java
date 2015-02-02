package com.coal.black.bc.socket.dto;

import com.coal.black.bc.socket.IDtoBase;

public class TaskDto extends IDtoBase {
	private static final long serialVersionUID = 672584624580279518L;
	private Integer id = 0;
	private String bank;
	private String caseID;
	private String name;
	private String cardOwnedName;// 持卡人姓名
	private String identityCard;
	private String bankCard;
	private double caseAmount = 0;
	private double hasPayed = 0;
	private String noticeStatement;
	private String address;
	private String addressType;// 地址类型
	private Integer visitTimes = 0;
	private String visitReason;
	private String requirement;
	private String memo;
	private String contactInfo;
	private String companyName;
	private String estimateVisitDate;
	private String province;// 省
	private String city;// 市
	private String region;// 区
	private boolean isUrgent = false;// 是否加急
	private Integer taskStatus = 0;
	private Integer userTaskStatus = 0;
	private Integer taskFlowTimes = 0;// 流程的次数
	private Long grantTime = 0L;// 任务授予的时间
	private Long operateTime = 0L;// 任务操作的时间
	private Long businessID;// 业务ID

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public String getCaseID() {
		return caseID;
	}

	public void setCaseID(String caseID) {
		this.caseID = caseID;
	}

	public String getIdentityCard() {
		return identityCard;
	}

	public void setIdentityCard(String identityCard) {
		this.identityCard = identityCard;
	}

	public String getBankCard() {
		return bankCard;
	}

	public void setBankCard(String bankCard) {
		this.bankCard = bankCard;
	}

	public double getCaseAmount() {
		return caseAmount;
	}

	public void setCaseAmount(double caseAmount) {
		this.caseAmount = caseAmount;
	}

	public double getHasPayed() {
		return hasPayed;
	}

	public void setHasPayed(double hasPayed) {
		this.hasPayed = hasPayed;
	}

	public String getNoticeStatement() {
		return noticeStatement;
	}

	public void setNoticeStatement(String noticeStatement) {
		this.noticeStatement = noticeStatement;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public int getVisitTimes() {
		return visitTimes;
	}

	public String getVisitReason() {
		return visitReason;
	}

	public void setVisitReason(String visitReason) {
		this.visitReason = visitReason;
	}

	public String getRequirement() {
		return requirement;
	}

	public void setRequirement(String requirement) {
		this.requirement = requirement;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getContactInfo() {
		return contactInfo;
	}

	public void setContactInfo(String contactInfo) {
		this.contactInfo = contactInfo;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getEstimateVisitDate() {
		return estimateVisitDate;
	}

	public void setEstimateVisitDate(String estimateVisitDate) {
		this.estimateVisitDate = estimateVisitDate;
	}

	public Integer getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(Integer taskStatus) {
		this.taskStatus = taskStatus;
	}

	public Integer getUserTaskStatus() {
		return userTaskStatus;
	}

	public void setUserTaskStatus(Integer userTaskStatus) {
		this.userTaskStatus = userTaskStatus;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getGrantTime() {
		return grantTime;
	}

	public void setGrantTime(Long grantTime) {
		this.grantTime = grantTime;
	}

	public Long getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(Long operateTime) {
		this.operateTime = operateTime;
	}

	public void setVisitTimes(Integer visitTimes) {
		this.visitTimes = visitTimes;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public boolean isUrgent() {
		return isUrgent;
	}

	public void setUrgent(boolean isUrgent) {
		this.isUrgent = isUrgent;
	}

	public Long getBusinessID() {
		return businessID;
	}

	public void setBusinessID(Long businessID) {
		this.businessID = businessID;
	}

	public Integer getTaskFlowTimes() {
		return taskFlowTimes;
	}

	public void setTaskFlowTimes(Integer taskFlowTimes) {
		this.taskFlowTimes = taskFlowTimes;
	}

	public String getCardOwnedName() {
		return cardOwnedName;
	}

	public void setCardOwnedName(String cardOwnedName) {
		this.cardOwnedName = cardOwnedName;
	}

	public String getAddressType() {
		return addressType;
	}

	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}

	@Override
	public String toString() {
		return "TaskDto [id=" + id + ", bank=" + bank + ", caseID=" + caseID + ", name=" + name + ", cardOwnedName=" + cardOwnedName + ", identityCard="
				+ identityCard + ", bankCard=" + bankCard + ", caseAmount=" + caseAmount + ", hasPayed=" + hasPayed + ", noticeStatement=" + noticeStatement
				+ ", address=" + address + ", addressType=" + addressType + ", visitTimes=" + visitTimes + ", visitReason=" + visitReason + ", requirement="
				+ requirement + ", memo=" + memo + ", contactInfo=" + contactInfo + ", companyName=" + companyName + ", estimateVisitDate=" + estimateVisitDate
				+ ", province=" + province + ", city=" + city + ", region=" + region + ", isUrgent=" + isUrgent + ", taskStatus=" + taskStatus
				+ ", userTaskStatus=" + userTaskStatus + ", taskFlowTimes=" + taskFlowTimes + ", grantTime=" + grantTime + ", operateTime=" + operateTime
				+ ", businessID=" + businessID + "]";
	}
}
