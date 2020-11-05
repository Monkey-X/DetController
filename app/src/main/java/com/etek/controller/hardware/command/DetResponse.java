package com.etek.controller.tool.command;/*
 * <p> 核心板应答类  </p>
 * <p> 实现应答字节和字符串的转换以及应答码的封装</p>
 * <p> 创建时间： ${date}</p>
 * <p> @author Xin Hongwei</p>
 * <p> @version 1.00</p>
 * */

import com.etek.controller.tool.util.DataConverter;

public class DetResponse {
	private int m_bRSP;
	private String m_strData;
	private byte[] m_szData;

	public DetResponse() {
		m_bRSP =0xff;
		m_strData = "未设定";
		m_szData = null;
	}

	public DetResponse(int brsp,String strData) {
		this.m_bRSP = 0x00;
		this.m_strData = strData;
		m_szData = DataConverter.hexStringToBytes(strData);
	}

	public DetResponse(int brsp,byte[] szData) {
		this.m_bRSP = 0x00;
		m_szData = szData;
		this.m_strData = DataConverter.bytes2HexString(m_szData);
	}

	public int GetRSP() {
		int n =m_bRSP;
		if(n<0){
			n = n &0x000000ff;
		}
		return n;
	}

	public String GetRespData() {
		return this.m_strData;
	}

	public void SetRSP(int nrsp) {
		this.m_bRSP = nrsp;
	}

	public void SetRespData(String strData) {
		this.m_strData = strData;
		m_szData = DataConverter.hexStringToBytes(strData);
	}

	public void SetRespData(byte[] szData) {
		m_szData = szData;
		this.m_strData = DataConverter.bytes2HexString(szData);
	}

	public byte[] GetRespSZData() {
		return m_szData;
	}

	@Override
	public String toString() {
		return "DetResponse{" +
				"flg=" + m_bRSP +
				", num=" + m_strData +
				'}';
	}

}
