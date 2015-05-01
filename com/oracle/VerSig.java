package com.oracle;

import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.util.Base64;

import java.security.KeyFactory;
import java.security.Signature;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/* This is the main module that verifies whether the input file is authentic  */
public class VerSig 
{
    public static boolean verify(String sign, String data) throws Exception
    {       
            FileInputStream keyfis = new FileInputStream("KEYS/pubkey.txt"); //import encoded public key
            byte[] encKey = new byte[keyfis.available()];  
            keyfis.read(encKey);
            keyfis.close();
            java.security.Security.addProvider(new BouncyCastleProvider());
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(encKey));
            KeyFactory keyFactory = KeyFactory.getInstance("ECDSA","BC");
            // Generate a public key object from the provided key specification(key material)
            PublicKey pubKey = keyFactory.generatePublic(pubKeySpec); 
            FileInputStream sigfis = new FileInputStream(sign); //import the signature bytes
            byte[] sigToVerify = new byte[sigfis.available()]; 
            sigfis.read(sigToVerify);
            sigfis.close();
            Signature sig = Signature.getInstance("SHA256withECDSA","BC"); //create a signature object 
            sig.initVerify(pubKey); //sign it with the public key
            
            //update and verify the input file
            FileInputStream datafis = new FileInputStream(data); 
            BufferedInputStream bufin = new BufferedInputStream(datafis);
            byte[] buffer = new byte[256];
            int len;
            while (bufin.available() != 0)
            {
                len = bufin.read(buffer);
                sig.update(buffer, 0, len);
            }
            bufin.close();
            boolean verifies = sig.verify(Base64.getDecoder().decode(sigToVerify)); 
            return verifies; //return verification result       
    }    
}

/*		***		LIBRARY OVERVIEW	***	                              		*/

/*X509EncodedKeySpec: Represents the ASN.1 encoding of a public key
 ASN.1 Details: http://en.wikipedia.org/wiki/Abstract_Syntax_Notation_One
 Class Details: https://docs.oracle.com/javase/7/docs/api/java/security/spec/X509EncodedKeySpec.html
 
 KeyFactory: Used to convert keys (opaque cryptographic keys of type Key) 
 into key specifications (transparent representations of the underlying key material), and vice versa. 
 Class Details: https://docs.oracle.com/javase/7/docs/api/java/security/KeyFactory.html
*/