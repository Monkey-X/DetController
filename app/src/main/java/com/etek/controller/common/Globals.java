package com.etek.controller.common;

import com.etek.controller.R;

import com.etek.controller.activity.AssistActivity;
import com.etek.controller.activity.AuthBombActivity;
import com.etek.controller.activity.ConnectTestActivity;
import com.etek.controller.activity.DelayDownloadActivity;
import com.etek.controller.activity.NetWorkActivity;
import com.etek.controller.activity.OfflineCheckActivity;
import com.etek.controller.activity.OnlineCheckActivity;

import com.etek.controller.activity.ReportActivity;
import com.etek.controller.activity.SettingsActivity;
import com.etek.controller.activity.UserInfoActivity;
import com.etek.controller.entity.FuncationActivity;

import com.etek.controller.model.User;


public class Globals {


	public static int type = 0;
	public static boolean isServerDanningOn;

	public static boolean isServerZhongbaoOn;

	public static User user;

	public static boolean isLogDocument;

	public static boolean isLogDatabase ;

	public static boolean isBuild ;

	public static String backServer = "";

	public static String authorizeCode = "372562";


    public static double longitude;
	public static double latitude;
	public static String deviceId;
	public static String contractId;
	public static String proId;
//	public static boolean isSimUPload = false;
	public static boolean isTest = false; //true打开测试模式 false为正式版本


	public static String zhongbaoAddress;
	public static String serverAddress;
	public static int serverIndex = 0;

	public static boolean isOnline;

	public static FuncationActivity[] mainFuncation = {
//			new FuncationActivity(OfflineCheckActivity.class,"离线检查",R.string.title_act_offline_checkout,R.drawable.icon_offline),
//			new FuncationActivity(OnlineCheckActivity.class,"在线检查",R.string.title_act_online_checkout,R.drawable.icon_online),
//			new FuncationActivity(CheckoutActivity.class,"数据检查",R.string.title_activity_checkout,R.drawable.check),
			new FuncationActivity(NetWorkActivity.class,"雷管组网",R.string.activity_network,R.drawable.networking),
			new FuncationActivity(ConnectTestActivity.class,"连接检测",R.string.title_activity_connecttest,R.drawable.connet_test),
			new FuncationActivity(DelayDownloadActivity.class,"延时下载",R.string.activity_delay_download,R.drawable.delay_download),
			new FuncationActivity(AuthBombActivity.class,"授权起爆",R.string.activity_auth_bomb,R.drawable.auth),
			new FuncationActivity(ReportActivity.class,"数据上报",R.string.activity_det_report,R.drawable.report),
			new FuncationActivity(AssistActivity.class,"辅助功能",R.string.activity_assist,R.drawable.assist),
			new FuncationActivity(UserInfoActivity.class,"个人设置",R.string.title_activity_personal_info,R.drawable.user),


//			new FuncationActivity()
	};



}
