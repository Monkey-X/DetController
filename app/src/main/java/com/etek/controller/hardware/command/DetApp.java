/*
 * <p> 核心板通信应用类  </p>
 * <p> 主要根据安卓APP的调用实现指令封装</p>
 * <p> 创建时间： ${date}</p>
 * <p> @author Xin Hongwei</p>
 * <p> @version 1.00</p>
 * */

package com.etek.controller.hardware.command;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


import com.etek.controller.hardware.comm.SerialCommBase;
import com.etek.controller.hardware.test.InitialCheckCallBack;
import com.szyd.jni.HandSetSerialComm;
import com.etek.controller.hardware.test.DetCallback;
import com.etek.controller.hardware.util.DataConverter;


public class DetApp {
	private SerialCommBase m_commobj;
	private DetCmd m_cmdObj;	
	private DetErrorCode m_detError;
	
	private String TAG = "DetApp";


	private DetApp(){}

	public static DetApp getInstance(){
		return SingletonHoler.sIntance;
	}

	private static class SingletonHoler{
		private static final DetApp sIntance = new DetApp();
	}

	/***
	 * 初始化过程，打开串口，创建内部对象
	 * @return
	 */
	public int Initialize() {	
		m_detError=new DetErrorCode((byte)0x00,0);
		
		m_commobj = new HandSetSerialComm( "/dev/ttyS1",115200);
		m_cmdObj = new DetCmd(m_commobj); 		

		int ret = m_commobj.OpenPort();
		
		m_detError.Setter((byte)0x00, ret);
		
		return ret;
	}

	/***
	 * 结束函数，关闭串口，释放对象
	 */
	public void Finalize() {
		if(null!=m_commobj){
			m_commobj.ClosePort();
		}
		
		m_commobj = null;		
		m_detError = null;
		Log.d(TAG, "Finalize: ");
		
		return;
	}


	/*
	 * 功能：核心板ECHO测试
	 *
	 * */
	public int MainBoardEcho() {
		int ret =  m_cmdObj.BoardCmd30();
		m_detError.Setter((byte)0x30, ret);
		return ret;
	}

	/***
	 * 返回最近一次错误的解析
	 * @return	错误信息
	 */
	public String GetErrorMessage() {
		return m_detError.GetErrorMessage();
	}

	/*
	 * 功能：获取版本信息
	 * */	
	public String MainBoardGetVersion(){
		StringBuilder strVer = new StringBuilder();
		
		int ret = m_cmdObj.BoardCmd31(strVer);

		m_detError.Setter((byte)0x31, ret);
		
		return strVer.toString();
	}

	/*
	 * 功能：获取配置表
	 * */
	public String MainBoardGetConfig(){
		StringBuilder strConfig = new StringBuilder();
		
		int ret = m_cmdObj.BoardCmd33(strConfig);

		m_detError.Setter((byte)0x33, ret);
		
		return strConfig.toString();
	}
	
	public String MainBoardGetSNO() {
		StringBuilder strSNO = new StringBuilder();
		
		int ret = m_cmdObj.BoardCmd32(strSNO);
		
		m_detError.Setter((byte)0x32, ret);
		
		return strSNO.toString();
	}

	/*
	 * 功能：总线电源打开并低压使能
	 * */
	public int MainBoardLVEnable() {
		int ret = m_cmdObj.BoardCmd40();

		m_detError.Setter((byte)0x40, ret);
		
		return ret;
	}

	/*
	 * 功能：总线电源关闭
	 * */
	public int MainBoardBusPowerOff() {
		int ret = m_cmdObj.BoardCmd41();		

		m_detError.Setter((byte)0x41, ret);

		Log.d(TAG, "MainBoardBusPowerOff: "+ ret);
		return ret;
	}

	/*
	 * 功能：总线电源打开并高压使能
	 * */
	public int MainBoardHVEnable() {
		int ret = m_cmdObj.BoardCmd42();
		
		m_detError.Setter((byte)0x42, ret);
		
		return ret;
	}


	/*
	 * 功能：总线电源打开并高压使能
	 * */
	public int MainBoardSetBus(boolean bHigh) {
		int ret = 0;
		byte bCmd = 0x45;
		
		if(bHigh){
			bCmd = 0x045;
			ret = m_cmdObj.BoardCmd45();
		}
		else {
			bCmd = 0x44;
			ret = m_cmdObj.BoardCmd44();			
		}
		
		m_detError.Setter(bCmd, ret);
		
		return ret;
	}

	/***
	 * 获取主板的电流电压值
	 * @param strData	电流和电压值
	 * @return
	 */
	public int MainBoardGetCurrentVoltage(StringBuilder strData) {
		int ret = m_cmdObj.BoardCmd4A(strData);

		m_detError.Setter((byte)0x4A, ret);

		return ret;
	}

	/*
	 * 功能：获取总线保护状态
	 * */
	public int MainBoardGetBusStatus() {
		int ret = m_cmdObj.BoardCmd4B();
		
		m_detError.Setter((byte)0x4B, ret);
		
		return ret;	
	}

	/***
	 * 主控板上电（供电）
	 * @return
	 */
	public int MainBoardPowerOn() {
		int ret = m_cmdObj.BoardPowerOn();
		return ret;
	}

	/***
	 * 主控板下电（断电）
	 * @return
	 */
	public int MainBoardPowerOff() {
		int ret = m_cmdObj.BoardPowerOff();
		return ret;
	}

	/***
	 * BL电平拉高或拉低
	 * @param bHigh
	 * @return
	 */
	public int MainBoardSetBL(boolean bHigh) {
		int ret = m_cmdObj.BoardSetBL(bHigh);
		return ret;
	}

	/***
	 * 获取GPIO74状态，是0？1？
	 * @return
	 */
	public int MainBoardGetGPIO74() {
		int ret = m_cmdObj.BoardGetGPIO74();
		return ret;
	}


	/***
	 * 获取模组ID
	 * @return 模组ID，如果返回-1表示出错
	 */
	public int ModuleGetID() {
		int ret;
		StringBuilder strData = new StringBuilder();
		ret = m_cmdObj.ModCmd50(strData);

		m_detError.Setter((byte)0x50, ret);
		
		if(0!=ret) return -1;
		
		byte[] arr = DataConverter.hexStringToBytes(strData.toString());
		ret = DataConverter.lsbBytes2Int(arr);
		
		return ret;
	}


	/***
	 * 根据模组ID获取管码
	 * @param nID	输入的模组ID
	 * @return 字符串格式的管码，如果失败返回为空
	 */
	public String ModuleGetDC(int nID) {
		int ret;
		StringBuilder strData = new StringBuilder();
		ret = m_cmdObj.ModCmd52(nID,strData);
		
		if(0!=ret){
			m_detError.Setter((byte)0x52, ret);
			strData.setLength(0);
			return "";
		}
		
		int i;
		int[] nval = new int[8];
		String str0 = strData.toString();
		for(i=0;i<16;i=i+2) {
			ret = DataConverter.charToByte(str0.charAt(i));
			ret = ret *0x10 + DataConverter.charToByte(str0.charAt(i+1));
			nval[i/2]=ret;
		}
		
		ret = nval[6];
		ret = ret *0x100 + nval[5];
		str0 = String.format("%02d%01d%02d%02d%c%03d%02d",
				nval[0],nval[1],nval[2],nval[3],nval[4],ret,nval[7]);
		
		return str0;
	}

	/***
	 * 读取模组延时
	 * @param nID
	 * @return
	 */
	public int ModuleGetDelayTime(int nID) {
		int ret;
		StringBuilder strData = new StringBuilder();
		ret = m_cmdObj.ModCmd54(nID,strData);
		
		m_detError.Setter((byte)0x54, ret);
		
		if(0!=ret) return ret;
		
		byte[] arr = DataConverter.hexStringToBytes(strData.toString());
		ret = DataConverter.lsbBytes2Int(arr);
		
		return ret;
	}

	/***
	 * 设置模组延时（毫秒）
	 * @param nID		模组ID
	 * @param nDT		延时，毫秒
	 * @return
	 */
	public int ModuleSetDelayTime(int nID,int nDT) {
		int ret;
		
		ret = m_cmdObj.ModCmd55(nID,nDT);	
		
		m_detError.Setter((byte)0x55, ret);
		
		return ret;
	}

	/***
	 * 设置模组IO 口输出状态（本指令主要用于总线是否脱落检测）
	 * @param nID
	 * @param bIO	IO 字节低2 位有效
	 * @return
	 */
	public int ModuleSetIOStatus(int nID,byte bIO) {
		int ret;
		ret = m_cmdObj.ModCmd58(nID,bIO);
		
		m_detError.Setter((byte)0x58, ret);
		
		return ret;
	}

	/***
	 * 设置模组进入休眠状态
	 * @param nID
	 * @return
	 */
	public int ModuleSetDormantStatus(int nID) {
		int ret;
		ret = m_cmdObj.ModCmd59(nID);
		
		m_detError.Setter((byte)0x59, ret);
		
		return ret;
	}

	/***
	 * 设置模组进入唤醒状态
	 * @param nID
	 * @return
	 */
	public int ModuleSetWakeupStatus(int nID) {
		int ret;
		ret = m_cmdObj.ModCmd5A(nID);
		
		m_detError.Setter((byte)0x5A, ret);
		
		return ret;
	}

	/***
	 * 模组发火电容充电和放电
	 * @param nID
	 * @param bCharge		true表示充电，false表示放电
	 * @return
	 */
	public int ModuleCapacitorCharge(int nID,boolean bCharge) {
		int ret;
		byte bCmd = 0x00;
		if(bCharge) {
			bCmd = 0x58;
			ret = m_cmdObj.ModCmd5B(nID);			
		}
		else {
			bCmd = 0x5B;
			ret = m_cmdObj.ModCmd5C(nID);			
		}
		
		m_detError.Setter(bCmd, ret);
		
		return ret;
	}

	/***
	 * 模组药头检测
	 * @param nID
	 * @return
	 */
	public int ModuleCheckPowderCable(int nID) {
		int ret;
		ret = m_cmdObj.ModCmd5D(nID);
		
		m_detError.Setter((byte)0x5D, ret);
		
		return ret;
	}

	/***
	 * 单颗模组是否在线检测
	 * @param nID
	 * @return
	 */
	public int ModuleSingleCheck(int nID) {
		int ret;
		ret = m_cmdObj.ModCmd5E(nID);
		
		m_detError.Setter((byte)0x5E, ret);
		
		return ret;
	}

	/***
	 * 雷管网络起爆
	 * @return
	 */
	public int ModuleDetonate(int nID) {
		int ret;
		ret = m_cmdObj.ModCmd5F(nID);
		return ret;
	}

	/***
	 * 模组延时参数校准
	 * @param nID
	 * @return
	 */
	public int ModuleDelayTimeAdjust(int nID) {
		int ret;
		
		ret = m_cmdObj.ModCmd60(nID);
		
		m_detError.Setter((byte)0x60, ret);
		
		return ret;
	}

	/***
	 * ID 的大于等于检查
	 * @param nID
	 * @return
	 */
	public int ModuleCheckMoreEqual(int nID) {
		int ret;
		
		ret = m_cmdObj.ModCmd61(nID);
		
		m_detError.Setter((byte)0x61, ret);
		
		return ret;		
	}

	/***
	 * ID 的小于检查
	 * @param nID
	 * @return
	 */
	public int ModuleCheckLessThan(int nID) {
		int ret;
		
		ret = m_cmdObj.ModCmd62(nID);
		
		m_detError.Setter((byte)0x62, ret);
		
		return ret;		
	}

	/***
	 * 下载主控板程序流程
	 * @param strBINFileName
	 * @return
	 */
	public int DownloadProc(String strBINFileName, DetCallback cbobj) {
		int ret;
		int nFizeSize;
		BufferedInputStream in=null;
		int WAIT_TIME_MS,k;

		final int PACKAGE_SIZE = 1024;

		//	发送6字节同步指令应答
		final byte[] CMD_SYN_RESPONSE = {0x42,0x65,0x6c,0x6c,0x65,0x20};
				
		DetCmd cmd = new DetCmd(m_commobj);
				
		File file = new File(strBINFileName);
        if (!file.exists() || !file.isFile()) {
        	if(null!=cbobj)
        		cbobj.DisplayText("文件不存在");
            return -1;
        }
        nFizeSize = (int) file.length();

		//	将BL引脚拉低
		ret = cmd.BoardSetBL(false);
		if(0!=ret) {
        	if(null!=cbobj)
        		cbobj.DisplayText("BL电平拉低失败!");
            return -1;
		}

		//	核心板5V供电
		ret = cmd.BoardPowerOn();
		if(0!=ret) {
        	if(null!=cbobj)
        		cbobj.DisplayText("核心板5V供电失败!");
            return -1;
		}

    	if(null!=cbobj)
    		cbobj.DisplayText("等待2秒");

		//	等待2s（丢弃所有UART口收到的数据）
    	WAIT_TIME_MS = 2000;
    	try {
    		for(k=0;k<WAIT_TIME_MS/100;k++) {
    			Thread.sleep(100);
    			
    			float f =  WAIT_TIME_MS - k*100;
    			
    	    	if(null!=cbobj)
    	    		cbobj.DisplayText(String.format("等待%.1f秒", f/1000));
    		}
    			
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		//	将BL脚置高（此时核心板进入BL状态）
		ret = cmd.BoardSetBL(true);
		if(0!=ret) {
        	if(null!=cbobj)
        		cbobj.DisplayText("BL脚置高失败!");
            return -1;
		}

		//	监控UART口,收到6字节同步指令？
		//	47 61 73 74 6F 6E
		m_commobj.SetTimeout(10000);		
		ret = m_commobj.WaitTimeout();
		if(0!=ret) {
	    	if(null!=cbobj)
	    		cbobj.DisplayText("未收到同步指令");
		}
		m_commobj.SetTimeout(3000);
			
		byte[] szData = m_commobj.RecvBlock(6);
		if(null==szData) {
	    	if(null!=cbobj)
	    		cbobj.DisplayText("未收到同步指令");
	    	return -1;
		}
		String str0 = DataConverter.bytes2HexString(szData);
		if(!"476173746f6e".equals(str0)) {
	    	if(null!=cbobj)
	    		cbobj.DisplayText("未收到同步指令 476173746f6e");
	    	return -1;
		}


		//	发送6字节应答指令
		ret = m_commobj.SendBlock(CMD_SYN_RESPONSE);
		if(0!=ret) {
        	if(null!=cbobj)
        		cbobj.DisplayText("发送同步指令应答 失败!");
            return -1;
		}
		
		
		while(true) {
			ret = m_commobj.WaitTimeout();
			if(0!=ret) {
		    	if(null!=cbobj)
		    		cbobj.DisplayText("未收到 发送6字节应答指令 应答");
		    	return -1;
			}
			
			byte[] data = m_commobj.RecvBlock(1);
			if(data[0]==0x47){
				data = m_commobj.RecvBlock(5);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				continue;
			}
			
			if(data[0]==0x53) {
				szData = new byte[7];
				szData[0]=0x57;
				data = m_commobj.RecvBlock(6);
				if(null!=data) {
					System.arraycopy(data, 0, szData, 1, data.length);
					break;
				}
			}
			
			if(data[0]==0x55) {
				szData = new byte[6];
				szData[0]=0x55;
				data = m_commobj.RecvBlock(5);
				if(null!=data) {
					System.arraycopy(data, 0, szData, 1, data.length);
					break;
				}
			}
		}

		//	收到7字节确认指令？
		//	53 68 69 HWL HWH BWL BWH	HW：硬件版本，BW:Bootloader（固件）版本

		str0 = DataConverter.bytes2HexString(szData);
		if("536869".equals(str0.subSequence(0, 6))){
        	if(null!=cbobj)
        		cbobj.DisplayText("确认指令错误!");
            return -1;
		}

		//	发送7字节固件升级参数包
		szData = new byte[7];
		szData[0]=0x44;szData[1]=0x50;szData[2]=0x00;
		byte[] szSize = DataConverter.int2BytesLSB(nFizeSize);
		System.arraycopy(szSize, 0, szData, 3, szSize.length);
		
		m_commobj.FlushComm();
		ret = m_commobj.SendBlock(szData);
		if(0!=ret) {
	    	if(null!=cbobj)
	    		cbobj.DisplayText("发送7字节固件升级参数 失败");
	    	return -1;
		}

		//	超时等待（此时核心板需要做擦除动作）
    	WAIT_TIME_MS = 5000;
    	try {
    		for(k=0;k<WAIT_TIME_MS/100;k++) {
    			Thread.sleep(100);
    			
    			float f =  WAIT_TIME_MS - k*100;
    			
    	    	if(null!=cbobj)
    	    		cbobj.DisplayText(String.format("等待%.1f秒", f/1000));
    		}
    			
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		//	收到2字节参数包确认应答
		//	5543 
		szData = m_commobj.RecvBlock(2);
		if(null==szData) {
	    	if(null!=cbobj)
	    		cbobj.DisplayText("收到2字节参数包确认应答 失败");
	    	return -1;
		}
		str0 = DataConverter.bytes2HexString(szData);
		if(!"5543".equals(str0)) {
	    	if(null!=cbobj)
	    		cbobj.DisplayText("未收到同步指令 4345");
	    	return -1;
		}
		
		int n,i,nPackNum = 0;
		szData = new byte[PACKAGE_SIZE];
		byte[] szHeader = new byte[2];
		boolean bLastPackage = false;
		
		if(nFizeSize%PACKAGE_SIZE==0) {
			nPackNum = nFizeSize/PACKAGE_SIZE;
		}			
		else {
			nPackNum = nFizeSize/PACKAGE_SIZE+1;
		}
		
		szHeader[0] = 0x44;szHeader[1]=0x44;
		
		n = 0;
		int readSize = -1; // 记录每次实际读取字节数
		
    	if(null!=cbobj) {
    		cbobj.DisplayText("开始下载...");
    		cbobj.StartProgressbar();
    	}
    		
		try {
			InputStream fis = new FileInputStream(strBINFileName);
			in = new BufferedInputStream(fis, PACKAGE_SIZE);
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		try {
			while (null != in && (readSize = in.read(szData)) != -1)
			{
				if(readSize<PACKAGE_SIZE) {
					for(i=readSize;i<PACKAGE_SIZE;i++) 
						szData[i]=(byte)0xff;	
					bLastPackage = true;
				}
				
				if(bLastPackage) {
					szHeader[0]=0x44;szHeader[1]=0x46;
				}

				// 发送
				m_commobj.FlushComm();				
				m_commobj.SendBlock(szHeader);
				m_commobj.SendBlock(szData);
				
				byte[] rsp = m_commobj.RecvBlock(2);
				if(null==rsp) {
					cbobj.DisplayText("未收到升级包应答");
					break;
				}
				
				String strresp = DataConverter.bytes2HexString(rsp);

				//0x57 55
				//0x46 55
				boolean brsp = false;
				if(!bLastPackage)
					brsp ="5557".equals(strresp);
				else
					brsp ="5546".equals(strresp);

				if(!brsp) {
					if(null!=cbobj)
			    		cbobj.DisplayText("包应答错误");
					break;
				}

				// 下一个包
				n++;
				if(null!=cbobj)
					cbobj.SetProgressbarValue((n*100)/nPackNum);
				
				if(bLastPackage) break;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//	核心板下电并重新上电
		if(null!=cbobj)
    		cbobj.DisplayText("�µ�");		
		cmd.BoardPowerOff();

		//	等待2秒
    	WAIT_TIME_MS = 2000;
    	try {
    		for(k=0;k<WAIT_TIME_MS/100;k++) {
    			Thread.sleep(100);
    			
    			float f =  WAIT_TIME_MS - k*100;
    			
    	    	if(null!=cbobj)
    	    		cbobj.DisplayText(String.format("等待%.1f秒", f/1000));
    		}
    			
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(null!=cbobj) {
			cbobj.DisplayText("上电");
			cbobj.DisplayText("升级完成！");
		}
    		
		cmd.BoardPowerOn();
		
		return 0;
	}

	/***
	 * 核心板初始化自检
	 * @return
	 */
		/***
	 * 核心板初始化自检
	 * @return
	 */
	public int MainBoardInitialize(InitialCheckCallBack cbobj) {
		Log.d(TAG, "MainBoardInitialize: ");
		int ret;
		
		DetCmd cmd = new DetCmd(m_commobj);
		StringBuilder strData = new StringBuilder();
		ret = cmd.BoardCmd80(strData);
		m_detError.Setter((byte)0x80, ret);
		Log.d(TAG, "MainBoardInitialize: ret = "+ret);
		if(0!=ret) return ret;
		
		//[0] //B0
		//[1] //固定值23，有效数据包DAT长度
		//[2~3] //硬件版本号，高字节在前，当前是v3.0.20
		//[4~5] //升级固件版本号，高字节在前，当前是v1.0.00
		//[6~7] //软件版本号，高字节在前，当前是v1.0.10
		//[8~11] //序列号
		//[12~15] //配置信息，应是02 XX XX XX
		//[16~19] //参数1，不显示，仅调试时使用
		//[20~23] //参数2，不显示，仅调试时使用
		//[24] //核心板电压检测结果，00:通过；其他:出错，直接报警
		//[25] //CRC8

		//	0210 1000 1013 00000000 02000012 00001F6E 0000340D 00 4A
		String str0 = strData.toString();
		
		String strHardwareVer = str0.substring(0,1)+"."+str0.substring(1,2)+"."+str0.substring(2,4);
		String strUpdateHardwareVer = str0.substring(4,5)+"."+str0.substring(5,6)+"."+str0.substring(6,8);
		String strSoftwareVer = str0.substring(8,9)+"."+str0.substring(9,10)+"."+str0.substring(10,12);
		String strSNO = str0.substring(12,20);
		String strConfig = str0.substring(20,28);
		
		byte bCheckResult = (byte)Byte.parseByte(str0.substring(44,46),16);
		
		if(null!=cbobj) {
			cbobj.SetInitialCheckData(strHardwareVer, strUpdateHardwareVer, 
					strSoftwareVer, 
					strSNO, 
					strConfig, 
					bCheckResult);
		}
				
		ret = bCheckResult;
		return ret;
	}
	

	/***
	 * 总线短路与漏电检测
	 * @return
	 */
	public int CheckBusShortCircuit() {
		int ret;
		
		DetCmd cmd = new DetCmd(m_commobj);
		StringBuilder strData = new StringBuilder();
		ret = cmd.BoardCmd81(strData);
	
		m_detError.Setter((byte)0x81, ret);
		
		return ret;
	}


	/***
	 * 关机流程
	 * @return
	 */
	public int ShutdownProc() {
		int ret;
		
		DetCmd cmd = new DetCmd(m_commobj);
		
		ret = this.MainBoardBusPowerOff();
		
		cmd.BoardPowerOff();
		Log.d(TAG, "ShutdownProc: ret = "+ret);
		
		return ret;
	}

	/***
	 * 单颗模组检测 单颗模组检测
	 * @param
	 * @return
	 */
	public int CheckSingleModule(DetCallback cbobj) {

		int ret;
		final int RESP_LEN = 0x19;
		final byte RESP_HEAD = (byte)0xb2;
		
		DetCmd cmd = new DetCmd(m_commobj);
		DetProtocol prt = new DetProtocol(m_commobj);
		DetResponse resp = new DetResponse();
		
		if(null!=cbobj) 
			cbobj.DisplayText("单颗模组检测 开始...");
		
		ret = cmd.BoardSendCmd82();
		if(0!=ret) return ret;
		
		if(null!=cbobj) 
			cbobj.StartProgressbar();
		
		while(true) {	
			ret = prt.RecvBlock(RESP_LEN, resp);
			if(0!=ret) {
				if(null!=cbobj) 
					cbobj.DisplayText("单颗模组检测 超时无应答");
				break;
			}
	
			//	B2 LEN 完成百分比[1] ID[4] DC[8] DT[4] 
			String str0 = resp.GetRespData();
			byte[] szdata = DataConverter.hexStringToBytes(str0);
			
			if(null==szdata) {
				if(null!=cbobj) 
					cbobj.DisplayText("单颗模组检测 获取无效数据");
				break;				
			}
			if(szdata.length<RESP_LEN-1) {
				if(null!=cbobj) 
					cbobj.DisplayText("单颗模组检测 获取数据长度不足");
				break;	
			}
						
			if(szdata[0]!=(byte)RESP_HEAD) {
				if(null!=cbobj) 
					cbobj.DisplayText("单颗模组检测 首字节无效");
				break;
			}
						
			ret = szdata[2];
			if(ret<0) ret = ret + 0x100;
			
			if(null!=cbobj) 
				cbobj.SetProgressbarValue(ret);
			
			if(ret<100)
				continue;

			//			B2 LEN 完成百分比[1] ID[4] DC[8] DT[4] 参数1[2] 参数2[2]  药头检测结果[1] CRC8[1]
			byte[] id = new byte[4];
			byte[] dc = new byte[8];
			byte[] dt = new byte[4];
			
			System.arraycopy(szdata, 3, id, 0, 4);
			System.arraycopy(szdata, 7, dc, 0, 8);
			System.arraycopy(szdata, 15, dt, 0, 4);
			
			byte bResult = szdata[23];
			
			//	ID
			int nid = DataConverter.bytes2Int(id);
			int ndt = DataConverter.bytes2Int(dt);
			
			if(ret>100){
				if(null!=cbobj) {
					cbobj.SetSingleModuleCheckData(nid, dc, ndt,bResult);
					cbobj.DisplayText("单颗模组检测 失败！");
				}				
				break;
			}
			
			ret = 0;	
			if(null!=cbobj) {
				cbobj.SetSingleModuleCheckData(nid, dc, ndt,bResult);
				cbobj.DisplayText("单颗模组检测 完成！");				
			}

			break;
		}
				
		return ret;

	}


	/***
	 * 总线上电与检测流程
	 * @param
	 * @return
	 */
	public int PowerOnSelfCheck(DetCallback cbobj) {
		int ret;
		final int RESP_LEN = 12;
		final byte RESP_HEAD = (byte)0xb5;
		Log.d(TAG, "PowerOnSelfCheck: ");
		
		DetCmd cmd = new DetCmd(m_commobj);
		DetProtocol prt = new DetProtocol(m_commobj);
		DetResponse resp = new DetResponse();

		
		if(null!=cbobj) 
			cbobj.DisplayText("总线上电与检测流程 开始...");

		Log.d(TAG, "PowerOnSelfCheck: 总线上电与检测流程 开始...");
		ret = cmd.BoardSendCmd85();
		Log.d(TAG, "PowerOnSelfCheck: ret = "+ ret);
		if(0!=ret) return ret;
		
		if(null!=cbobj) 
			cbobj.StartProgressbar();
		
		while(true) {	
			ret = prt.RecvBlock(RESP_LEN, resp);
			if(0!=ret) {
				if(null!=cbobj) 
					cbobj.DisplayText("总线上电与检测流程 超时无应答");
				break;
			}
	
			String str0 = resp.GetRespData();
			byte[] szdata = DataConverter.hexStringToBytes(str0);
			
			if(null==szdata) {
				if(null!=cbobj) 
					cbobj.DisplayText("总线上电与检测流程  获取无效数据");
				break;				
			}
			if(szdata.length<RESP_LEN-1) {
				if(null!=cbobj) 
					cbobj.DisplayText("总线上电与检测流程 获取数据长度不足");
				break;	
			}
						
			if(szdata[0]!=(byte)RESP_HEAD) {
				if(null!=cbobj) 
					cbobj.DisplayText("总线上电与检测流程 首数据无效");
				break;
			}
						
			if(null!=cbobj) 
				cbobj.SetProgressbarValue(szdata[2]);
			
			if(szdata[2]>=0x64) {
				cbobj.DisplayText("总线上电与检测流程 完成！");
				break;
			}			
		}
				
		return ret;
	}
	
	
	

	public void testDetAPP() {
		String strErrMsg ="";

		//	ECHO测试
		MainBoardEcho();
		strErrMsg = GetErrorMessage();

		//	获取主控板版本
		strErrMsg = MainBoardGetVersion();
		if(strErrMsg.length()==0)
			strErrMsg = GetErrorMessage();

		//	核心板初始化自检
		int ret = MainBoardInitialize(null);
		if(ret!=0) {
			System.out.println(String.format("核心板初始化自检 失败 %d", ret));
		}
		
		ret = CheckBusShortCircuit();
		if(ret!=0) {
			System.out.println(String.format("总线短路与漏电检测 失败 %d", ret));
		}

//		//	单颗模组检测
//		DetCallback cbobj = new DetCallback();
//		ret = CheckSingleModule(cbobj);
//		if(ret!=0) {
//			System.out.println(String.format("单颗模组检测 失败 %d", ret));
//		}
//
//		//	总线上电与检测流程
//		ret = PowerOnSelfCheck(cbobj);
//		if(ret!=0) {
//			System.out.println(String.format("总线上电与检测流程 失败 %d", ret));
//		}
		
		
		return;
	}

	/***
	 * 功能测试
	 */
	public void testProc() {
		// TODO Auto-generated method stub
		int ret = Initialize();
		if(0!=ret) {
			System.out.println("串口打开失败!");
			return;
		}
		
		testDetAPP();
		
		testDownloadProc();
		
		Finalize();
		
		return;
	}


	/***
	 * 主控板程序升级过程测试
	 */
	private void testDownloadProc() {

		String strfile ="E:\\Kanbox\\Android\\Detocator\\ref\\CoreBrd1768_Std_v1.0.13.bin";
		
		DetApp detapp = new DetApp();
		detapp.Initialize();
		
//		DetCallback cbobj = new DetCallback();
//
//		detapp.DownloadProc(strfile, cbobj);
		return;
	}
}
