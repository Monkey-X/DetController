package com.etek.controller.tool.command;/*
 * <p> 核心板通信协议类  </p>
 * <p> 主要实现按照协议格式发送和接收命令</p>
 * <p> 创建时间： ${date}</p>
 * <p> @author Xin Hongwei</p>
 * <p> @version 1.00</p>
 * */

import com.etek.controller.tool.comm.SerialCommBase;
import com.etek.controller.tool.util.DataConverter;

public class DetProtocol {
	private SerialCommBase m_commobj;
	private final boolean DEBUG_PRINT = true;

	public DetProtocol(SerialCommBase serialobj){
		m_commobj = serialobj;
	}

	/*
	 * 获取CRC8校验
	 * nLen: 输入字节流长度
	 * szdata: 字节流
	 * */
	private byte GetCRC8(int nLen,byte[] szdata) {
		int wcrc = 0x00;
		int i,j;

		for(j=nLen;j>0;j--) {
			wcrc = wcrc ^ szdata[nLen-j]*0x100;

			for(i=8;i>0;i--) {
				if((wcrc&0x8000)!=0)
					wcrc = wcrc^(0x1070<<3);
				wcrc = wcrc<<1;
			}
		}

		byte b = (byte)(wcrc>>8);

		return b;
	}

	/***
	 * 发送一个BLOCK，校验由协议计算
	 * @param szCmd
	 * @return
	 */
	public int SendBlock(byte[] szCmd) {

		if(null==szCmd) return 0;

		int n = szCmd.length+1;

		byte[] szcmd = new byte[n];
		System.arraycopy(szCmd, 0, szcmd, 0, n-1);
		//	CRC8;
		szcmd[n-1] = GetCRC8(n-1,szcmd);

		if(DEBUG_PRINT) {
			System.out.println(String.format("\t命令：%s",DataConverter.bytes2HexString(szcmd)));
		}
		int ret = m_commobj.SendBlock(szcmd);
		return ret;
	}

	/***
	 * 接收一个BLOCK，校验去除
	 * @param nLen		期望回复的长度
	 * @param resp		DetResponse返回的数据
	 * @return
	 */
	public int RecvBlock(int nLen,DetResponse resp) {
		byte[] data =  m_commobj.RecvBlock(nLen);
		if(null==data) return m_commobj.GetErrorCode();

		//check crc8;
		int n = data.length;
		byte ncrc =GetCRC8(n-1,data);
		if(ncrc!=data[n-1]) return DetErrorCode.ERR_PROTOCOL_CHECK;

		// Data
		byte[] szResp = new byte[n-1];
		System.arraycopy(data, 0, szResp, 0, n-1);

		if(null==resp)
			resp = new DetResponse(0x00,szResp);
		resp.SetRSP(0x00);
		resp.SetRespData(szResp);

		return 0;
	}



	/*
	 *发送和接收
	 * bCmd: 命令字节
	 * szParam: 命令所带参数
	 * nLen:	期望返回的数据长度
	 * resp:	返回的数据
	 * */
	public int SendRecv(byte bCmd,byte[] szParam,
						int nLen,DetResponse resp) {

		int n = 3;
		if(null!=szParam) n =  szParam.length+3;

		byte[] szcmd = new byte[n];
		//	CMD
		szcmd[0] = bCmd;
		//	Length
		szcmd[1] = (byte)(n-3);

		// DATA
		if(null!=szParam)
			System.arraycopy(szParam, 0,szcmd,2,szParam.length);

		//	CRC8;
		szcmd[n-1] = GetCRC8(n-1,szcmd);

		if(DEBUG_PRINT) {
			System.out.println(String.format("\t命令：%s", DataConverter.bytes2HexString(szcmd)));
		}

		long t0 = System.currentTimeMillis();
		byte[] data = m_commobj.SendRecv(szcmd, nLen+3);
		long t1 = System.currentTimeMillis();

		if(DEBUG_PRINT) {
			System.out.println(String.format("命令：%02X\t耗时:%d ms",bCmd,t1-t0));
			if(null==data)
				System.out.println("应答: 无");
			else
				System.out.println(String.format("\t应答：%s",DataConverter.bytes2HexString(data)));
		}

		if(null==data) return m_commobj.GetErrorCode();

		n = data.length;
		if(n<3)
			return DetErrorCode.ERR_PROTOCOL_NOT_ENOUGTH;

		//check crc8;
		byte ncrc =GetCRC8(n-1,data);
		if(ncrc!=data[n-1]) return DetErrorCode.ERR_PROTOCOL_CHECK;

		// RSP
		int bRSP = (int) data[0];

		// LEN
		//	长度有效性判断
		n = data[1];
		if(n+3!=data.length)
			return DetErrorCode.ERR_PROTOCOL_LENGTH;

		// Data
		byte[] szResp = null;
		if(n>0) {
			szResp = new byte[n];
			System.arraycopy(data, 2, szResp, 0, n);
		}

		if(null==resp)
			resp = new DetResponse(bRSP,szResp);
		resp.SetRSP(bRSP);
		resp.SetRespData(szResp);

		return 0;
	}

}

