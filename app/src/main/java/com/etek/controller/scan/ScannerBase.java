package com.etek.controller.scan;

import android.content.Context;

/***
 * 扫描器基类
 */
public abstract class ScannerBase {
    public Context mContext;

    public ScannerBase(Context context) {
        mContext = context;
    }

    //	1.打开扫描设置界面
    public abstract void ShowUI();

    //	2.打开扫描头电源
    public abstract void open();

    //2，关闭扫描头电源
    public abstract void  close();

    // 3. 触发扫描头，扫描头出光
    public abstract void  scan_start();

    //4.停止扫描头解码，扫描头灭光
    public abstract void scan_stop();

    /***锁定设备的扫描按键，锁定后，只能通过iScan定义的扫描按键控制扫描，用户无法自定义按键。*/
    public abstract void  lockScanKey();

    /******解除对扫描按键的锁定。解除后iScan无法控制扫描键，用户可自定义按键。*/
    public abstract void unlockScanKey();


    /**扫描头的输出模式*/
    public abstract void setOutputMode(int mode);

    /**8 是否播放声音*/
    public abstract void enablePlayBeep(boolean enable);

    /**扫描失败是否播放声音*/
    public abstract void enableFailurePlayBeep(boolean enable);


    /**9 是否震动*/
    public abstract void enablePlayVibrate(boolean enable);

    /**  附加回车、换行等>*/
    public abstract void  enableAddKeyValue(int value);
    /************************************************************/

    //添加前缀
    public abstract void  addPrefix(String  text);

    //添加后缀
    public abstract void  addSuffix(String  text);
    //截取左字符
    public abstract void interceptTrimleft  (int  num);

    //截取右字符
    public abstract void interceptTrimright  (int  num);

    //右侧Led灯光控制
    public abstract void   lightSet (boolean enable );

    //设置超时时间
    public abstract void timeOutSet(int  value);
    //过滤特定字符
    public abstract void filterCharacter (String text );

    // 是否连扫
    public abstract void continceScan (boolean enable );

    //连续扫描间隔时间
    public abstract void  intervalSet(int  value);

    //扫描失败广播
    public abstract void SetErrorBroadCast (boolean enable );

    //恢复默认设置
    public abstract void resultScan();

    public abstract  void doScan();
}
