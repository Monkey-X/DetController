package com.etek.controller.hardware.test;


import com.etek.controller.hardware.command.DetApp;
import com.etek.controller.hardware.command.DetCmd;
import com.etek.controller.hardware.util.DetIDConverter;

public class detTest {

	public static void main(String[] args) {
		DetCmd cmdobj = new DetCmd(null);
		cmdobj.testProc();
		
		
		DetApp appobj = DetApp.getInstance();
		appobj.testProc();
		
		DetIDConverter covobj = new DetIDConverter();
		covobj.testProc();
		
		return;
	}
	

	

}
