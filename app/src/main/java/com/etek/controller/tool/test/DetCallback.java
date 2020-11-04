/***
 * 回调测试类
 */

package com.etek.controller.tool.test;

public class DetCallback {

	public void DisplayText(String strText) {
		System.out.println(strText);
		return;
	}
	
	public void StartProgressbar() {
		return;
	}
	
	public void SetProgressbarValue(int nVal) {
		String str = String.format("%d", nVal);
		System.out.println(str+"%");
		return;		
	}
	
}
