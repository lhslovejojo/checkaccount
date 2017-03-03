package com.lef.checkaccount.entity;

import java.io.Serializable;

public class AnalysisRecord implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	/**
	 * 对账日期
	 */
	private String analysisDay;
	/**
	 * 状态，执行中;成功；失败
	 */
	private String status;
	/**
	 * 异常步骤
	 */
	private Integer errorStep;
	/**
	 * 异常步骤说明
	 */
	private String errorStepDesc;
	/**
	 * 异常描述
	 */
	private String errorMsg;
	/**
	 * 异常编码
	 */
	private String errorCode;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAnalysisDay() {
		return analysisDay;
	}

	public void setAnalysisDay(String analysisDay) {
		this.analysisDay = analysisDay;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getErrorStep() {
		return errorStep;
	}

	public void setErrorStep(Integer errorStep) {
		this.errorStep = errorStep;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorStepDesc() {
		return errorStepDesc;
	}

	public void setErrorStepDesc(String errorStepDesc) {
		this.errorStepDesc = errorStepDesc;
	}

}
