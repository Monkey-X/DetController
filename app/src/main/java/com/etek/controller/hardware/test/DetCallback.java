package com.etek.controller.hardware.test;

import com.etek.controller.hardware.util.DetIDConverter;

/***
 * 回调测试类
 */



public interface DetCallback {

	public void DisplayText(String strText);

	public void StartProgressbar();

	public void SetProgressbarValue(int nVal);

	/***
	 * 单颗模组检测完成后，回调的信息
	 * @param nID			雷管的ID
	 * @param szDC			管码
	 * @param nDT			延时时间
	 * @param bCheckResult	药头检测结果
	 */
//	public void SetSingleModuleCheckData(int nID,byte[] szDC,int nDT,byte bCheckResult) {
//		DetIDConverter idc = new DetIDConverter();
//
//		String strDC = idc.GetDisplayDC(szDC);
//		String str = String.format("ID=%d,DC=%s,DT=%d,Result=%d", nID,strDC,nDT,bCheckResult);
//		System.out.println(str);
//		return;
//	}
//
	public void SetSingleModuleCheckData(int nID,byte[] szDC,int nDT,byte bCheckResult);

	/***
	 * 核心板初始化自检回调信息
	 * @param strHardwareVer
	 * @param strUpdateHardwareVer
	 * @param strSNO
	 * @param strConfig
	 * @param bCheckResult
	 */
//	 	public void SetInitialCheckData(String strHardwareVer,
//			String strUpdateHardwareVer,
//			String strSoftwareVer,
//			String strSNO,
//			String strConfig,
//			byte bCheckResult);
/*
	public void SetInitialCheckData(String strHardwareVer,
			String strUpdateHardwareVer,
			String strSoftwareVer,
			String strSNO,
			String strConfig,
			byte bCheckResult) {
		System.out.println(String.format("硬件版本：%s",strHardwareVer));
		System.out.println(String.format("硬件升级版本：%s",strUpdateHardwareVer));
		System.out.println(String.format("软件版本：%s",strSoftwareVer));
		System.out.println(String.format("序列号：%s",strSNO));
		System.out.println(String.format("配置信息：%s",strConfig));
		System.out.println(String.format("自检建国：%d", bCheckResult));
		
		return;
	}
*/

	/***
	 * 批量操作雷管时结果回调函数
	 * @param nID
	 * @param nResult
	 */
	public void SetDetsSettingResult(int nID,int nResult);
//	{
//		System.out.println(String.format("雷管 0x%08X 结果:%d", nID,nResult));
//		return;
//	}

	/***
	 * 充电和放电过程中返回的电压和电流值
	 * @param nVoltage
	 * @param nCurrent
	 */
	public void SetChargeData(int nVoltage,int nCurrent);
//	{
//		System.out.println(String.format("\t当前：电压 %d 电流:%d",nVoltage,nCurrent));
//		return;
//	}
}
