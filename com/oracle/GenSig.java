package com.oracle;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;

import java.security.SecureRandom;
import java.security.KeyPairGenerator;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;

/* This is the main module that creates the detached digital signature */
public class GenSig
{
    public static void Gen_sig(String file,String dest) throws Exception
    {	   
            
            /* Create a Signature object and initialize it with the private key */
            Signature rsa = Signature.getInstance("SHA1withRSA", "SunRsaSign");
            FileInputStream keyfis = new FileInputStream("KEYS/privkey"); //import encoded private key
            byte[] encKey = new byte[keyfis.available()];  
            keyfis.read(encKey);
            keyfis.close();
            PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(encKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA", "SunRsaSign");
            // Generate private key object from the provided key specification(key material)
            PrivateKey priv = keyFactory.generatePrivate(privKeySpec); 
            rsa.initSign(priv); 
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bufin = new BufferedInputStream(fis);
            byte[] buffer = new byte[3072];
            int len;
            while (bufin.available() != 0) //update input file
            {
                len = bufin.read(buffer);
                rsa.update(buffer, 0, len);
            }
            bufin.close();
            byte[] realSig = rsa.sign(); //Now that all the data to be signed has been read in,generate a signature for it     
            FileOutputStream sigfos = new FileOutputStream(dest+"/sig"); //create 'sig' file
            sigfos.write(realSig);//write the signature to 'sig' file
            sigfos.close();            	  
    }
}
/*		***		LIBRARY OVERVIEW	***	                              		*/

/*Signature: Used to provide applications the functionality of a digital signature algorithm
  Class Details: https://docs.oracle.com/javase/7/docs/api/java/security/Signature.html
  
  PKCS8EncodedKeySpec: This class represents the ASN.1 encoding of a private key, 
  encoded according to the ASN.1 type PrivateKeyInfo
  Class Details: https://docs.oracle.com/javase/7/docs/api/java/security/spec/PKCS8EncodedKeySpec.html
  
  BufferedInputStream: A BufferedInputStream adds functionality to another input stream
  Class Details: docs.oracle.com/javase/7/docs/api/java/io/BufferedInputStream.html
*/