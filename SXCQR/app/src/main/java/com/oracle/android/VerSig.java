package com.oracle.android;

import android.util.Base64;

import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.InputStream;

import java.security.KeyFactory;
import java.security.Security;
import java.security.Signature;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import org.spongycastle.jce.provider.BouncyCastleProvider;

/**
 * Verifies input file for Android OS
*/
public class VerSig 
{

	/**
	 * Checks whether input file is authentic or not
	 * @param is Represents pubkey.txt
	 * @param sign Path to the 'sig' file
	 * @param data File to be verified
	 * @return true if authenticated, false otherwise
	 * @throws Exception
	 */
	public static boolean verify(InputStream is,String sign, String data ) throws Exception
	{
			byte[] encKey = new byte[is.available()];
			is.read(encKey);
			is.close();
			Security.insertProviderAt(new BouncyCastleProvider(),1);
			X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(Base64.decode(encKey,Base64.DEFAULT));
			KeyFactory keyFactory = KeyFactory.getInstance("ECDSA","SC");
			PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
			FileInputStream sigfis = new FileInputStream(sign);
			byte[] sigToVerify = new byte[sigfis.available()];
			sigfis.read(sigToVerify);
			sigfis.close();
			Signature sig = Signature.getInstance("SHA256withECDSA","SC");
			sig.initVerify(pubKey);
			FileInputStream datafis = new FileInputStream(data);
			BufferedInputStream bufin = new BufferedInputStream(datafis);
			byte[] buffer = new byte[256];
			int len;
			while (bufin.available() != 0) {
				len = bufin.read(buffer);
				sig.update(buffer, 0, len);
			}
			bufin.close();
			boolean verifies = sig.verify(Base64.decode(sigToVerify, Base64.DEFAULT));
			return verifies;
	}
}