package jdo.reg.services;

import java.util.List;


public class RegResponse {
	private String IsSuccessed; //0 成功  1 失败
	private String ErrorMsg;
	private Result Result;
	private String IpdNo;
	private String PatientName;
	private String Station;
	private String CtzDesc;
	private String Bed;
	private String Price;
	private String DeptCode;
	private String TotalPrice;
	private String OverTimeStatus; //退费状态 0失败 1成功 2执行中
	public String getOverTimeStatus() {
		return OverTimeStatus;
	}
	public void setOverTimeStatus(String overTimeStatus) {
		OverTimeStatus = overTimeStatus;
	}
	
	public List<Result> getResults() {
		return Results;
	}
	public void setResults(List<Result> results) {
		Results = results;
	}
	private List<Result> Results;
	
	public String getIsSuccessed() {
		return IsSuccessed;
	}
	public void setIsSuccessed(String isSuccessed) {
		IsSuccessed = isSuccessed;
	}
	public String getErrorMsg() {
		return ErrorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		ErrorMsg = errorMsg;
	}
	public Result getResult() {
		return Result;
	}
	public void setResult(Result result) {
		Result = result;
	}
	public String getIpdNo() {
		return IpdNo;
	}
	public void setIpdNo(String ipdNo) {
		IpdNo = ipdNo;
	}
	public String getPatientName() {
		return PatientName;
	}
	public void setPatientName(String patientName) {
		PatientName = patientName;
	}
	public String getStation() {
		return Station;
	}
	public void setStation(String station) {
		Station = station;
	}
	public String getCtzDesc() {
		return CtzDesc;
	}
	public void setCtzDesc(String ctzDesc) {
		CtzDesc = ctzDesc;
	}
	public String getBed() {
		return Bed;
	}
	public void setBed(String bed) {
		Bed = bed;
	}
	public String getPrice() {
		return Price;
	}
	public void setPrice(String price) {
		Price = price;
	}
	public String getDeptCode() {
		return DeptCode;
	}
	public void setDeptCode(String deptCode) {
		DeptCode = deptCode;
	}
	public String getTotalPrice() {
		return TotalPrice;
	}
	public void setTotalPrice(String totalPrice) {
		TotalPrice = totalPrice;
	}

	

}
