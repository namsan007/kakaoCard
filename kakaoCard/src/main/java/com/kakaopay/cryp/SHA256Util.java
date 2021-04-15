package com.kakaopay.cryp;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.context.annotation.Configuration;

import com.kakaopay.exception.CrypExcn;

import lombok.extern.slf4j.Slf4j;

/**
 * 암호화 모듈
 *
 * @author kjy
 * @since Create : 2021. 4. 15
 * @version 1.0
 */
@Configuration
@Slf4j
public class SHA256Util {
	private final static String KEYNAME = "nfaator!plaeemo!";
	private final static String ALGORITHM = "AES";
	public static final String AES_ECB_NOPADDING = "AES/ECB/NoPadding";

	public static String encrypt(String source) throws CrypExcn {
		byte[] eArr = null;
		try {
			eArr = null;
			SecretKeySpec skeySpec = new SecretKeySpec(KEYNAME.getBytes(), ALGORITHM);
			Cipher cipher = Cipher.getInstance(AES_ECB_NOPADDING);
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			eArr = cipher.doFinal(addPadding(source.getBytes()));

		} catch (Exception e) {
	    	log.error("암호화 에러({}) error :: {}",  e.getMessage());
		}
		return fromHex(eArr);
	}
	public static String decrypt(final String source) throws CrypExcn {
		byte[] eArr = null;
		try {
			Cipher cipher = Cipher.getInstance(AES_ECB_NOPADDING);
			SecretKeySpec skeySpec = new SecretKeySpec(KEYNAME.getBytes(), ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			eArr = removePadding(cipher.doFinal(toBytes(source)));
		} catch (Exception e) {
	    	log.error("복호화 에러({}) error :: {}",  e.getMessage());
	    	
		}
		return new String(eArr);
	}

	private static byte[] toBytes(final String pSource) {
		StringBuffer buff = new StringBuffer(pSource);
		int bCount = buff.length() / 2;
		byte[] bArr = new byte[bCount];
		for (int bIndex = 0; bIndex < bCount; bIndex++) {
			bArr[bIndex] = (byte) Long.parseLong(buff.substring(2 * bIndex, (2 * bIndex) + 2), 16);
		}
		return bArr;
	}

	private static byte[] removePadding(final byte[] pBytes) {
		int pCount = pBytes.length;
		int index = 0;
		boolean loop = true;
		while (loop) {
			if (index == pCount || pBytes[index] == 0x00) {
				loop = false;
				index--;
			}
			index++;
		}
		byte[] tBytes = new byte[index];
		System.arraycopy(pBytes, 0, tBytes, 0, index);
		return tBytes;
	}

	private static byte[] addPadding(final byte[] pBytes) {
		int pCount = pBytes.length;
		int tCount = pCount + (16 - (pCount % 16));
		byte[] tBytes = new byte[tCount];
		System.arraycopy(pBytes, 0, tBytes, 0, pCount);
		for (int rIndex = pCount; rIndex < tCount; rIndex++) {
			tBytes[rIndex] = 0x00;
		}
		return tBytes;
	}

	public static String fromHex(byte[] pBytes) {
		int pCount = pBytes.length;
		StringBuffer buff = new StringBuffer(pCount * 2);
		for (int pIndex = 0; pIndex < pCount; pIndex++) {
			if (((int) pBytes[pIndex] & 0xff) < 0x10) {
				buff.append(0);
			}
			buff.append(Long.toString((int) pBytes[pIndex] & 0xff, 16));
		}
		return buff.toString();
	}
}
