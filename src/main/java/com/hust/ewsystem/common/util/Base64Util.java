package com.hust.ewsystem.common.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Base64工具类
 */
public class Base64Util {

	/**
	 * 对给定的字符串进行base64解码操作
	 * @param inputData
	 * @return String
	 */
	public static String decodeToString(String inputData) {
		if (null == inputData) {
			return null;
		}
		return new String(Base64.getMimeDecoder().decode(inputData), StandardCharsets.UTF_8);
	}
	
	/**
	 * 对给定的二进制数组进行base64解码操作
	 * @param pArray
	 * @return byte[]
	 */
	public static byte[] decodeToByte(byte[] pArray) {
		return Base64.getMimeDecoder().decode(pArray);
	}
	
	/**
	 * 对给定的字符串进行base64解码操作
	 * @param inputData
	 * @return byte[]
	 */
	public static byte[] decodeToByte(String inputData) {
		if(inputData == null) {
			return null;
		}
		return Base64.getMimeDecoder().decode(inputData.getBytes());
	}
	
	/**
	 * 对给定的字符串进行base64解码操作
	 * @param inputData 字符串
	 * @param encode 编码格式
	 * @return utf-8格式字符串
	 */
	public static byte[] decodeToByte(String inputData, String encode) {
		if (null == inputData) {
			return null;
		}
		String encodeString = new String(Base64.getMimeDecoder().decode(inputData), Charset.forName(encode));
		return encodeString.getBytes(StandardCharsets.UTF_8);
	}
	
	
	/**
	 * 对给定的字符串进行base64解码操作
	 * @param encodeData
	 * @return String
	 */
	public static String decodeToString(byte[] encodeData) {
		return new String(Base64.getMimeDecoder().decode(encodeData), StandardCharsets.UTF_8);
	}
	
	/**
	 * 对给定的二进制数组进行base64加密操作
	 * @param pArray
	 * @return String
	 */
	public static String encodeToString(byte[] pArray) {
		return encodeToString(pArray, false);
	}
	
	/**
	 * 对给定的二进制数组进行base64加密操作
	 * @param pArray
	 * @param isChunked false不支持Mime,true支持Mime
	 * @return String
	 */
	public static String encodeToString(byte[] pArray, boolean isChunked) {
		String str = null;
		if(isChunked) {
			str = Base64.getMimeEncoder().encodeToString(pArray);
		} else {
			str = Base64.getEncoder().encodeToString(pArray);
		}
		return str;
	}

}
