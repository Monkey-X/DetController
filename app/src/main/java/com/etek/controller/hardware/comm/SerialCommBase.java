package com.etek.controller.hardware.comm;

/***
 * 串口通信基类，便于嵌入式和桌面同时测试
 */

public abstract class SerialCommBase {
	public String m_strPortName="COM3";
	public int m_nBaudrate=115200;
	//	缺省为3秒
	public int m_nTimeout = 5000;
	public int m_nErrorCode =0;


	public SerialCommBase(String portName,int nBaud){
		m_strPortName = portName;
		m_nBaudrate = nBaud;
	}

	public abstract int OpenPort();

	/***
	 * 关闭打开的串口
	 */
	public abstract void ClosePort();

	/***
	 * 发送字节流
	 * @param szcmd		发送的字节流
	 * @return
	 * 0	成功
	 * 其他		失败
	 */
	public abstract int SendBlock(byte[] szcmd);

	/***
	 * 接收指定的字节流
	 * @param nLen		期望接收的字节流数
	 * @return
	 * 	成功：	字节流
	 * 	其他：失败
	 * @throws InterruptedException
	 */
	public abstract byte[] RecvBlock(int nLen);

	/***
	 * 发送接收
	 * @param szcmd		发送的字节流
	 * @param nLen		期望接收的字节流个数
	 * @return
	 * 	成功：	字节流
	 * 	失败：	null
	 */
	public abstract byte[] SendRecv(byte[] szcmd,int nLen);

	/*
	 * 接收超时设置
	 * nTimeout: 接收超时时间，毫秒
	 * */
	public void SetTimeout(int nTimeout) {
		m_nTimeout = nTimeout;
	}

	public int GetTimeout() {
		return m_nTimeout;
	}

	/***
	 * 情书串口的输入缓存
	 */
	public abstract void FlushComm();

	/***
	 * 等待指令的时间，判断串口是否有数据输入
	 * @return
	 */
	public abstract int WaitTimeout();

	public int GetErrorCode() {
		return m_nErrorCode;
	}


	/**
	 * 开关总电源
	 *
	 * @param operationValue 1,2 开关总电源  3,4 开关串口S0电源    5,6开关串口S1电源
	 * @return true 成功，false失败
	 */
	public abstract boolean ctlPowerSupply(int operationValue);

	// true 拉高，false拉低
	public abstract boolean controlGpio73(boolean ifpullHigh);

	//获取GPIO74的值
	public abstract String getGpio74();
}
