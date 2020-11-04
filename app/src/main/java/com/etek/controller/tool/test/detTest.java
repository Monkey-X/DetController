package com.etek.controller.tool.test;


import com.etek.controller.tool.command.DetApp;
import com.etek.controller.tool.command.DetCmd;
import com.etek.controller.tool.util.DetIDConverter;

public class detTest {

	public static void main(String[] args) {
		DetCmd cmdobj = new DetCmd(null);
		cmdobj.testProc();
		
		
		DetApp appobj = new DetApp();
		appobj.testProc();
		
		DetIDConverter covobj = new DetIDConverter();
		covobj.testProc();
		
		return;
	}
	

	

}
