package com.etek.controller.minaclient;


public class MinaMessageSend {

	private String sn;
	private String commType = "R";
	private String packNo;

	public MinaMessageSend(String sn, String commType, String packNo) {
		super();
		this.sn = sn;
		this.commType = commType;
		this.packNo = packNo;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getCommType() {
		return commType;
	}

	public void setCommType(String commType) {
		this.commType = commType;
	}

	public String getPackNo() {
		return packNo;
	}

	public void setPackNo(String packNo) {
		this.packNo = packNo;
	}

}
