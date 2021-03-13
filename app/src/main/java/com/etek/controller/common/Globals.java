package com.etek.controller.common;

import com.etek.controller.R;
import com.etek.controller.activity.AssistActivity;
import com.etek.controller.activity.AuthBombActivity;
import com.etek.controller.activity.project.ConnectTestActivity;
import com.etek.controller.activity.project.DelayDownloadActivity;
import com.etek.controller.activity.NetWorkActivity;
import com.etek.controller.activity.ReportActivity2;
import com.etek.controller.activity.UserInfoActivity;
import com.etek.controller.entity.FuncationActivity;
import com.etek.controller.model.User;

public class Globals {


	public static int type = 0;
	// 数据上报开关
	public static boolean isServerDanningOn = true;

	public static boolean isTest = true; //true打开测试模式 false为正式版本

	public static boolean isServerZhongbaoOn = true;



	public static User user;



    public static double longitude;
	public static double latitude;
	public static String deviceId;
	public static String contractId;
	public static String proId;


	public static String zhongbaoAddress;
	public static boolean isOnline;





}
