package com.szyd.jni; /***
 * 手持机串口通信类
 */

import com.etek.controller.hardware.comm.SerialCommBase;
import com.etek.controller.hardware.command.DetErrorCode;
import com.etek.controller.hardware.util.DataConverter;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class HandSetSerialComm extends SerialCommBase {

	private UHFInfo m_comobj=null;
	private FileDescriptor m_fd=null;

	public HandSetSerialComm(String portName,int nBaud){
		super(portName,nBaud);
		m_comobj= null;
	}

	public int OpenPort(){
		int ret = 0;
		boolean b = false;
		if("/dev/ttyS0".equals(m_strPortName))
			b = true;

		if(null!=m_comobj) {
			m_comobj.close(m_strPortName);
			m_comobj = null;
		}

		m_fd = m_comobj.getmFd(b);

		if(null==m_fd) {
			m_nErrorCode = DetErrorCode.ERR_COMM_OPEN;
			ret = -1;
		}

		m_nErrorCode = DetErrorCode.SUCCESS;
		return ret;
	}

	/***
	 * 关闭打开的串口
	 */
	public void ClosePort(){
		if(null!=m_comobj) {
			m_comobj.close(m_strPortName);
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

		FileOutputStream os = null;

		try {
			os = new FileOutputStream(m_fd);
			os.write(szcmd);
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		} finally {
			//关闭流操作
			try {
				if (os != null) {
					os.close();
					os = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
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

		int nTotalLen = 0;

		String str0="";

		int ret = WaitTimeout();
		if(0!=ret) {
			m_nErrorCode = DetErrorCode.ERR_COMM_RECV_TIMEOUT;
			return null;
		}

		//保存串口返回信息
		FileInputStream is = null;
		byte[] bytes = null;

		try {
			is = new FileInputStream(m_fd);
			int bufflenth = is.available();//获得数据长度

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
			try {
				if (is != null) {
					is.close();
					is = null;
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
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
		int ret;

		if(null==m_fd) {
			m_nErrorCode = DetErrorCode.ERR_COMM_NOT_OPEN;
			return null;
		}

		//	清除缓冲
		FlushComm();

		//	发送
		ret = SendBlock(szcmd);
		if(0!=ret) return null;

		//	接收
		return RecvBlock(nLen);
	}


	/***
	 * 情书串口的输入缓存
	 */
	public void FlushComm() {
		if(null==m_fd) return;

		//保存串口返回信息
		FileInputStream is = null;
		byte[] bytes = null;
		try {
			is = new FileInputStream(m_fd);
			int bufflenth = is.available();//获得数据长度
			while (bufflenth != 0) {
				bytes = new byte[bufflenth];//初始化byte数组
				is.read(bytes);

				bufflenth = is.available();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
					is = null;
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		return;
	}

	/***
	 * 等待指令的时间，判断串口是否有数据输入
	 * @return
	 */
	public int WaitTimeout() {
		int ret=0;

		long t0 = System.currentTimeMillis();
		//保存串口返回信息
		FileInputStream is = null;

		try {
			is = new FileInputStream(m_fd);
			int bufflenth = is.available();//获得数据长度
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
			try {
				if (is != null) {
					is.close();
					is = null;
				}
			} catch(IOException e) {
				ret = -1;
				e.printStackTrace();
			}
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

		return m_comobj.ctlPowerSupply(operationValue);
	}
	// true 拉高，false拉低
	public boolean controlGpio73(boolean ifpullHigh) {
		if(null==m_comobj) {
			m_nErrorCode = DetErrorCode.ERR_COMM_OPEN;
			return false;
		}

		return m_comobj.controlGpio73(ifpullHigh);
	}


	//获取GPIO74的值
	public String getGpio74() {
		if(null==m_comobj) {
			m_nErrorCode = DetErrorCode.ERR_COMM_OPEN;
			return "";
		}

		return m_comobj.getGpio74();
	}
}
