package com.etek.controller.hardware.command;

/***
 * 雷管应用错误信息类
 * @author Xin Hongwei
 * @version v1.00
 */

public class DetErrorCode{

	//	成功
	public static final int SUCCESS = 0;

	//	硬件串口作物，范围在0x100和0x4FF之间
	//	串口通信错误
	public static final int ERR_COMM_OPEN = 0x101;
	public static final int ERR_COMM_SEND = 0x102;
	public static final int ERR_COMM_RECV_TIMEOUT = 0x103;
	public static final int ERR_COMM_NOT_OPEN = 0x104;

	//	协议层错误
	public static final int ERR_PROTOCOL_CHECK = 0x201;
	public static final int ERR_PROTOCOL_LENGTH = 0x202;
	public static final int ERR_PROTOCOL_NOT_ENOUGTH =0x203;

	private byte m_bCmd;
	private int m_nRSP;

	public DetErrorCode() {
		Reset();
	}

	public DetErrorCode(byte bCmd,int nRSP) {
		m_bCmd = bCmd;
		m_nRSP = nRSP;
	}

	/***
	 * 获取做进错误的信息，获取后就Reset了
	 * @return 错误信息
	 */
	public String GetErrorMessage() {
		String strerrmsg ="";

		if(m_nRSP==0x00) {
			strerrmsg =  "成功！";
			Reset();
			return strerrmsg;
		}

		///////////////////////////////////////////////////////////////////////////////////////
		//	通信层、协议层的错误信息
		///////////////////////////////////////////////////////////////////////////////////////
		if(m_nRSP>=0x100&&m_nRSP<0x500) {
			switch(m_nRSP) {
				case ERR_COMM_OPEN:
					strerrmsg =  "串口打开失败";
					break;
				case ERR_COMM_SEND:
					strerrmsg =  "串口发送数据失败";
					break;
				case ERR_COMM_RECV_TIMEOUT:
					strerrmsg =  "串口接收失败";
					break;
				case ERR_COMM_NOT_OPEN:
					strerrmsg ="串口没有打开";
					break;
				case ERR_PROTOCOL_CHECK:
					strerrmsg =  "接收数据校验错误";
					break;
				case ERR_PROTOCOL_LENGTH:
					strerrmsg =  "接收数据长度不足";
					break;
				case ERR_PROTOCOL_NOT_ENOUGTH:
					strerrmsg ="接收数据长度小于3";
					break;

				default:
					strerrmsg =String.format("未知错误[%02X]--%X",m_bCmd,m_nRSP);
					break;
			}
			Reset();
			return strerrmsg;
		}

		///////////////////////////////////////////////////////////////////////////////////////
		//	主板错误信息
		///////////////////////////////////////////////////////////////////////////////////////
		if(m_bCmd>=0x30&&m_bCmd<0x50){
			switch(m_nRSP){
				case 0xA1:
					strerrmsg =  "成功";
					break;
				case 0xA8:
					strerrmsg = "主板、指令格式或CRC错误";
					break;
				case 0xA9:
					strerrmsg ="芯片返回数据无效";
					break;
				case 0xAA:
					strerrmsg="芯片无应答";
					break;
				default:
					strerrmsg =String.format("未知错误[%02X]--%X",m_bCmd,m_nRSP);
					break;
			}
		}

		///////////////////////////////////////////////////////////////////////////////////////
		//	模块错误信息
		///////////////////////////////////////////////////////////////////////////////////////
		if(m_bCmd>=0x50) {
			switch(m_nRSP){
				case 0xA1:
					strerrmsg =  "成功";
					break;
				case 0xA8:
					strerrmsg = "指令本身错误，或获取的ID不合法";
					break;
				case 0xA9:
					strerrmsg ="获取模组的数据错误，或总线上有多颗模组";
					break;
				case 0xAA:
					strerrmsg="总线未上电，或无模组，或模组休眠，或开路等等";
					break;
				default:
					strerrmsg =String.format("未知错误[%02X]--%X",m_bCmd,m_nRSP);
					break;
			}
		}
		Reset();

		return strerrmsg;
	}

	/***
	 * 设置错误信息
	 * @param bCmd		命令码
	 * @param nRSP		错误码
	 */
	public void Setter(byte bCmd,int nRSP){
		m_bCmd = bCmd;
		m_nRSP = nRSP;
		return;
	}

	/***
	 * 复位错误信息
	 */
	private void Reset() {
		m_bCmd = 0x00;m_nRSP = 0;
	}

}

