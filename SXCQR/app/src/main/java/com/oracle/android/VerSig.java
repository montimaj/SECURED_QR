package com.oracle.android;

import java.io.FileInputStream;
import java.io.BufferedInputStream;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import edu.sxccal.qrcodescanner.Verify;

//Verifies input file
//Functionality is same to that of the PC module com.oracle.VerSig
public class VerSig 
{
    public static void verify(InputStream keyfis,String f1, String f2 ) throws Exception
    {
		byte[] encKey = new byte[keyfis.available()];  
		keyfis.read(encKey);
		keyfis.close();
		X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encKey);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA", "AndroidOpenSSL");
		PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
		FileInputStream sigfis = new FileInputStream(f1);
		byte[] sigToVerify = new byte[sigfis.available()]; 
		sigfis.read(sigToVerify);
		sigfis.close();
		Signature sig = Signature.getInstance("SHA1withRSA", "AndroidOpenSSL");
		sig.initVerify(pubKey);
		FileInputStream datafis = new FileInputStream(f2);
		BufferedInputStream bufin = new BufferedInputStream(datafis);
		byte[] buffer = new byte[3072];
		int len;
		while (bufin.available() != 0)
		{
			len = bufin.read(buffer);
		    sig.update(buffer, 0, len);
		}
		bufin.close();
		boolean verifies=sig.verify(sigToVerify);  	            
		Verify.tv.setText("Digital Signature verification result: "+verifies);		       		 
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
 
 
