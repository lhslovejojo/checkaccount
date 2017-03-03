package com.lef.checkaccount.Exception;

public class AnalysisException extends RuntimeException {
	private String retCd;
	private String msgDes;
	private int errorTask;

	public int getErrorTask() {
		return errorTask;
	}

	public void setErrorTask(int errorTask) {
		this.errorTask = errorTask;
	}

	public AnalysisException() {
		super();
	}
	public AnalysisException(Throwable e) {
		super(e);
	}

	public AnalysisException(String message) {
		super(message);
		msgDes = message;
	}

	public AnalysisException(String retCd, String msgDes, int errorTask) {
		super();
		this.retCd = retCd;
		this.msgDes = msgDes;
		this.errorTask=errorTask;
	}
	public AnalysisException(String retCd, String msgDes) {
		super();
		this.retCd = retCd;
		this.msgDes = msgDes;
	}
	public AnalysisException(String retCd, String msgDes,int errorTask,Throwable e) {
		super(e);
		this.retCd = retCd;
		this.msgDes = msgDes;
		this.errorTask=errorTask;
	}

	public String getRetCd() {
		return retCd;
	}

	public String getMsgDes() {
		return msgDes;
	}
}
