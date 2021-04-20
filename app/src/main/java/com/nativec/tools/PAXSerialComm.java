package com.nativec.tools;

import android.util.Log;

import com.etek.controller.hardware.comm.SerialCommBase;
import com.etek.controller.hardware.command.DetErrorCode;
import com.etek.controller.hardware.util.DataConverter;
import com.etek.controller.hardware.util.DetLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/***
 * 百富(PAX)串口类
 */
public class PAXSerialComm extends SerialCommBase {

    private String TAG = "PAXSerialComm";

    private SerialPort m_comobj=null;

    public PAXSerialComm(String portName,int nBaud){
        super(portName,nBaud);
        m_comobj= null;
    }

    public int OpenPort(){
        int ret = 0;

        if(null!=m_comobj) {
            m_comobj.close();
            m_comobj = null;
        }

        try {
            m_comobj = new SerialPort(new File("dev/ttyHSL2"),m_nBaudrate,0);
        }catch (Exception e){
            e.printStackTrace();
            m_comobj = null;
            return -1;
        }

        return 0;
    }

    /***
     * 关闭打开的串口
     */
    public void ClosePort(){
        if(null!=m_comobj) {
            m_comobj.close();
            m_comobj = null;
        }
        return;
    }

    /***
     * 发送字节流
     * @param szcmd		发送的字节流
     * @return
     * 0	成功
     * 其他		失败
     */
    public int SendBlock(byte[] szcmd){
        if(null==m_comobj) {
            m_nErrorCode = DetErrorCode.ERR_COMM_OPEN;
            return -1;
        }

        FileOutputStream os = null;
        try {
            os = m_comobj.getOutputStream();
            os.write(szcmd);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        } finally {
        }

        return 0;
    }

    /***
     * 接收指定的字节流
     * @param nLen		期望接收的字节流数
     * @return
     * 	成功：	字节流
     * 	其他：失败
     * @throws InterruptedException
     */
    public byte[] RecvBlock(int nLen){
        if(null==m_comobj) {
            m_nErrorCode = DetErrorCode.ERR_COMM_OPEN;
            return null;
        }

        int nTotalLen = 0;
        String str0="";

        int ret = WaitTimeout();
        Log.d(TAG,"RecvBlock -->WaitTimeout:"+ret);
        if(0!=ret) {
            m_nErrorCode = DetErrorCode.ERR_COMM_RECV_TIMEOUT;
            return null;
        }

        //保存串口返回信息
        FileInputStream is = null;
        byte[] bytes = null;

        try {
            is = m_comobj.getInputStream();
            int bufflenth = is.available();//获得数据长度
            Log.d(TAG,"RecvBlock -->bufflenth:"+bufflenth);

            //	不能全部都收回来
            if(bufflenth>nLen)
                bufflenth = nLen;

            while (bufflenth != 0) {
                bytes = new byte[bufflenth];//初始化byte数组
                is.read(bytes);

                str0 = str0+ DataConverter.bytes2HexString(bytes);

                nTotalLen = nTotalLen + bufflenth;
                if(nTotalLen>=nLen) break;

                Thread.sleep(30);

                bufflenth = is.available();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        } finally {
        }

        if(str0.length()>0)
            bytes = DataConverter.hexStringToBytes(str0);

        return bytes;
    }

    /***
     * 发送接收
     * @param szcmd		发送的字节流
     * @param nLen		期望接收的字节流个数
     * @return
     * 	成功：	字节流
     * 	失败：	null
     */
    public byte[] SendRecv(byte[] szcmd,int nLen){
        if(null==m_comobj) {
            m_nErrorCode = DetErrorCode.ERR_COMM_OPEN;
            return null;
        }

        int ret;
        Log.d(TAG,"function SendRec,FlushComm start...");
        //	清除缓冲
        FlushComm();
        Log.d(TAG,"function SendRec,FlushComm end...");

        //	发送
        ret = SendBlock(szcmd);
        Log.d(TAG,"function SendRec,SendBlock end...");
        if(0!=ret) {
            Log.d(TAG,"function SendRec,SendBlock 返回为空");
            return null;
        }

        Log.d(TAG,"function SendRec,RecvBlock start...");
        //	接收
        return RecvBlock(nLen);
    }


    /***
     * 清除串口的输入缓存
     */
    public void FlushComm() {
        if(null==m_comobj) {
            m_nErrorCode = DetErrorCode.ERR_COMM_OPEN;
            return ;
        }

        //保存串口返回信息
        FileInputStream is = null;
        byte[] bytes = null;
        try {
            is = m_comobj.getInputStream();
            int bufflenth = is.available();//获得数据长度
            Log.d(TAG,"function FlushComm,bufflenth:"+ bufflenth);

            while (bufflenth != 0) {
                bytes = new byte[bufflenth];//初始化byte数组
                is.read(bytes);
                DetLog.writeLog(TAG,"FlushComm:"+ Arrays.toString(bytes));

                bufflenth = is.available();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        return;
    }

    /***
     * 等待指令的时间，判断串口是否有数据输入
     * @return
     */
    public int WaitTimeout() {
        int ret=0;

        if(null==m_comobj) {
            m_nErrorCode = DetErrorCode.ERR_COMM_OPEN;
            return -1;
        }

        long t0 = System.currentTimeMillis();
        //保存串口返回信息
        FileInputStream is = null;

        try {
            is = m_comobj.getInputStream();
            int bufflenth = is.available();//获得数据长度
            Log.d(TAG,"WaitTimeout -->bufflenth:"+bufflenth);
            while(true){
                if(bufflenth>0) break;
                Thread.sleep(10);

                //	超时判断
                long t1 = System.currentTimeMillis();

                if(t1-t0>m_nTimeout) {
                    ret = -1;
                    break;
                }
                bufflenth = is.available();//获得数据长度
            }

        } catch (IOException e) {
            ret = -1;
            e.printStackTrace();
        } catch (InterruptedException e){
            ret = -1;
            e.printStackTrace();
        }finally {
        }

        return ret;
    }


    /**
     * 开关总电源
     *
     * @param operationValue 1,2 开关总电源  3,4 开关串口S0电源    5,6开关串口S1电源
     * @return true 成功，false失败
     */
    public boolean ctlPowerSupply(int operationValue) {
        if(null==m_comobj) {
            m_nErrorCode = DetErrorCode.ERR_COMM_OPEN;
            return false;
        }

        String cmd_power = SerialPort.vcc_en_gpio;
        String str1 = "当前电源状态:    " + m_comobj.execRootCmd("cat " + cmd_power);
        Log.d(TAG,str1);
        switch (operationValue){
            case 1:
                m_comobj.execRootCmdSilent("echo 1 > " + cmd_power);
                str1 = "设置后电源状态:    " + m_comobj.execRootCmd("cat " + cmd_power);
                Log.d(TAG,str1);
                break;
            case 3:
            case 5:
                break;
            case 2:
                m_comobj.execRootCmdSilent("echo 0 > " + cmd_power);
                str1 = "设置后电源状态:    " + m_comobj.execRootCmd("cat " + cmd_power);
                Log.d(TAG,str1);
                break;
            case 4:
            case 6:
                break;
        }
        return true;
    }

    // true 拉高，false拉低
    public boolean controlGpio73(boolean ifpullHigh) {
        if(null==m_comobj) {
            m_nErrorCode = DetErrorCode.ERR_COMM_OPEN;
            return false;
        }

        String cmd_121 = SerialPort.uhf_en_gpio;
        String str1 = "当前电平输出:    " + m_comobj.execRootCmd("cat " + cmd_121);
        Log.d(TAG,str1);
        if(ifpullHigh){
            m_comobj.execRootCmdSilent("echo 1 > " + cmd_121);
        }else{
            m_comobj.execRootCmdSilent("echo 0 > " + cmd_121);
        }
        str1 = "现在电平输出:    " + m_comobj.execRootCmd("cat " + cmd_121);
        Log.d(TAG,str1);

        return true;
    }


    //获取GPIO74的值
    public String getGpio74() {
        if(null==m_comobj) {
            m_nErrorCode = DetErrorCode.ERR_COMM_OPEN;
            return "";
        }
        String cmd_74 = SerialPort.handle_pwr;
        String str1 = "当前电平输出:    " + m_comobj.execRootCmd("cat " + cmd_74);

        return str1;
    }
}
