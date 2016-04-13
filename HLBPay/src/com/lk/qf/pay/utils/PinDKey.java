package com.lk.qf.pay.utils;

import java.io.ByteArrayOutputStream;
import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import com.cbapay.util.ByteUtils;

import android.util.Log;

public class PinDKey {
	private static String DES = "DES/ECB/NoPadding";
	private static String TriDes = "DESede/ECB/NoPadding";
	public static final String PINZMK = "9756AE2C988C4C52A115A44376DBEFEF";
	public static final String ZMK = "11111111111111110123456789ABCDEF";

	/**
	 * 
	 * @param asc
	 * @return
	 */
	public static byte[] str2Bcd(String asc) {
		int len = asc.length();
		int mod = len % 2;

		if (mod != 0) {
			asc = "0" + asc;
			len = asc.length();
		}

		byte[] abt = new byte[len];
		if (len >= 2) {
			len /= 2;
		}

		byte[] bbt = new byte[len];
		abt = asc.getBytes();

		for (int p = 0; p < asc.length() / 2; p++) {
			int j;
			if ((abt[(2 * p)] >= 48) && (abt[(2 * p)] <= 57)) {
				j = abt[(2 * p)] - 48;
			} else {
				if ((abt[(2 * p)] >= 97) && (abt[(2 * p)] <= 122))
					j = abt[(2 * p)] - 97 + 10;
				else
					j = abt[(2 * p)] - 65 + 10;
			}
			int k;
			if ((abt[(2 * p + 1)] >= 48) && (abt[(2 * p + 1)] <= 57)) {
				k = abt[(2 * p + 1)] - 48;
			} else {
				if ((abt[(2 * p + 1)] >= 97) && (abt[(2 * p + 1)] <= 122))
					k = abt[(2 * p + 1)] - 97 + 10;
				else {
					k = abt[(2 * p + 1)] - 65 + 10;
				}
			}
			int a = (j << 4) + k;
			byte b = (byte) a;
			bbt[p] = b;
		}
		return bbt;
	}

	public static byte[] str2Bcd16(String asc) {
		int len = asc.length();
		int mod = len % 2;

		if (mod != 0) {
			asc = "0" + asc;
			len = asc.length();
		}

		byte abt[] = new byte[len];
		if (len >= 2) {
			len = len / 2;
		}

		byte bbt[] = new byte[len];
		abt = asc.getBytes();
		int j, k;

		for (int p = 0; p < asc.length() / 2; p++) {
			if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
				j = abt[2 * p] - '0';
			} else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
				j = abt[2 * p] - 'a' + 0x0a;
			} else {
				j = abt[2 * p] - 'A' + 0x0a;
			}

			if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
				k = abt[2 * p + 1] - '0';
			} else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
				k = abt[2 * p + 1] - 'a' + 0x0a;
			} else {
				k = abt[2 * p + 1] - 'A' + 0x0a;
			}

			int a = (j << 4) + k;
			byte b = (byte) a;
			bbt[p] = b;
		}
		return bbt;
	}

	public static String PinEncrypt(String account, String passwd) {
		String result = "";
		String accountTemp1 = "";
		int passwdLen = passwd.length();
		if (passwdLen == 0)
			passwd = "FFFFFF";
		else if (passwdLen < 6) {
			for (int i = 0; i < 6 - passwdLen; i++) {
				passwd = passwd + "F";
			}
		}

		String passwdTemp1 = "0" + passwdLen + passwd + "FFFFFFFF";

		if ((account != null) && (!"".equals(account))) {
			int len = account.length();
			String accountTemp = account.substring(len - 13, len - 1);
			accountTemp1 = "0000" + accountTemp;
		}
		if (accountTemp1.equals("")) {
			result = passwdTemp1;
		} else {
			byte[] accountByte = str2Bcd(accountTemp1);
			byte[] passwdByte = str2Bcd(passwdTemp1);

			byte[] resultByte = new byte[8];

			for (int i = 0; i < 8; i++) {
				resultByte[i] = ((byte) (accountByte[i] ^ passwdByte[i]));
			}
			result = bytesToHexString(resultByte);
		}

		return result.toUpperCase();
	}

	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder();
		if ((src == null) || (src.length <= 0)) {
			return null;
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

	public static String pinResultMak(String zmk, String pinKey,
			String account, String passwd) {
		String pinBlack = PinEncrypt(account, passwd);
		Log.i("result", "---------pinBlack-----------" + pinBlack);
		byte[] tmkByte = str2Bcd(zmk);
		byte[] pinkByte = str2Bcd(pinKey);
		byte[] pinkBlackByte = str2Bcd(pinBlack);

		// byte[] MwPinkByte = Union3DesDecrypt(tmkByte, pinkByte);
		// String mwPinkByte = bytesToHexString(MwPinkByte);
		// Log.i("result", "---------MwPinkByte-----------"+mwPinkByte);

		byte[] pinResultByte = DoubleDesEncrypt(pinkByte, pinkBlackByte);

		// Log.i("result",
		// "---------pinResultByte-----------"+bytesToHexString(pinResultByte));
		// byte[] pinResultByte = DoubleDesEncrypt(MwPinkByte, pinkBlackByte);
		String pinResult = bytesToHexString(pinResultByte).toUpperCase();
		return pinResult;
	}

	/**
	 * 
	 * @param bankNum
	 * @param pwd
	 * @return
	 */
	public static String pwdDecrypt(String bankNum, String pwd) {

		String bankNum12 = "0000"
				+ bankNum
						.substring(bankNum.length() - 13, bankNum.length() - 1);

		// String str = UnionDecryptData(pwd2, bankNum12);
		// byte[] tmkByte = str2Bcd(pwd);
		//
		// byte[] pinkByte = str2Bcd(bankNum);
		//
		// for (int i = 0; i < tmkByte.length; i++) {
		// // Log.i("result", "----------------pwd--s--------" + tmkByte[i]);
		// }
		// byte[] MwPinkByte = DoubleDesEncrypt(tmkByte, pinkByte);
		String str1 = siplt2HexStr(bankNum12);
		String str2 = siplt2HexStr(pwd);
		String mwPinkByte = yihuo(bankNum12, pwd);
		String mwPinkByte2 = yihuo(str1, str2);
		Log.i("result", "---------mwPinkByte----------" + mwPinkByte);
		Log.i("result", "---------mwPinkByte2----------" + mwPinkByte2);

		return mwPinkByte;

	}

	public static String siplt2HexStr(String hexStr) {
		StringBuilder hexs = new StringBuilder();
		String str = null;
		int i = 0;
		while (true) {
			if ((i + 1) * 2 > hexStr.length()) {
				break;
			}
			str = hexStr.substring((i * 2), (i + 1) * 2);
			hexs.append(str).append(" ");
			i++;
		}
		hexs.deleteCharAt(hexs.length() - 1);
		return hexs.toString();
	}

	public static byte[] getByte(String ss) {
		String[] shil = ss.split(" ");
		StringBuilder er = new StringBuilder();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		for (String string : shil) {
			if ("".equals(string)) {
				continue;
			}
			int i = Integer.valueOf(string, 16);
			out.write(i);
		}
		return out.toByteArray();
	}

	/**
	 * 异或处理
	 * 
	 * @param data1
	 *            格式7100000100000000 不带空格
	 * @param data2
	 * @return
	 */
	public static String yihuo(String data1, String data2) {
		StringBuilder sb = new StringBuilder();

		String dataString1 = ByteUtils.siplt2HexStr(data1);
		byte[] Dataarray1 = ByteUtils.getByte(dataString1);

		String dataString2 = ByteUtils.siplt2HexStr(data2);
		byte[] Dataarray2 = ByteUtils.getByte(dataString2);

		for (int i = 0; i < Dataarray1.length; i++) {
			String result = Integer.toHexString(Dataarray1[i] ^ Dataarray2[i]);
			if (result.length() == 1) {
				sb.append("0").append(result);
			} else if (result.length() == 8) {
				sb.append(result.substring(6));
			} else {
				sb.append(result);
			}
		}
		return sb.toString();
	}

	/**
	 * 加密
	 * 
	 * @param key
	 * @param data
	 * @return
	 */
	public static byte[] Union3DesEncrypt(byte[] key, byte[] data) {
		try {
			byte[] k = new byte[24];

			int len = data.length;
			if (data.length % 8 != 0) {
				len = data.length - data.length % 8 + 8;
			}
			byte[] needData = null;
			if (len != 0) {
				needData = new byte[len];
			}
			for (int i = 0; i < len; i++) {
				needData[i] = 0;
			}

			System.arraycopy(data, 0, needData, 0, data.length);

			if (key.length == 16) {
				System.arraycopy(key, 0, k, 0, key.length);
				System.arraycopy(key, 0, k, 16, 8);
			} else {
				System.arraycopy(key, 0, k, 0, 24);
			}

			KeySpec ks = new DESedeKeySpec(k);
			SecretKeyFactory kf = SecretKeyFactory.getInstance("DESede");
			SecretKey ky = kf.generateSecret(ks);

			Cipher c = Cipher.getInstance(TriDes);
			c.init(1, ky);
			return c.doFinal(needData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] Union3DesDecrypt(byte[] key, byte[] data) {
		try {
			byte[] k = new byte[24];

			int len = data.length;
			if (data.length % 8 != 0) {
				len = data.length - data.length % 8 + 8;
			}
			byte[] needData = null;
			if (len != 0) {
				needData = new byte[len];
			}
			for (int i = 0; i < len; i++) {
				needData[i] = 0;
			}

			System.arraycopy(data, 0, needData, 0, data.length);

			if (key.length == 16) {
				System.arraycopy(key, 0, k, 0, key.length);
				System.arraycopy(key, 0, k, 16, 8);
			} else {
				System.arraycopy(key, 0, k, 0, 24);
			}
			KeySpec ks = new DESedeKeySpec(k);
			SecretKeyFactory kf = SecretKeyFactory.getInstance("DESede");
			SecretKey ky = kf.generateSecret(ks);

			Cipher c = Cipher.getInstance(TriDes);
			c.init(2, ky);
			return c.doFinal(needData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] UnionDesDecrypt(byte[] key, byte[] data) {
		try {
			KeySpec ks = new DESKeySpec(key);
			SecretKeyFactory kf = SecretKeyFactory.getInstance("DES");
			SecretKey ky = kf.generateSecret(ks);

			Cipher c = Cipher.getInstance(DES);
			c.init(2, ky);
			return c.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] UnionDesEncrypt(byte[] key, byte[] data) {
		try {
			KeySpec ks = new DESKeySpec(key);
			SecretKeyFactory kf = SecretKeyFactory.getInstance("DES");
			SecretKey ky = kf.generateSecret(ks);

			Cipher c = Cipher.getInstance(DES);
			c.init(1, ky);
			return c.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] DoubleDesEncrypt(byte[] key, byte[] data) {
		byte[] key1 = new byte[8];
		byte[] key2 = new byte[8];

		for (int i = 0; i < key.length; i++) {
			if (i < 8)
				key1[i] = key[i];
			else {
				key2[(i - 8)] = key[i];
			}
		}

		byte[] result = new byte[data.length];

		result = UnionDesEncrypt(key1, data);

		result = UnionDesDecrypt(key2, result);

		result = UnionDesEncrypt(key1, result);

		return result;
	}

	public static byte[] getByteNot(byte[] args) {
		byte[] result = new byte[args.length];

		for (int i = 0; i < args.length; i++) {
			result[i] = ((byte) (args[i] ^ 0xFFFFFFFF));
		}
		return result;
	}

	public static String GenRandomKey(String key, String factor) {
		byte[] keyByte = str2Bcd(key);
		byte[] factorByte = str2Bcd(factor);

		byte[] temp1 = DoubleDesEncrypt(keyByte, factorByte);

		byte[] factorByteTemp = getByteNot(factorByte);

		byte[] temp2 = DoubleDesEncrypt(keyByte, factorByteTemp);

		String result = "";

		result = bytesToHexString(temp1) + bytesToHexString(temp2);
		return result;
	}

	/**
	 * 解密磁道
	 * 
	 * @param key
	 * @param data
	 * @return
	 */
	public static String UnionDecryptData(String key, String data) {
		if ((key.length() != 16) && (key.length() != 32)
				&& (key.length() != 48)) {
			return "";
		}
		if (data.length() % 16 != 0) {
			return "";
		}
		int lenOfKey = 0;
		lenOfKey = key.length();
		String strEncrypt = "";
		byte[] sourData = str2Bcd(data);
		switch (lenOfKey) {
		case 16:
			byte[] deskey8 = str2Bcd(key);
			byte[] encrypt = UnionDesDecrypt(deskey8, sourData);
			strEncrypt = bytesToHexString(encrypt);
			break;
		case 32:
		case 48:
			String newkey1 = "";
			if (lenOfKey == 32) {
				String newkey = key.substring(0, 16);
				newkey1 = key + newkey;
			} else {
				newkey1 = key;
			}
			byte[] deskey24 = str2Bcd(newkey1);
			byte[] desEncrypt = Union3DesDecrypt(deskey24, sourData);
			strEncrypt = bytesToHexString(desEncrypt);
		}
		return strEncrypt;
	}

	/**
	 * 加密数据
	 * @param key
	 * @param data
	 * @return
	 */
	public static String UnionEncryptData(String key, String data) {
		if ((key.length() != 16) && (key.length() != 32)
				&& (key.length() != 48)) {
			return null;
		}
		if (data.length() % 16 != 0) {
			return "";
		}
		int lenOfKey = 0;
		lenOfKey = key.length();
		String strEncrypt = "";
		byte[] sourData = str2Bcd(data);
		switch (lenOfKey) {
		case 16:
			byte[] deskey8 = str2Bcd(key);
			byte[] encrypt = UnionDesEncrypt(deskey8, sourData);
			strEncrypt = bytesToHexString(encrypt).toUpperCase();
			break;
		case 32:
		case 48:
			String newkey1 = "";
			if (lenOfKey == 32) {
				String newkey = key.substring(0, 16);
				newkey1 = key + newkey;
			} else {
				newkey1 = key;
			}
			byte[] deskey24 = str2Bcd(newkey1);
			byte[] desEncrypt = Union3DesEncrypt(deskey24, sourData);
			strEncrypt = bytesToHexString(desEncrypt).toUpperCase();
		}
		return strEncrypt;
	}
	

	public static String pinKeyDecrypt(String zmk, String pinKey) {
		byte[] tmkByte = str2Bcd(zmk);
		byte[] pinkByte = str2Bcd(pinKey);

		byte[] MwPinkByte = Union3DesDecrypt(tmkByte, pinkByte);

		String pinKeyResult = bytesToHexString(MwPinkByte).toUpperCase();
		return pinKeyResult;
	}

	public static String UnionDecrypt(String T2Len) {
		int t2len = Integer.parseInt(T2Len);
		while (t2len % 16 != 0) {
			t2len++;
		}
		String resultHex = String.valueOf(t2len);
		return resultHex;
	}

	public static String trackInf(String trackInf, String TLen) {
		int TLen1 = Integer.parseInt(TLen);
		return trackInf.substring(0, TLen1);
	}

	public static void main(String[] args) {
		String zmk = "945C0D8AD325B94A4AFD892C30C9BED2";
		String pinKey = "56F7B7C8059A3D1B6147CC4378CD15A6";
		String account = "";
		String passwd = "950901";
		System.out.println("pinBlack=========" + PinEncrypt(account, passwd));
		System.out.println("pinResult========"
				+ pinResultMak(zmk, pinKey, account, passwd));

		String key = "000102030405060708090a0b0c0d0e0f";
		String factor = "26D73E4800000002";
		String data = "7977C8563FCC3D18F8DF24221902A19845B6B14F5B17E38F837A1EA24625C340A7BAA7301AB5DE6E3AF763784A78B5C7ABE377474E4EADAEC57C827464E12D1CB26C093A0A05BEAAFC063A203537978B";

		String result2 = GenRandomKey(key, factor);

		String result1 = UnionDecryptData(result2, data);
		System.out.println("解密出来的磁道信息是�?" + result1.toUpperCase());

		String result3 = UnionEncryptData(result2, result1);
		System.out.println("加密出来的磁道信息是�?" + result3.toUpperCase());

		String zmk1 = "3016745AB289EFCDBADCFE0325476981";
		String pink = "2C21AE1E4107DFB05A2F3AE37DDEE686";
		System.out.println(pinKeyDecrypt(zmk1, pink));

		System.out.println("计算出来�?近的16整数倍：" + UnionDecrypt("24"));

		System.out
				.println("截取数�?�："
						+ trackInf(
								"6222021001142793501D49121202679991612FFFFFFFFFFF996222021001142793501D1561560000000000001003267999010000049120D000000000000D000000000000D00000000FFFFFFFFFFFFFFF",
								"37"));
	}

}
