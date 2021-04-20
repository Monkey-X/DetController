package com.etek.controller.hardware.command;/*
 * <p> 核心板通信命令类  </p>
 * <p> 主要实现按照协议格式发送和接收命令</p>
 * <p> 创建时间： ${date}</p>
 * <p> @author Xin Hongwei</p>
 * <p> @version 1.00</p>
 * */


import android.util.Log;

import com.etek.controller.hardware.comm.SerialCommBase;
import com.szyd.jni.HandSetSerialComm;
import com.etek.controller.hardware.util.DataConverter;

public class DetCmd {
	private SerialCommBase m_commobj;
	
	private String TAG = "DetCmd";
	public DetCmd(SerialCommBase commobj) {
		m_commobj = commobj;
		Log.d(TAG, "DetCmd: ");
	}

	/*
	 * 无参数，无返回数据的命令执行
	 * bcmd:	命令
	 * */
	private int BoardCmd(byte bcmd) {
		byte[] szcmd = new byte[2];
		szcmd[0]=bcmd;szcmd[1]=0x00;

		DetProtocol prt = new DetProtocol(m_commobj);

		DetResponse resp=new DetResponse(0xff,"未设定");

		int ret = prt.SendRecv(bcmd,null, 0x00, resp);
		if(0!=ret) return ret;

		int bRSP = resp.GetRSP();
		if(bRSP==0xA0) return 0;
		return bRSP;
	}

	/*
	 *  无参数，有返回数据的命令执行
	 * bcmd:	命令
	 * nLen:	期望恢复的数据长度（不含CMD，LEN和CRC8
	 * nRSP:	正确的RSP
	 * strResp:	应答数据
	 * */
	private int BoardCmd(byte bcmd,int nLen,
						 int nRSP,StringBuilder strResp) {
		return BoardCmd(bcmd,null,nLen,nRSP,strResp);
	}

	/*
	 *  有参数，有返回数据的命令执行
	 * bcmd:	命令
	 * szParam:	命令参数
	 * nLen:	期望恢复的数据长度（不含CMD，LEN和CRC8
	 * nRSP:	正确的RSP
	 * strResp:	应答数据
	 * */
	private int BoardCmd(byte bcmd,byte[] szParam,
						 int nLen,
						 int nRSP,StringBuilder strResp) {

		int n = 0;
		if(null!=szParam) n =szParam.length;

		byte[] szcmd = new byte[n+2];
		szcmd[0]=bcmd;szcmd[1]=(byte)(n);

		DetProtocol prt = new DetProtocol(m_commobj);

		DetResponse resp=new DetResponse(0xff,"未设定");

		int ret = prt.SendRecv(bcmd,szParam, nLen, resp);
		if(0!=ret) return ret;

		int bRSP = resp.GetRSP();
		if(bRSP==nRSP)
		{
			strResp.setLength(0);
			strResp.append(resp.GetRespData());
			return 0;
		}
		return bRSP;
	}

	//////////////////////////////////////////////////////////////////////////////////////////
	//1、核心板 ECHO测试
	public int BoardCmd30() {
		byte bcmd = 0x30;
		return BoardCmd(bcmd);
	}
	//	2. 获取版本信息
	public int BoardCmd31(StringBuilder strVer) {
		byte bcmd = 0x31;
		return BoardCmd(bcmd,6,0xa1,strVer);
	}
	//	3. 获取序列号
	public int BoardCmd32(StringBuilder strSNO) {
		byte bcmd = 0x32;
		return BoardCmd(bcmd,4,0xa1,strSNO);
	}
	//	4. 获取配置表
	public int BoardCmd33(StringBuilder strCfg) {
		byte bcmd = 0x33;
		return BoardCmd(bcmd,4,0xa1,strCfg);
	}
	//	5. 彰显电源打开并低压使能
	public int BoardCmd40() {
		byte bcmd = 0x40;
		return BoardCmd(bcmd);
	}
	//	6. 总线电源总关闭
	public int BoardCmd41() {
		byte bcmd = 0x41;
		return BoardCmd(bcmd);
	}
	//	7. 总线电源打开并高压使能
	public int BoardCmd42() {
		byte bcmd = 0x42;
		return BoardCmd(bcmd);
	}
	//	8. 设置模组总线为低
	public int BoardCmd44() {
		byte bcmd = 0x44;
		return BoardCmd(bcmd);
	}
	//	9. 设置模组总线为高
	public int BoardCmd45() {
		byte bcmd = 0x45;
		return BoardCmd(bcmd);
	}
	//	10. 获取模组总线电压电流值
	public int BoardCmd4A(StringBuilder strData) {
		byte bcmd = 0x4A;
		return BoardCmd(bcmd,8,0xa1,strData);
	}
	//	11. 获取模组总线保护状态
	public int BoardCmd4B() {
		byte bcmd = 0x4B;
		return BoardCmd(bcmd);
	}

	/***
	 * 主控板上电（供电）
	 * @return
	 */
	public int BoardPowerOn() {
		boolean b = m_commobj.ctlPowerSupply(1);
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		boolean c = m_commobj.ctlPowerSupply(5);
		if(b&&c)
			return 0;

		return -1;
	}

	/***
	 * 主控板下电（断电）
	 * @return
	 */
	public int BoardPowerOff() {

		boolean b = m_commobj.ctlPowerSupply(6);

		boolean c = m_commobj.ctlPowerSupply(2);

		if(b)
			return 0;
		return -1;
	}

	/***
	 * BL电平拉高或拉低
	 * @param bHigh
	 * @return
	 */
	public int BoardSetBL(boolean bHigh) {
		boolean b = m_commobj.controlGpio73(bHigh);
		if(b)
			return 0;
		return -1;
	}

	/***
	 * 获取GPIO74状态，是0？1？
	 * @return
	 */
	public int BoardGetGPIO74() {
		String str0 = m_commobj.getGpio74();
		int ret = Integer.parseInt(str0);
		return ret;
	}

	/***
	 * 核心板初始化自检
	 * @return
	 */
	public int BoardCmd80(StringBuilder strData) {
		byte bcmd = (byte)0x80;
		return BoardCmd(bcmd,null,0x15,0xb0,strData);
	}

	/***
	 * 总线短路与漏电检测
	 * @return
	 */
	public int BoardCmd81(StringBuilder strData) {
		byte bcmd = (byte)0x81;
		return BoardCmd(bcmd,null,0x09,0xb1,strData);
	}

	/***
	 * 发送 单颗模组检测 命令
	 * @return
	 */
	public int BoardSendCmd82() {
		byte[] szcmd = new byte[2];
		szcmd[0]=(byte)0x82;szcmd[1]=0x00;

		DetProtocol prt = new DetProtocol(m_commobj);

		int ret = prt.SendBlock(szcmd);
		return ret;
	}

	/***
	 * 辅助功能中的线路检测（不需要单独上下电操作，完成后总没有）
	 * @return
	 */
	public int BoardSendCmd83() {
		byte[] szcmd = new byte[2];
		szcmd[0]=(byte)0x83;szcmd[1]=0x00;

		DetProtocol prt = new DetProtocol(m_commobj);

		int ret = prt.SendBlock(szcmd);
		return ret;
	}

	/***
	 * 发送 总线上电与检测流程 命令
	 * @return
	 */
	public int BoardSendCmd85() {
		byte[] szcmd = new byte[2];
		szcmd[0]=(byte)0x85;szcmd[1]=0x00;

		Log.d(TAG, "BoardSendCmd85: ");

		DetProtocol prt = new DetProtocol(m_commobj);

		int ret = prt.SendBlock(szcmd);
		return ret;
	}

	/***
	 * 10个雷管连接检测
	 * @param arrIDs:	10个雷管的ID数组
	 * @return
	 */
	public int BoardSendCmd88(int[] arrIDs) {
		byte[] szcmd = new byte[43];

		szcmd[0]=(byte)0x88;szcmd[1]=0x29;

		int n = arrIDs.length;
		szcmd[2] = (byte)n;

		for(n=0;n<arrIDs.length;n++) {
			byte[] arrid = DataConverter.int2BytesLSB(arrIDs[n]);
			System.arraycopy(arrid, 0, szcmd, 3+n*4, 4);
		}

		DetProtocol prt = new DetProtocol(m_commobj);

		int ret = prt.SendBlock(szcmd);
		return ret;
	}

	/***
	 * 5个雷管延时下载
	 * @param arrIDs
	 * @param arrDTs
	 * @return
	 */
	public int BoardSendCmd89(int[] arrIDs,int[] arrDTs) {
		byte[] szcmd = new byte[43];

		szcmd[0]=(byte)0x89;szcmd[1]=0x29;

		int n = arrIDs.length;
		szcmd[2] = (byte)n;

		for(n=0;n<arrIDs.length;n++) {
			byte[] arrid = DataConverter.int2BytesLSB(arrIDs[n]);
			System.arraycopy(arrid, 0, szcmd, 3+n*8, 4);

			arrid = DataConverter.int2BytesLSB(arrDTs[n]);
			System.arraycopy(arrid, 0, szcmd, 3+n*8+4, 4);
		}

		DetProtocol prt = new DetProtocol(m_commobj);

		int ret = prt.SendBlock(szcmd);
		return ret;
	}

	/*
	* 搜寻误接的雷管（总线此时必须有电！）
	* */
	public int BoardSendCmd8B() {
		byte[] szcmd = new byte[2];

		szcmd[0]=(byte)0x8B;szcmd[1]=0x00;

		DetProtocol prt = new DetProtocol(m_commobj);

		int ret = prt.SendBlock(szcmd);
		return ret;
	}

	/***
	 * 雷管网络充电流程
	 * @return
	 */
	public int BoardSendCmd8C(int nCount) {
		int n = (nCount%0x10000);
		byte[] szcmd = new byte[4];

		szcmd[0]=(byte)0x8C;szcmd[1]=0x02;
		szcmd[2] = (byte)(nCount&0xff);
		szcmd[3] = (byte)(nCount>>8);

		DetProtocol prt = new DetProtocol(m_commobj);

		int ret = prt.SendBlock(szcmd);
		return ret;
	}

	/***
	 * 雷管网络放电流程
	 * @return
	 */
	public int BoardSendCmd8D() {
		byte[] szcmd = new byte[2];

		szcmd[0]=(byte)0x8D;szcmd[1]=0x00;

		DetProtocol prt = new DetProtocol(m_commobj);

		int ret = prt.SendBlock(szcmd);
		return ret;
	}

	/***
	 * 雷管网络等待起爆时的电压和是否脱落检测
	 * @param strData
	 * @return
	 */
	public int BoardCmd8E(StringBuilder strData) {
		byte bcmd = (byte)0x8E;
		return BoardCmd(bcmd,null,8,0xbe,strData);
	}


	/***
	 * 读取单颗雷管的ID和管码（不需要单独上下电操作，完成后总线没有电）
	 * @return
	 */
	public int BoardSendCmd84() {
		byte[] szcmd = new byte[2];

		szcmd[0]=(byte)0x84;szcmd[1]=0x00;

		DetProtocol prt = new DetProtocol(m_commobj);

		int ret = prt.SendBlock(szcmd);
		return ret;
	}

	/***
	 * 雷管网络起爆（完成后自动下电），主控板需要在1E版本后
	 * @return
	 */
	public int BoardCmd8F(){
		byte bcmd = (byte)0x8F;
		StringBuilder strData = new StringBuilder();
		return BoardCmd(bcmd,null,0x01,0xbf,strData);
	}
	//////////////////////////////////////////////////////////////////////////////////////////


	//////////////////////////////////////////////////////////////////////////////////////////
	//	1、读取单颗模组ID
	public int ModCmd50(StringBuilder strData){
		byte bcmd = 0x50;
		return BoardCmd(bcmd,4,0xa1,strData);
	}

	//	2、读取模组管码
	public int ModCmd52(int nID,StringBuilder strData){
		byte bcmd = 0x52;
		byte[] szID = DataConverter.int2BytesLSB(nID);
		return BoardCmd(bcmd,szID,8,0xa1,strData);
	}

	//	3、读取模组延时
	public int ModCmd54(int nID,StringBuilder strData){
		byte bcmd = 0x54;
		byte[] szID = DataConverter.int2BytesLSB(nID);
		return BoardCmd(bcmd,szID,4,0xa1,strData);
	}

	//	4、设置模组延时
	public int ModCmd55(int nID,int nDT){
		byte bcmd = 0x55;
		byte[] szID = DataConverter.int2BytesLSB(nID);
		byte[] szDT = DataConverter.int2BytesLSB(nDT);

		byte[] szParam = new byte[8];
		System.arraycopy(szID, 0, szParam, 0, 4);
		System.arraycopy(szDT, 0, szParam, 4, 4);

		StringBuilder strData = new StringBuilder();
		strData.setLength(0);
		return BoardCmd(bcmd,szParam,0,0xa0,strData);
	}

	public int ModCmd56(int nID,String strData){
		return 0;
	}

	//	5、设置模组IO 口输出状态（本指令主要用于总线是否脱落检测）
	public int ModCmd58(int nID,byte bIO){
		byte bcmd = 0x58;
		byte[] szID = DataConverter.int2BytesLSB(nID);

		byte[] szParam = new byte[5];
		System.arraycopy(szID, 0, szParam, 0, 4);
		szParam[4] =bIO;

		StringBuilder strData = new StringBuilder();
		strData.setLength(0);
		return BoardCmd(bcmd,szParam,0,0xa0,strData);
	}

	//	6、设置模组进入休眠状态
	public int ModCmd59(int nID){
		byte bcmd = 0x59;
		byte[] szID = DataConverter.int2BytesLSB(nID);

		StringBuilder strData = new StringBuilder();
		strData.setLength(0);
		return BoardCmd(bcmd,szID,0,0xa0,strData);
	}

	//	7、设置模组进入唤醒状态
	public int ModCmd5A(int nID){
		byte bcmd = 0x5a;
		byte[] szID = DataConverter.int2BytesLSB(nID);

		StringBuilder strData = new StringBuilder();
		strData.setLength(0);
		return BoardCmd(bcmd,szID,0,0xa0,strData);
	}

	//	8、模组发火电容充电
	public int ModCmd5B(int nID){
		byte bcmd = 0x5b;
		byte[] szID = DataConverter.int2BytesLSB(nID);

		StringBuilder strData = new StringBuilder();
		strData.setLength(0);
		return BoardCmd(bcmd,szID,0,0xa0,strData);
	}

	//	9、模组发火电容放电
	public int ModCmd5C(int nID){
		byte bcmd = 0x5c;
		byte[] szID = DataConverter.int2BytesLSB(nID);

		StringBuilder strData = new StringBuilder();
		strData.setLength(0);
		return BoardCmd(bcmd,szID,0,0xa0,strData);
	}

	//	10、模组药头检测
	public int ModCmd5D(int nID){
		byte bcmd = 0x5d;
		byte[] szID = DataConverter.int2BytesLSB(nID);

		StringBuilder strData = new StringBuilder();
		strData.setLength(0);
		return BoardCmd(bcmd,szID,0,0xa0,strData);
	}

	//	11、单颗模组是否在线检测
	public int ModCmd5E(int nID){
		byte bcmd = 0x5e;
		byte[] szID = DataConverter.int2BytesLSB(nID);

		StringBuilder strData = new StringBuilder();
		strData.setLength(0);
		return BoardCmd(bcmd,szID,0,0xa0,strData);
	}

	//	12、雷管网络起爆
	public int ModCmd5F(int nID){
		byte bcmd = 0x5f;
		byte[] szID = DataConverter.int2BytesLSB(nID);

		StringBuilder strData = new StringBuilder();
		strData.setLength(0);
		return BoardCmd(bcmd,szID,0,0xa0,strData);
	}

	//	13、模组延时参数校准
	public int ModCmd60(int nID){
		byte bcmd = 0x60;
		byte[] szID = DataConverter.int2BytesLSB(nID);

		StringBuilder strData = new StringBuilder();
		strData.setLength(0);
		return BoardCmd(bcmd,szID,0,0xa0,strData);
	}

	//	14、ID 的大于等于检查
	public int ModCmd61(int nID){
		byte bcmd = 0x61;
		byte[] szID = DataConverter.int2BytesLSB(nID);

		StringBuilder strData = new StringBuilder();
		strData.setLength(0);
		return BoardCmd(bcmd,szID,0,0xa0,strData);
	}

	//	15、ID 的小于检查
	public int ModCmd62(int nID){
		byte bcmd = 0x62;
		byte[] szID = DataConverter.int2BytesLSB(nID);

		StringBuilder strData = new StringBuilder();
		strData.setLength(0);
		return BoardCmd(bcmd,szID,0,0xa0,strData);
	}

	public int ModCmd64(int nID,StringBuilder strUIDH){
		byte bcmd = 0x64;
		byte[] szID = DataConverter.int2BytesLSB(nID);

		strUIDH.setLength(0);
		return BoardCmd(bcmd,szID,4,0xa1,strUIDH);
	}

	//	16、分组充电
	public int ModCmd7A(byte bGP){
		byte bcmd = 0x7A;
		byte[] szID = new byte[1];
		szID[0]=bGP;

		StringBuilder strData = new StringBuilder();
		strData.setLength(0);
		return BoardCmd(bcmd,szID,0,0xa0,strData);
	}

	/***
	 *
	 *
	 * > 36 00 87
	 * < A1 06 46 99 A8 01 02 03 57
	 * @param strsno
	 * @return
	 */
	public int ModCmd36(StringBuilder strsno){
		byte bcmd = 0x36;

		StringBuilder strData = new StringBuilder();
		int ret =  BoardCmd(bcmd,null,0x06,0xA1,strData);
		if(0!=ret) return ret;

		byte[] szdata = DataConverter.hexStringToBytes(strData.toString());

		String sno = String.format("%c%02X%02X%02X%02X%02X",szdata[0],szdata[1],szdata[2],szdata[3],szdata[4],szdata[5]);

		strsno.setLength(0);
		strsno.append(sno);

		return 0;
	}

	/***
	 *
	 * > 37 06 46 99 A8 01 02 03 B5
	 * < A0 00 18
	 * @param strsno
	 * @return
	 */
	public int ModCmd37(String strsno){
		byte bcmd = 0x37;

		byte[] szno = DataConverter.hexStringToBytes(strsno.substring(1));
		byte[] szdata = new byte[6];
		szdata[0]=0x46;
		System.arraycopy(szno,0,szdata,1,5);

		StringBuilder strData = new StringBuilder();
		int ret =  BoardCmd(bcmd,szdata,0x06,0xa0,strData);
		if(0!=ret) return ret;

		return 0;
	}
	//////////////////////////////////////////////////////////////////////////////////////////

	public void testProc() {
		// TODO Auto-generated method stub
		SerialCommBase comm = new HandSetSerialComm( "/dev/ttyS1",115200);
		//SerialCommBase comm = new SerialComm("COM5",115200);


		int ret = comm.OpenPort();
		if(0!=ret) {
			System.out.println("打开串口失败！");
			return;
		}

		//ArrayList<String> strAllComs = comm.findSystemAllComPorts();

		DetCmd detobj = new DetCmd(comm);
		//	无数据指令测试
		testNoDataCmd(detobj);
		//	有数据返回指令测试
		testData4Cmd(detobj);

		comm.ClosePort();
		return;
	}

	private void testNoDataCmd(DetCmd detobj)
	{
		int ret =0;

		/*		*/
		//	板级指令
		//	30	核心板 ECHO测
		ret = detobj.BoardCmd30();
		if(0!=ret) {
			printErrorMsg((byte)0x30,ret);
		}

		//	41	关闭总线电源
		ret =detobj.BoardCmd41();
		if(0!=ret) {
			printErrorMsg((byte)0x41,ret);
		}

		//	40	使能低压（Android开机后，缺省情况下总线不带电）；
		ret =detobj.BoardCmd40();
		if(0!=ret) {
			printErrorMsg((byte)0x40,ret);
		}


		//	42	总线使能高压
		ret =detobj.BoardCmd42();
		if(0!=ret) {
			printErrorMsg((byte)0x42,ret);
		}

		//	44	设置总线电平为0
		ret =detobj.BoardCmd44();
		if(0!=ret) {
			printErrorMsg((byte)0x44,ret);
		}

		//	45	设置总线电平为1
		ret =detobj.BoardCmd45();
		if(0!=ret) {
			printErrorMsg((byte)0x45,ret);
		}

		//	4B	获取总线保护状态，比如短路等；
		ret =detobj.BoardCmd4B();
		if(0!=ret) {
			printErrorMsg((byte)0x4B,ret);
		}


		//	42	总线使能高压
		ret =detobj.BoardCmd42();
		if(0!=ret) {
			printErrorMsg((byte)0x42,ret);
		}


		//	模块指令 48 00 E4 07
		//	55	用于检测总线上某个EDD是否脱落；
		int nID = 0x07E40048;
		int nDT = 1000;
		ret = detobj.ModCmd55(nID,nDT);
		if(0!=ret) {
			printErrorMsg((byte)0x55,ret);
		}

		//	58	不会对某一个EDD设置，全00set IO
		byte bIO = 0x02;
		ret = detobj.ModCmd58(nID, bIO);
		if(0!=ret) {
			printErrorMsg((byte)0x58,ret);
		}

		//	59	模组上线后是唤醒状态，可以通过指令将模组设置为休眠；只有在休眠状态下可以充电；
		ret = detobj.ModCmd59(nID);
		if(0!=ret) {
			printErrorMsg((byte)0x59,ret);
		}


		//	5A	设置模组进入唤醒状态
		ret = detobj.ModCmd5A(nID);
		if(0!=ret) {
			printErrorMsg((byte)0x5A,ret);
		}

		//	59	模组上线后是唤醒状态，可以通过指令将模组设置为休眠；只有在休眠状态下可以充电；
		ret = detobj.ModCmd59(nID);
		if(0!=ret) {
			printErrorMsg((byte)0x59,ret);
		}

		//	5B	充电
		ret = detobj.ModCmd5B(0);
		if(0!=ret) {
			printErrorMsg((byte)0x5B,ret);
		}

		//	5C	 放电
		ret = detobj.ModCmd5C(0);
		if(0!=ret) {
			printErrorMsg((byte)0x5C,ret);
		}

		//	5D	模组药头检测
		/*
		ret = detobj.ModCmd5D(nID);
		if(0!=ret) {
			printErrorMsg((byte)0x5D,ret);
		}
		*/

		//	5A	设置模组进入唤醒状态
		ret = detobj.ModCmd5A(nID);
		if(0!=ret) {
			printErrorMsg((byte)0x5A,ret);
		}

		//	5F	起爆，必须在休眠状态下
		ret = detobj.ModCmd5F(0);
		if(0!=ret) {
			printErrorMsg((byte)0x5F,ret);
		}


		//	60	模组延时参数校准
		ret = detobj.ModCmd60(nID);
		if(0!=ret) {
			printErrorMsg((byte)0x60,ret);
		}

		//	61	ID大于等于检查
		ret = detobj.ModCmd61(nID);
		if(0!=ret) {
			printErrorMsg((byte)0x61,ret);
		}

		//	62	ID小于检查
		ret = detobj.ModCmd62(nID+1);
		if(0!=ret) {
			printErrorMsg((byte)0x62,ret);
		}

		//	7A	分组充电
		bIO = (byte)(nID&0x0f);
		ret = detobj.ModCmd7A(bIO);
		if(0!=ret) {
			printErrorMsg((byte)0x7A,ret);
		}

		return;
	}

	private void testData4Cmd(DetCmd detobj)
	{
		int ret =0;
		StringBuilder strresp = new StringBuilder();

		//	板级指令
		// 	31	获取版本信息
		strresp.setLength(0);
		ret = detobj.BoardCmd31(strresp);
		if(0!=ret) {
			printErrorMsg((byte)0x31,ret);
		}else {
			System.out.println(String.format("版本:%s", strresp));
		}

		// 	32	获取核心板序列号
		strresp.setLength(0);
		ret = detobj.BoardCmd32(strresp);
		if(0!=ret) {
			printErrorMsg((byte)0x32,ret);
		}else {
			System.out.println(String.format("核心板序列号:%s", strresp));
		}

		//	33	获取配置表
		strresp.setLength(0);
		ret = detobj.BoardCmd33(strresp);
		if(0!=ret) {
			printErrorMsg((byte)0x33,ret);
		}else {
			System.out.println(String.format("配置表:%s", strresp));
		}


		//	4a	获取模组总线电流电压值
		strresp.setLength(0);
		ret = detobj.BoardCmd4A(strresp);
		if(0!=ret) {
			printErrorMsg((byte)0x4a,ret);
		}else {
			System.out.println(String.format("电流电压值:%s", strresp));
		}




		//	模块指令
		//	50	单个模组ID
		strresp.setLength(0);
		ret = detobj.ModCmd50(strresp);
		if(0!=ret) {
			printErrorMsg((byte)0x50,ret);
			strresp.setLength(0);
			strresp.append("12345678");
		}else {
			System.out.println(String.format("单颗模组ID:%s", strresp));
		}


		//	获取ID
		String str = strresp.toString();
		byte[] arr = DataConverter.hexStringToBytes(str);
		int nID = DataConverter.lsbBytes2Int(arr);
		System.out.println(String.format("ID:%d", nID));

		//	52	读取模组管码
		strresp.setLength(0);
		ret = detobj.ModCmd52(nID,strresp);
		if(0!=ret) {
			printErrorMsg((byte)0x52,ret);
		}else {
			System.out.println(String.format("模组管码:%s", strresp));
		}


		//	54
		strresp.setLength(0);
		ret = detobj.ModCmd54(nID,strresp);
		if(0!=ret) {
			printErrorMsg((byte)0x54,ret);
		}else {
			System.out.println(String.format("模组延时:%s", strresp));
		}

		return;
	}

	private void printErrorMsg(byte bCmd,int ret) {
		DetErrorCode dec = new DetErrorCode(bCmd,ret);
		String strerrmsg = dec.GetErrorMessage();
		System.out.println(strerrmsg);
		return;
	}


}
