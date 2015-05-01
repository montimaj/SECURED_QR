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

//Verifies input file
//Functionality is same to that of the PC module com.oracle.VerSig
public class VerSig 
{
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

/*								***		LIBRARY OVERVIEW	***	                              		*/

/*X509EncodedKeySpec: Represents the ASN.1 encoding of a public key
 ASN.1 Details: http://en.wikipedia.org/wiki/Abstract_Syntax_Notation_One
 Class Details: https://docs.oracle.com/javase/7/docs/api/java/security/spec/X509EncodedKeySpec.html
 
 KeyFactory: Used to convert keys (opaque cryptographic keys of type Key) 
 into key specifications (transparent representations of the underlying key material), and vice versa. 
 Class Details: https://docs.oracle.com/javase/7/docs/api/java/security/KeyFactory.html
*/
 
 
