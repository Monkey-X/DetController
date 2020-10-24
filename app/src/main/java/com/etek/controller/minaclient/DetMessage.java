<<<<<<< HEAD
package com.etek.controller.minaclient;


import com.etek.controller.utils.SommerUtils;

import org.apache.mina.core.buffer.IoBuffer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class DetMessage {

	private double lng;
	private double lat;
	private String sn;
	private String qbDate;

	private byte[] msgByte;
//	private ConcurrentMap<String, String> leiguan = new ConcurrentHashMap<String, String>();//

	private List<String> detonators = new ArrayList<String>();

	private int packCount;

	private List<String> packNo = new ArrayList<String>();

	private boolean isOver = false;

	private Date recordTime = new Date();

	private int sendCount = 3;

	private int totalDet = 0;

	int type;
	
	
	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getQbDate() {
		return qbDate;
	}

	public void setQbDate(String qbDate) {
		this.qbDate = qbDate;
	}

	public boolean isOver() {
		return isOver;
	}

	public void setOver(boolean isOver) {
		this.isOver = isOver;
	}

	public Date getRecordTime() {
		return recordTime;
	}

	public void setRecordTime(Date recordTime) {
		this.recordTime = recordTime;
	}

//	public Map<String, String> getLeiguan() {
//		return leiguan;
//	}
//
//	public void putLeiguan(String a, String b) {
//		leiguan.put(a, b);
//	}

	public int getPackCount() {
		return packCount;
	}

	public void setPackCount(int packCount) {
		this.packCount = packCount;
	}


	public byte[] getMsgByte() {
		return msgByte;
	}

	public void setMsgByte(byte[] msgByte) {
		this.msgByte = msgByte;
	}

	public List<String> getUnPackNo() {
		List<String> tmp = new ArrayList<String>();
		if(packNo.size()==packCount){
			return tmp;
		}
		for(int i=1;i<=packCount;i++){
			String s = i<10?("0"+i):String.valueOf(i);
			if(!packNo.contains(s)){
				tmp.add(s);
			}
		}
		return tmp;
	}

	public void addPackNo(String packNo) {
		boolean iscz = false;
		for(String no : this.packNo){
			if(no.equals(packNo)){
				iscz = true;
				break;
			}
		}
		if(!iscz){
			this.packNo.add(packNo);
		}
	}
	
	public String getLngStr() {
		return String.valueOf(lng*10000).split("\\.")[0];
	}
	
	public String getLatStr() {
		return String.valueOf(lat*10000).split("\\.")[0];
	}
	
	public int getSendCount() {
		sendCount = sendCount>0?sendCount-1:-1;
		return sendCount;
	}

	public void setSendCount(int sendCount) {
		this.sendCount = sendCount;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public List<String> getDetonators() {
		return detonators;
	}

	public void setDetonators(List<String> detonators) {
		this.detonators = detonators;
	}

	public void clrDetonators() {
		this.detonators.clear();
	}
	public void addDetonator(String detonator) {

		this.detonators.add(detonator);
	}

	public int getTotalDet() {
		return totalDet;
	}

	public void setTotalDet(int totalDet) {
		this.totalDet = totalDet;
	}

	public byte[] toByte(){
		byte bw = '$';//$

		IoBuffer buf = IoBuffer.allocate(300).setAutoExpand(true);
		buf.mark();
		String a = null;
		if(packCount>99){
			packCount = 99;
		}
		String countStr = String.format("%02d",packCount);
		if(sendCount>99){
			sendCount = 99;
		}
		String sendStr = String.format("%02d",sendCount);


		String lenStr="000";
		if(type==0){
			lenStr = String.format("%03d",48);
			if(totalDet>1000){
				totalDet = 999;
			}
			String totalDetStr = String.format("%03d",totalDet);
//			System.out.println(totalDetStr);
			a = "*"+countStr+sendStr+lenStr+this.getSn()+this.getLngStr()+this.getLatStr()+this.getQbDate()+totalDetStr;
//			System.out.println(a.length());
		}else{
			int len = 20+ 14*(detonators.size());
			lenStr = String.format("%03d",len);
			a = "*"+countStr+sendStr+lenStr+this.getSn();
			for(String det:detonators){
				a +=det;
			}

		}

		byte[] b = a.getBytes();
		buf.put(b);

		String xor = String.valueOf(SommerUtils.getXor(b, 0, b.length));
		while (xor.length()<3) {
			xor = "0"+xor;
		}
		buf.put(xor.getBytes());

		buf.put(bw);

		byte[] bb = new byte[buf.position()];
		buf.reset();
		buf.get(bb);

		setMsgByte(bb);

//		System.out.println(new String(buf.array()));
		return bb;
	}
}
=======
package com.etek.controller.minaclient;


import com.etek.controller.utils.SommerUtils;

import org.apache.mina.core.buffer.IoBuffer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class DetMessage {

	private double lng;
	private double lat;
	private String sn;
	private String qbDate;

	private byte[] msgByte;
//	private ConcurrentMap<String, String> leiguan = new ConcurrentHashMap<String, String>();//

	private List<String> detonators = new ArrayList<String>();

	private int packCount;

	private List<String> packNo = new ArrayList<String>();

	private boolean isOver = false;

	private Date recordTime = new Date();

	private int sendCount = 3;

	private int totalDet = 0;

	int type;
	
	
	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getQbDate() {
		return qbDate;
	}

	public void setQbDate(String qbDate) {
		this.qbDate = qbDate;
	}

	public boolean isOver() {
		return isOver;
	}

	public void setOver(boolean isOver) {
		this.isOver = isOver;
	}

	public Date getRecordTime() {
		return recordTime;
	}

	public void setRecordTime(Date recordTime) {
		this.recordTime = recordTime;
	}

//	public Map<String, String> getLeiguan() {
//		return leiguan;
//	}
//
//	public void putLeiguan(String a, String b) {
//		leiguan.put(a, b);
//	}

	public int getPackCount() {
		return packCount;
	}

	public void setPackCount(int packCount) {
		this.packCount = packCount;
	}


	public byte[] getMsgByte() {
		return msgByte;
	}

	public void setMsgByte(byte[] msgByte) {
		this.msgByte = msgByte;
	}

	public List<String> getUnPackNo() {
		List<String> tmp = new ArrayList<String>();
		if(packNo.size()==packCount){
			return tmp;
		}
		for(int i=1;i<=packCount;i++){
			String s = i<10?("0"+i):String.valueOf(i);
			if(!packNo.contains(s)){
				tmp.add(s);
			}
		}
		return tmp;
	}

	public void addPackNo(String packNo) {
		boolean iscz = false;
		for(String no : this.packNo){
			if(no.equals(packNo)){
				iscz = true;
				break;
			}
		}
		if(!iscz){
			this.packNo.add(packNo);
		}
	}
	
	public String getLngStr() {
		return String.valueOf(lng*10000).split("\\.")[0];
	}
	
	public String getLatStr() {
		return String.valueOf(lat*10000).split("\\.")[0];
	}
	
	public int getSendCount() {
		sendCount = sendCount>0?sendCount-1:-1;
		return sendCount;
	}

	public void setSendCount(int sendCount) {
		this.sendCount = sendCount;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public List<String> getDetonators() {
		return detonators;
	}

	public void setDetonators(List<String> detonators) {
		this.detonators = detonators;
	}

	public void clrDetonators() {
		this.detonators.clear();
	}
	public void addDetonator(String detonator) {

		this.detonators.add(detonator);
	}

	public int getTotalDet() {
		return totalDet;
	}

	public void setTotalDet(int totalDet) {
		this.totalDet = totalDet;
	}

	public byte[] toByte(){
		byte bw = '$';//$

		IoBuffer buf = IoBuffer.allocate(300).setAutoExpand(true);
		buf.mark();
		String a = null;
		if(packCount>99){
			packCount = 99;
		}
		String countStr = String.format("%02d",packCount);
		if(sendCount>99){
			sendCount = 99;
		}
		String sendStr = String.format("%02d",sendCount);


		String lenStr="000";
		if(type==0){
			lenStr = String.format("%03d",48);
			if(totalDet>1000){
				totalDet = 999;
			}
			String totalDetStr = String.format("%03d",totalDet);
//			System.out.println(totalDetStr);
			a = "*"+countStr+sendStr+lenStr+this.getSn()+this.getLngStr()+this.getLatStr()+this.getQbDate()+totalDetStr;
//			System.out.println(a.length());
		}else{
			int len = 20+ 14*(detonators.size());
			lenStr = String.format("%03d",len);
			a = "*"+countStr+sendStr+lenStr+this.getSn();
			for(String det:detonators){
				a +=det;
			}

		}

		byte[] b = a.getBytes();
		buf.put(b);

		String xor = String.valueOf(SommerUtils.getXor(b, 0, b.length));
		while (xor.length()<3) {
			xor = "0"+xor;
		}
		buf.put(xor.getBytes());

		buf.put(bw);

		byte[] bb = new byte[buf.position()];
		buf.reset();
		buf.get(bb);

		setMsgByte(bb);

//		System.out.println(new String(buf.array()));
		return bb;
	}
}
>>>>>>> 806c842... 雷管组网
