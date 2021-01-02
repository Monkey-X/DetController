package com.etek.controller.hardware.util;/*
 * 数据转换类
 * @author Xin Hongwei
 * @ version 1.00
 * */


public class DataConverter {

	/***
	 * 十六进制字符串为字节值
	 * @param c		要转的字符
	 * @return		字节值
	 */
	public static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	/***
	 * bcd码
	 * @param b	要转的字符
	 * @return	bcd码值
	 */
	public static byte bcd(byte b) {
		byte ch1, ch2;

		ch2 = (byte) (b % 10);
		ch1 = (byte) ((b - ch2) / 10);

		return (byte) (ch1 * 0x10 + ch2);
	}

	public static byte bcd2Hex(byte b) {
		byte ch1, ch2;
		ch2 = (byte) (b % 0x10);
		ch1 = (byte) ((b - ch2) / 0x10);
		return (byte) (ch1 * 10 + ch2);
	}

	public static byte[] int2Bytes(int n) {
		byte[] arr = new byte[4];

		arr[0] = (byte) (n >> 24);
		arr[1] = (byte) (n >> 16);
		arr[2] = (byte) (n >> 8);
		arr[3] = (byte) (n & 0xff);
		return arr;
	}

	public static byte[] int2BytesLSB(int n) {
		byte[] arr = new byte[4];

		arr[3] = (byte) (n >> 24);
		arr[2] = (byte) (n >> 16);
		arr[1] = (byte) (n >> 8);
		arr[0] = (byte) (n & 0xff);
		return arr;
	}

	public static int bytes2Int(byte[] arr) {
		int n = 0;
		n = (0x000000FF & arr[0]);
		n = n * 0x100 + (0x000000FF & arr[1]);
		n = n * 0x100 + (0x000000FF & arr[2]);
		n = n * 0x100 + (0x000000FF & arr[3]);
		return n;
	}

	public static int lsbBytes2Int(byte[] arr) {
		int n = 0;
		n = (0x000000FF & arr[3]);
		n = n * 0x100 + (0x000000FF & arr[2]);
		n = n * 0x100 + (0x000000FF & arr[1]);
		n = n * 0x100 + (0x000000FF & arr[0]);
		return n;
	}

	public static int bytes2Word(byte[] arr){
		int n = 0;
		n = (0x000000FF & arr[0]);
		n = n * 0x100 + (0x000000FF & arr[1]);
		return n;
	}

	public static byte[] word2Bytes(short n){
		byte[] arr = new byte[2];
		arr[0] = (byte) (n >> 8);
		arr[1] = (byte) (n & 0xff);
		return arr;
	}

	public static String bytes2HexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return "";
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	public static int getByteValue(byte b) {
		if(b>=0) return (int)b;
		return (int)(0x100+b);
	}

	public static long getIntValue(int n) {
		if(n>=0) return n;
		return (long)(0x100000000L+n);
	}
}
