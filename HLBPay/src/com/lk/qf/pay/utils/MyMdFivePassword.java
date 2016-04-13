package com.lk.qf.pay.utils;

import java.security.MessageDigest;

import android.util.Log;

public class MyMdFivePassword {

	/**
	 * md5 ����  32λ��д
	 * 
	 * @param s
	 * @return
	 */
	public final static String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = s.getBytes(); // ���MD5ժҪ�㷨�� MessageDigest ����
			MessageDigest mdInst = MessageDigest.getInstance("MD5"); // ʹ��ָ�����ֽڸ���ժҪ
			mdInst.update(btInput); // �������
			byte[] md = mdInst.digest();
			//
			// ������ת����ʮ����Ƶ��ַ���ʽ
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String  MYMD(String pwd){
		return MD5(pwd);
		
	}

}
