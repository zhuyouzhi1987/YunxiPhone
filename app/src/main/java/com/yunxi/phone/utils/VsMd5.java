package com.yunxi.phone.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class VsMd5 {
	static char hexidgits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	// 返回32位md5加密秘文
	public static String md5(String s) {
		if (s == null)
			return null;
		try {
			// Create MD5 Hash
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();
			StringBuffer hexString = new StringBuffer();
			String temps = "";
			for (int i = 0; i < messageDigest.length; i++) {
				temps = Integer.toHexString(0xFF & messageDigest[i]);
				if (temps.length() == 1)
					hexString.append("0");// 保证2�?
				hexString.append(temps);
			}
			String getmd5 = hexString.toString();
			return getmd5;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return "";
	}

	public static String getRandom() {
		Random r = new Random();
		String s = "";
		for (int i = 0; i < 6; i++) {
			s += r.nextInt(10);
		}
		return s;
	}

	public static String getMD5(File file) {
		if (!file.isFile()) {
			return null;
		}
		FileInputStream fis = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			fis = new FileInputStream(file);
			byte[] buffer = new byte[2048];
			int length = -1;
			// long s=System.currentTimeMillis();

			while ((length = fis.read(buffer)) != -1) {
				md.update(buffer, 0, length);
			}
			byte[] b = md.digest();
			return byteToHexString(b);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();

			return null;
		} finally {
			try {
				fis.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private static String byteToHexString(byte[] temp) {
		String s;

		char str[] = new char[16 * 2];
		int k = 0;
		for (int i = 0; i < 16; i++) {
			byte byte0 = temp[i];
			str[k++] = hexidgits[byte0 >>> 4 & 0xf];
			str[k++] = hexidgits[byte0 & 0xf];
		}
		s = new String(str);
		return s;
	}
}
