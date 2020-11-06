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

}
