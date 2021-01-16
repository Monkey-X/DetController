/*
 * <p> 核心板通信应用类  </p>
 * <p> 主要根据安卓APP的调用实现指令封装</p>
 * <p> 创建时间： ${date}</p>
 * <p> @author Xin Hongwei</p>
 * <p> @version 1.00</p>
 * */

package com.etek.controller.hardware.command;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


import com.etek.controller.hardware.comm.SerialCommBase;
import com.etek.controller.hardware.test.BusChargeCallback;
import com.etek.controller.hardware.test.InitialCheckCallBack;
import com.etek.controller.hardware.test.PowerCheckCallBack;
import com.etek.controller.hardware.test.SingleCheckCallBack;
import com.etek.controller.hardware.util.DetIDConverter;
import com.szyd.jni.HandSetSerialComm;
import com.etek.controller.hardware.test.DetCallback;
import com.etek.controller.hardware.util.DataConverter;


public class DetApp {
	private static final int MAX_TRY = 3;
	private SerialCommBase m_commobj;
	private DetCmd m_cmdObj;
	private DetErrorCode m_detError;

	private String TAG = "DetApp";

	//	起爆器厂商编码
	private static byte m_bMID = 99;

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
		System.out.println("Initialize....");

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

		if(null!=m_detError) {
			m_detError.Setter((byte)0x41, ret);
		}

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
		int ret =0;

		for (int i = 0; i < MAX_TRY; i++) {

			ret = m_cmdObj.ModCmd55(nID,nDT);

			m_detError.Setter((byte)0x55, ret);
			if (ret ==0) {
				break;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
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
		int ret = 0;

		for(int i=0;i<MAX_TRY;i++){
			ret = m_cmdObj.ModCmd5E(nID);

			m_detError.Setter((byte)0x5E, ret);
			if (ret ==0){
				break;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	/***
	 * 雷管网络起爆
	 * @return
	 */
	public int ModuleDetonate(int nID) {
	    int ret;
	    ret = m_cmdObj.BoardCmd8F();
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
	 * 獲取雷管的UID
	 * @param nID
	 * @param strUID
	 * @return
	 */
	public int ModuleGetUID(int nID,StringBuilder strUID) {
		StringBuilder struidh = new StringBuilder();
		int ret = m_cmdObj.ModCmd64(nID, struidh);
		if(0!=ret) return ret;

		String struid = struidh.substring(0, 6)+String.format("%08X", nID);

		strUID.setLength(0);
		strUID.append(struid);
		return 0;
	}

	/***
	 * 下载主控板程序流程
	 * @param strBINFileName
	 * @return
	 */
	public int DownloadProc(String strBINFileName, DetCallback cbobj) {
        Log.d(TAG, "DownloadProc: ");
		int ret;
		int nFizeSize;
		BufferedInputStream in=null;
		int WAIT_TIME_MS,k;

		final int PACKAGE_SIZE = 1024;

		//	发送6字节同步指令应答
		final byte[] CMD_SYN_RESPONSE = {0x42,0x65,0x6c,0x6c,0x65,0x20};

		DetCmd cmd = new DetCmd(m_commobj);

		//	核心板5V供电
		Log.d(TAG, String.format("核心板5V供电"));
		ret = cmd.BoardPowerOn();
		if(0!=ret) {
			if(null!=cbobj)
				cbobj.DisplayText("核心板5V供电失败!");

			Log.d(TAG, String.format("核心板5V供电失败!"));
			return -1;
		}

		//	改成绝对路径
		String strFilePath = Environment.getExternalStorageDirectory().getPath()+strBINFileName;
		Log.d(TAG, String.format("文件路径：%s",strFilePath));
        File file = new File(strFilePath);
        if (!file.exists() || !file.isFile()) {
        	if(null!=cbobj)
        		cbobj.DisplayText("文件不存在");

			Log.d(TAG, String.format("文件不存在"));
            return -1;
        }
        nFizeSize = (int) file.length();


		//	将BL引脚拉低
		Log.d(TAG, String.format("BL电平拉低"));
		ret = cmd.BoardSetBL(false);
		if(0!=ret) {
			if(null!=cbobj)
				cbobj.DisplayText("BL电平拉低失败!");

			Log.d(TAG, String.format("BL电平拉低失败!"));
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

		m_commobj.FlushComm();

		//	将BL脚置高（此时核心板进入BL状态）
		Log.d(TAG, String.format("BL脚置高"));
		ret = cmd.BoardSetBL(true);
		if(0!=ret) {
        	if(null!=cbobj)
        		cbobj.DisplayText("BL脚置高失败!");

			Log.d(TAG, String.format("BL脚置高失败!"));
            return -1;
		}

		//	监控UART口,收到6字节同步指令？
		//	47 61 73 74 6F 6E
		m_commobj.SetTimeout(10000);
		ret = m_commobj.WaitTimeout();
		System.out.println("监控UART口,ret:"+ret);

		if(0!=ret) {
	    	if(null!=cbobj)
	    		cbobj.DisplayText("未收到同步指令");

			Log.d(TAG, String.format("未收到同步指令0"));
		}
		m_commobj.SetTimeout(5000);

		byte[] szData = m_commobj.RecvBlock(6);
		if(null==szData) {
	    	if(null!=cbobj)
	    		cbobj.DisplayText("未收到同步指令");

			Log.d(TAG, String.format("未收到同步指令1"));
	    	return -1;
		}
		String str0 = DataConverter.bytes2HexString(szData);
		System.out.println("收到的指令："+str0);
		if(!"476173746f6e".equals(str0)) {
	    	if(null!=cbobj)
	    		cbobj.DisplayText("未收到同步指令 476173746f6e");

			Log.d(TAG, String.format("未收到同步指令 476173746f6e"));
	    	return -1;
		}


		//	发送6字节应答指令
		ret = m_commobj.SendBlock(CMD_SYN_RESPONSE);
		if(0!=ret) {
        	if(null!=cbobj)
        		cbobj.DisplayText("发送同步指令应答 失败!");

			Log.d(TAG, String.format("发送同步指令应答 失败!"));
            return -1;
		}

		while(true) {
			ret = m_commobj.WaitTimeout();
			if(0!=ret) {
		    	if(null!=cbobj)
		    		cbobj.DisplayText("未收到 发送6字节应答指令 应答");

				Log.d(TAG, String.format("未收到 发送6字节应答指令 应答"));
		    	return -1;
			}

			byte[] data = m_commobj.RecvBlock(1);
			if(null==data){
				if(null!=cbobj)
					cbobj.DisplayText("6字节应答指令 为空");

				Log.d(TAG, String.format("6字节应答指令 为空"));
				return -1;
			}

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
		Log.d(TAG, String.format("开始收取确认指令..."));
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
//    	try {
//    		for(k=0;k<WAIT_TIME_MS/100;k++) {
//    			Thread.sleep(100);
//
//    			float f =  WAIT_TIME_MS - k*100;
//
//    	    	if(null!=cbobj)
//    	    		cbobj.DisplayText(String.format("等待%.1f秒", f/1000));
//    		}
//
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}

		//	收到2字节参数包确认应答
		//	5543
		Log.d(TAG, String.format("开始收2字节参数包确认应答..."));
		m_commobj.SetTimeout(WAIT_TIME_MS);
		szData = m_commobj.RecvBlock(2);
		if(null==szData) {
	    	if(null!=cbobj)
	    		cbobj.DisplayText("收到2字节参数包确认应答 失败");

			Log.d(TAG, String.format("收到2字节参数包确认应答 失败"));
	    	return -1;
		}
		str0 = DataConverter.bytes2HexString(szData);
		if(!"5543".equals(str0)) {
	    	if(null!=cbobj)
	    		cbobj.DisplayText("未收到同步指令 5543");

			Log.d(TAG, String.format("未收到同步指令 5543"));
	    	return -1;
		}

		/*	*/

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

		Log.d(TAG, String.format("开始下载..."));

		try {
			FileInputStream fis = new FileInputStream(strFilePath);
			in = new BufferedInputStream(fis, PACKAGE_SIZE);
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			cbobj.DisplayText("打开主控板程序失败");
			return -1;
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
		Log.d(TAG, String.format("下载完成"));

		try {
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//	核心板下电并重新上电
		if(null!=cbobj)
    		cbobj.DisplayText("主控板下电");
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
	public int MainBoardInitialize(InitialCheckCallBack cbobj) {
		Log.d(TAG, "MainBoardInitialize: ");
		int ret;

		//	超时等待（此时核心板需要做擦除动作）
		long WAIT_TIME_MS = 2000;

			for(int k=0;k<WAIT_TIME_MS/100;k++) {

				try {
					Thread.sleep(100);
				}catch (Exception e){
					e.printStackTrace();
				}
				float f =  WAIT_TIME_MS - k*100;

//				if(null!=cbobj)
//					cbobj.DisplayText(String.format("等待%.1f秒", f/1000));
			}
		DetCmd cmd = new DetCmd(m_commobj);
		StringBuilder strData = new StringBuilder();
		ret = cmd.BoardCmd80(strData);
		m_detError.Setter((byte)0x80, ret);
		Log.d(TAG, "MainBoardInitialize: ret = "+ret);
		if(0!=ret) return ret;

		//[0] //B0
		//[1] //固定值21，有效数据包DAT长度
		//[2~3] //硬件版本号，高字节在前，当前是v3.0.20
		//[4~5] //升级固件版本号，高字节在前，当前是v1.0.00
		//[6~7] //软件版本号，高字节在前，当前是v1.0.10
		//[8~13] //设备序列号，格式为“FXXA8ZZZZZZ”，如：“F61A8123456”
		//		其中[8]为固定字节0x46，为‘F’的ASCII
		//				[9]为厂家代码，可以参与APP的厂家一致性判断；
		//      [10]为固定值A8；
		//      [11~13]为其他字节
		//		显示是”%c%02X%02X%02X%02X%02X”
		//[14~17] //参数1，不显示，仅调试时使用
		//[18~21] //参数2，不显示，仅调试时使用
		//[22] //核心板电压检测结果，00:通过；其他:出错，直接报警
		//[23] //CRC8

		//	3020 10F0 101A 460061200002 00001F23 000033B2 00
		String str0 = strData.toString();
		//[2~3] //硬件版本号
		String strHardwareVer = str0.substring(0,1)+"."+str0.substring(1,2)+"."+str0.substring(2,4);
		//[4~5] //升级固件版本号
		String strUpdateHardwareVer = str0.substring(4,5)+"."+str0.substring(5,6)+"."+str0.substring(6,8);
		//[6~7] //软件版本号
		String strSoftwareVer = str0.substring(8,9)+"."+str0.substring(9,10)+"."+str0.substring(10,12);
		//[8~13] //设备序列号
		String strSNO = str0.substring(12,24);
		byte[] arrdata = DataConverter.hexStringToBytes(strSNO);
		strSNO = String.format("%c%02X%02X%02X%02X%02X",
				arrdata[0],
				arrdata[1],arrdata[2],arrdata[3],arrdata[4],arrdata[5]);

		//	设置MID
		m_bMID = DataConverter.bcd2Hex(arrdata[1]);
		m_bMID =99;
		DetIDConverter.SetMID(m_bMID);

		//
		String strConfig = "";

		byte bCheckResult = (byte)Byte.parseByte(str0.substring(40,42),16);

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
	public int CheckBusShortCircuit(StringBuilder strData) {
		int ret=0;
		int i;

		DetCmd cmd = new DetCmd(m_commobj);

		for(i=0;i<MAX_TRY;i++){
			ret = cmd.BoardCmd81(strData);
			if(0==ret)
				break;

			try {
				Thread.sleep(100);
			}catch (Exception e){
				e.printStackTrace();
			}
		}
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

//		ret = this.MainBoardBusPowerOff();

		cmd.BoardPowerOff();
		Log.d(TAG, "ShutdownProc");
//		Log.d(TAG, "ShutdownProc: ret = "+ret);

		return 0;
	}

	/***
	 * 单颗模组检测 单颗模组检测
	 * @param
	 * @return
	 */
	public int CheckSingleModule(SingleCheckCallBack cbobj) {

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

		while(true) {
			ret = prt.RecvBlock(RESP_LEN, resp);
			if(0!=ret) {
				if(null!=cbobj)
					cbobj.DisplayText("单颗模组检测 超时无应答");
				break;
			}

			//	B2 LEN 完成百分比[1] ID[4] DC[8] DT[4]
			String str0 = resp.GetRespData();

			System.out.println("GetRespData:"+str0);

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

			if(ret<100) {
				if(null!=cbobj)
					cbobj.SetProgressbarValue(ret);
				continue;
			}


			if(ret>100)
				break;

			//			B2 LEN 完成百分比[1] ID[4] DC[8] DT[4] 参数1[2] 参数2[2]  药头检测结果[1] CRC8[1]
			byte[] id = new byte[4];
			byte[] dc = new byte[8];
			byte[] dt = new byte[4];

			System.arraycopy(szdata, 3, id, 0, 4);
			System.arraycopy(szdata, 7, dc, 0, 8);
			System.arraycopy(szdata, 15, dt, 0, 4);

			byte bResult = szdata[23];

			//	ID
			int nid = DataConverter.lsbBytes2Int(id);
			int ndt = DataConverter.lsbBytes2Int(dt);

			//	比较起爆器厂商编码
			if(99!=m_bMID){
				if(dc[0]!=m_bMID){
					cbobj.DisplayText("单颗模组检测 和起爆器编码不一致！");
					return -1;
				}
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
	 *
	 * @return
	 */
	public int MainBoardLineCheckStart(){
		int ret;

		DetCmd cmd = new DetCmd(m_commobj);

		Log.d(TAG, "MainBoardLineCheckStart: 总线检测开始");
		ret = cmd.BoardSendCmd83();
		return ret;
	}

	/***
	 *
	 * @param strdata, 电压电流值
	 * @return
	 * 	0	成功，展示电流电压值
	 * 	1	出错	展示strdata返回的信息
	 * 	2	退出	检测完毕
	 */
	public int MainBoardLineCheckGetValue(StringBuilder strdata){
		int ret;
		final int RESP_LEN = 13;
		final byte RESP_HEAD = (byte)0xb3;

		strdata.setLength(0);
		Log.d(TAG, "MainBoardLineCheckGetValue: ");

		DetProtocol prt = new DetProtocol(m_commobj);

		DetResponse resp = new DetResponse();
		ret = prt.RecvBlock(RESP_LEN, resp);
		if(0!=ret) {
			strdata.append("没有获取到数据!");
			return 1;
		}

		String str0 = resp.GetRespData();
		byte[] szdata = DataConverter.hexStringToBytes(str0);

		if(null==szdata) {
			strdata.append("数据无效!");
			return 1;
		}
		if(szdata.length<RESP_LEN-1) {
			strdata.append("数据长度不足!");
			return 1;
		}

		if(szdata[0]!=(byte)RESP_HEAD) {
			strdata.append("首字节无效B3!");
			return 1;
		}
		//[11]检测结果，  00:未检测；  01:通过； 0A:总线漏电； 0F:总线短路；
		ret = DataConverter.getByteValue(szdata[11]) ;
		if(0x0a==ret){
			strdata.append("总线漏电");
			return 1;
		}
		if(0x0f==ret){
			strdata.append("总线短路");
			return 1;
		}

		//[2] 正在测试或已结束， 0x00:正在测试中； 0x64:用户正常取消； 0xC8:硬件短路，终止流程
		ret = DataConverter.getByteValue(szdata[2]) ;
		if(ret==0x00) {
			strdata.append(str0.substring(6,22));
			return 0;
		}

		if(0x64==ret){
			strdata.append("用户取消");
			return 2;
		}

		if(0xC8==ret){
			strdata.append("硬件短路");
			return 2;
		}

		return 0;
	}

	/***
	 * 总线上电与检测流程（也叫：总线充电）
	 * @param
	 * @return
	 */
	public int PowerOnSelfCheck(PowerCheckCallBack cbobj) {
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

			ret = szdata[2];
			if(ret<0) ret = ret +0x100;

			if(ret==0x64) {
				ret= 0;
				cbobj.DisplayText("总线上电与检测流程 完成！");
				break;
			}

			if(ret<0x64) {
				cbobj.SetProgressbarValue(ret);
				continue;
			}

			cbobj.DisplayText("总线上电与检测流 出错");
			return -1;
		}
		return 0;
	}

	/***
	 * 批量雷管网络连接检测（最多10个）
	 * @param nIDs
	 * @param cbobj
	 * @return
	 */
	public int DetsCheckLinkage(int[] nIDs,DetCallback cbobj) {
		int ret;
		final int RESP_LEN = 14;
		final byte RESP_HEAD = (byte)0xb8;
		byte[] szdata  = null;


		DetCmd cmd = new DetCmd(m_commobj);
		DetProtocol prt = new DetProtocol(m_commobj);
		DetResponse resp = new DetResponse();

		int[] arrIDs = new int[10];
		int nDetNum = nIDs.length;
		if(nDetNum>=10)
			nDetNum = 10;
		System.arraycopy(nIDs, 0,arrIDs, 0, nDetNum);

		ret = cmd.BoardSendCmd88(arrIDs);
		m_detError.Setter((byte)0x88, ret);
		if(0!=ret) return ret;

		//[0] //B8
		//[1] //固定值0B，有效数据包DAT长度

		//[2] //完成百分比
		//[3~12 //10字节状态，表示10个雷管的连接测试状态：00：未测试，01：通过；0F：错误
		//[13] //CRC8
		if(null!=cbobj)
			cbobj.StartProgressbar();

		while(true) {
			ret = prt.RecvBlock(RESP_LEN, resp);
			if(0!=ret) {
				if(null!=cbobj)
					cbobj.DisplayText("雷管网络连接检测 超时无应答");
				break;
			}

			String str0 = resp.GetRespData();
			szdata = DataConverter.hexStringToBytes(str0);

			if(null==szdata) {
				if(null!=cbobj)
					cbobj.DisplayText("雷管网络连接检测  获取无效数据");
				break;
			}
			if(szdata.length<RESP_LEN-1) {
				if(null!=cbobj)
					cbobj.DisplayText("雷管网络连接检测 获取数据长度不足");
				break;
			}

			if(szdata[0]!=(byte)RESP_HEAD) {
				if(null!=cbobj)
					cbobj.DisplayText("雷管网络连接检测 首数据无效");
				break;
			}

			ret = szdata[2];
			if(ret<0) ret = ret +0x100;

			if(ret==0x64) {
				if(null!=cbobj)
					cbobj.DisplayText("总雷管网络连接检测 完成！");
				break;
			}

			if(ret<0x64) {
				if(null!=cbobj)
					cbobj.SetProgressbarValue(ret);
				continue;
			}

			if(null!=cbobj)
				cbobj.DisplayText("雷管网络连接检测出错");
			return -1;
		}


		for(int n=0;n<nDetNum;n++) {
			if(null!=cbobj)
				cbobj.SetDetsSettingResult(nIDs[n], szdata[3+n]);
		}
		return 0;

	}

	/***
	 * 批量设置雷管延时，每组最多5个
	 * @param nIDs
	 * @param nDTs
	 * @param cbobj
	 * @return
	 */
	public int DetsSetDelayTime(int[] nIDs,int[] nDTs,DetCallback cbobj) {
		int ret;
		final int RESP_LEN = 9;
		final byte RESP_HEAD = (byte)0xb9;
		byte[] szdata  = null;

		DetCmd cmd = new DetCmd(m_commobj);
		DetProtocol prt = new DetProtocol(m_commobj);
		DetResponse resp = new DetResponse();

		int[] arrIDs = new int[5];

		int nDetNum = nIDs.length;
		if(nDetNum>=5)
			nDetNum = 5;
		System.arraycopy(nIDs, 0,arrIDs, 0, nDetNum);
		int[] arrDTs = new int[5];
		System.arraycopy(nDTs, 0, arrDTs, 0, nDetNum);
		ret = cmd.BoardSendCmd89(arrIDs,arrDTs);
		m_detError.Setter((byte)0x89, ret);
		if(0!=ret) return ret;

		//[0] //B9
		//[1] //固定值06，有效数据包DAT长度
		//[2] //完成百分比
		//[3~7 //5字节状态，表示5个雷管的延时下载状态：00：未测试，01：成功；0F：错误
		//[8] //CRC8

		if(null!=cbobj)
			cbobj.StartProgressbar();

		while(true) {
			ret = prt.RecvBlock(RESP_LEN, resp);
			if(0!=ret) {
				if(null!=cbobj)
					cbobj.DisplayText("雷管网络延时下载 超时无应答");
				break;
			}

			String str0 = resp.GetRespData();
			szdata = DataConverter.hexStringToBytes(str0);

			if(null==szdata) {
				if(null!=cbobj)
					cbobj.DisplayText("雷管网络延时下载  获取无效数据");
				break;
			}
			if(szdata.length<RESP_LEN-1) {
				if(null!=cbobj)
					cbobj.DisplayText("雷管网络延时下载 获取数据长度不足");
				break;
			}

			if(szdata[0]!=(byte)RESP_HEAD) {
				if(null!=cbobj)
					cbobj.DisplayText("雷管网络延时下载 首数据无效");
				break;
			}

			ret = szdata[2];
			if(ret<0) ret = ret +0x100;

			if(ret==0x64) {
				if(null!=cbobj)
					cbobj.DisplayText("雷管网络延时下载 完成！");
				break;
			}

			if(ret<0x64) {
				if(null!=cbobj)
					cbobj.SetProgressbarValue(ret);
				continue;
			}

			if(null!=cbobj)
				cbobj.DisplayText("雷管网络延时下载出错");
			return -1;
		}


		for(int n=0;n<nDetNum;n++) {
			if(null!=cbobj)
				cbobj.SetDetsSettingResult(nIDs[n], szdata[3+n]);
		}
		return 0;
	}

	/***
	 * 雷管网络总线充电（也叫：雷管充电）
	 * @param cbobj
	 * @return
	 */
	public int DetsBusCharge(BusChargeCallback cbobj) {
		int ret;
		final int RESP_LEN = 12;
		final byte RESP_HEAD = (byte)0xbc;
		byte[] szdata  = null;

		DetCmd cmd = new DetCmd(m_commobj);
		DetProtocol prt = new DetProtocol(m_commobj);
		DetResponse resp = new DetResponse();

		ret = cmd.BoardSendCmd8C();
		m_detError.Setter((byte)0x8C, ret);
		if(0!=ret) return ret;

		//[0] //BC
		//[1] //固定值09，有效数据包DAT长度
		//[2] //完成百分比，正确流程为0~100，表示正在进行中
		//                检测到错误时，此值为>100的值，检测终止
		//[3~6] //总线电压值，实时显示
		//[7~10 //总线电流值，实时显示
		//[11] //CRC8

		while(true) {
			ret = prt.RecvBlock(RESP_LEN, resp);
			if(0!=ret) {
				if(null!=cbobj)
					cbobj.DisplayText("雷管网络总线充电 超时无应答");
				break;
			}

			String str0 = resp.GetRespData();
			szdata = DataConverter.hexStringToBytes(str0);

			if(null==szdata) {
				if(null!=cbobj)
					cbobj.DisplayText("雷管网络总线充电  获取无效数据");
				ret = -1;
				break;
			}
			if(szdata.length<RESP_LEN-1) {
				if(null!=cbobj)
					cbobj.DisplayText("雷管网络总线充电 获取数据长度不足");
				ret = -1;
				break;
			}

			if(szdata[0]!=(byte)RESP_HEAD) {
				if(null!=cbobj)
					cbobj.DisplayText("雷管网络总线充电 首数据无效");
				ret = -1;
				break;
			}

			ret = szdata[2];
			if(ret<0) ret = ret +0x100;

			byte[] vt = new byte[4];
			byte[] cr = new byte[4];

			System.arraycopy(szdata, 3, vt, 0, 4);
			System.arraycopy(szdata, 7, cr, 0, 4);

			int nVoltage = DataConverter.bytes2Int(vt);
			int nCurrent = DataConverter.bytes2Int(cr);

			if(null!=cbobj)
				cbobj.setChargeData(nVoltage, nCurrent);

			if(ret==0x64) {
				if(null!=cbobj)
					cbobj.DisplayText("雷管网络总线充电 完成！");
				ret = 0;
				break;
			}

			if(ret<0x64) {
				if(null!=cbobj)
					cbobj.SetProgressbarValue(ret);
				continue;
			}

			if(null!=cbobj)
				cbobj.DisplayText("雷管网络总线充电 出错");
			return -1;
		}

		return 0;
	}

	/***
	 * 雷管网络总线放电（雷管放电）
	 * @param cbobj
	 * @return
	 */
	public int DetsBusDischarge(BusChargeCallback cbobj) {
		int ret;
		final int RESP_LEN = 12;
		final byte RESP_HEAD = (byte)0xbd;
		byte[] szdata  = null;

		DetCmd cmd = new DetCmd(m_commobj);
		DetProtocol prt = new DetProtocol(m_commobj);
		DetResponse resp = new DetResponse();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		m_commobj.FlushComm();

		ret = cmd.BoardSendCmd8D();
		m_detError.Setter((byte)0x8D, ret);
		if(0!=ret) return ret;

		//[0] //BC
		//[1] //固定值09，有效数据包DAT长度
		//[2] //完成百分比，正确流程为0~100，表示正在进行中
		//                检测到错误时，此值为>100的值，检测终止
		//[3~6] //总线电压值，实时显示
		//[7~10 //总线电流值，实时显示
		//[11] //CRC8

		while(true) {
			ret = prt.RecvBlock(RESP_LEN, resp);
			if(0!=ret) {
				if(null!=cbobj)
					cbobj.DisplayText("雷管网络总线放电 结束1");
				break;
			}

			String str0 = resp.GetRespData();
			szdata = DataConverter.hexStringToBytes(str0);

			if(null==szdata) {
				if(null!=cbobj)
					cbobj.DisplayText("雷管网络总线放电 结束2");
				break;
			}
			if(szdata.length<RESP_LEN-1) {
				if(null!=cbobj)
					cbobj.DisplayText("雷管网络总线放电 结束3");
				break;
			}

			if(szdata[0]!=(byte)RESP_HEAD) {
				if(null!=cbobj)
					cbobj.DisplayText("雷管网络总线放电 结束4");
				break;
			}

			ret = szdata[2];
			if(ret<0) ret = ret +0x100;

			byte[] vt = new byte[4];
			byte[] cr = new byte[4];

			System.arraycopy(szdata, 3, vt, 0, 4);
			System.arraycopy(szdata, 7, cr, 0, 4);

			int nVoltage = DataConverter.bytes2Int(vt);
			int nCurrent = DataConverter.bytes2Int(cr);


			if(null!=cbobj)
				cbobj.setChargeData(nVoltage, nCurrent);

			if(ret==0x64) {
				if(null!=cbobj)
					cbobj.DisplayText("雷管网络总线放电 完成！");
				break;
			}

			if(ret<0x64) {
				if(null!=cbobj)
					cbobj.SetProgressbarValue(ret);
				continue;
			}

			if(null!=cbobj)
				cbobj.DisplayText("雷管网络总线放电 结束5");
			return -1;
		}

		return 0;
	}

	/*
	[0] 	BE
	[1] 	固定值05，有效数据包DAT长度
	[2~5] 	总线电压值，实时显示
	[6 		0x01：正常，等待起爆指令；0x0B：总线能量输出不足；0x0C：总线雷管模组脱落，开路
	[7] 	CRC8
	*/
	public int DetsCheckDropOff(StringBuilder strData) {
		int ret;

		DetCmd cmd = new DetCmd(m_commobj);
		ret = cmd.BoardCmd8E(strData);
		m_detError.Setter((byte)0x8E, ret);
		if(0!=ret) return ret;

		return ret;
	}

	/***
	 * 读取单颗雷管的ID和管码（不需要单独上下电操作，完成后总线没有电）
	 * @param strid:		雷管ID
	 * @param strDC			雷管的管码，是可视管码
	 * @param cbobj			回调信息
	 * @return
	 */
	public int DetsGetIDAndDC(StringBuilder strid,StringBuilder strDC,PowerCheckCallBack cbobj){
		int ret;
		final int RESP_LEN = 17;
		final byte RESP_HEAD = (byte)0xb4;
		byte[] szdata  = null;

		DetCmd cmd = new DetCmd(m_commobj);
		DetProtocol prt = new DetProtocol(m_commobj);
		DetResponse resp = new DetResponse();

		ret = cmd.BoardSendCmd84();
		m_detError.Setter((byte)0x84, ret);
		if(0!=ret) return ret;

		//[0] //B4
		//[1] //固定值14，有效数据包DAT长度
		//[2] //完成百分比，正确流程为0~100，表示正在进行中
		//		检测到错误时，此值为>100的值，检测终止
		//				[3~6] //ID[0~3]，ID，未检测时为00 00 00 00（不显示，仅验算时使用）
		//[7~14] //DC[0~7]，管码，未检测时为00 00 00 00 30 00 00 00
		//[15] //读取结果，00:尚未读取；01:通过；0A:ID读取错误；0B:管码读取错误；0F:短路；
		//[16] //CRC8

		while(true) {
			ret = prt.RecvBlock(RESP_LEN, resp);
			if(0!=ret) {
				if(null!=cbobj)
					cbobj.DisplayText("读取单颗雷管的ID和管码 超时无应答");
				break;
			}

			String str0 = resp.GetRespData();
			szdata = DataConverter.hexStringToBytes(str0);

			if(null==szdata) {
				if(null!=cbobj)
					cbobj.DisplayText("读取单颗雷管的ID和管码  获取无效数据");
				break;
			}
			if(szdata.length<RESP_LEN-1) {
				if(null!=cbobj)
					cbobj.DisplayText("读取单颗雷管的ID和管码 获取数据长度不足");
				break;
			}

			if(szdata[0]!=(byte)RESP_HEAD) {
				if(null!=cbobj)
					cbobj.DisplayText("读取单颗雷管的ID和管码 首数据无效");
				break;
			}

			ret = szdata[2];
			if(ret<0) ret = ret +0x100;

			if(ret<0x64) {
				if(null!=cbobj)
					cbobj.SetProgressbarValue(ret);
				continue;
			}

			if(ret>100){
				String strerrmsg =new String();

				Log.d(TAG, "DetsGetIDAndDC: ret ="+ret);
				switch(ret){
					case 110:
					case 120:
						strerrmsg="总线短路";
						break;
					case 150:
						//	ID读取失败，提示：“雷管读取失败”
						strerrmsg="雷管读取失败";
						break;
					case 180:
						//	管码读取失败，提示：“管码读取失败”
						strerrmsg="雷管读取失败";
						break;
					default:
						strerrmsg="读取单颗雷管的ID和管码 出错";
						break;
				}
				if(null!=cbobj)
					cbobj.DisplayText(strerrmsg);
				return -1;
			}

			byte[] id = new byte[4];
			byte[] dc = new byte[8];

			System.arraycopy(szdata, 3, id, 0, 4);
			System.arraycopy(szdata, 7, dc, 0, 8);

			ret = DataConverter.lsbBytes2Int(id);
			strid.append(String.valueOf(ret));

			DetIDConverter ic = new DetIDConverter();
			String strdc = ic.GetDisplayDC(dc);
			strDC.append(strdc);

			if(null!=cbobj)
				cbobj.DisplayText("读取单颗雷管的ID和管码 完成！");
			break;
		}

		return 0;
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

		StringBuilder strData = new StringBuilder();
		ret = CheckBusShortCircuit(strData);
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
