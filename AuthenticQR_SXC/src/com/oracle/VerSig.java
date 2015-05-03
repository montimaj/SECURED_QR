package com.oracle;

import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.util.Base64;

import java.security.KeyFactory;
import java.security.Security;
import java.security.Signature;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Verifies whether the input file is authentic  
*/
public class VerSig 
{
    /**
     * Return verification result
     * @param sign Signature file
     * @param data Input file to be verified
     * @return true if authentic false otherwise
     * @throws Exception
     */
	public static boolean verify(String sign, String data) throws Exception
    {       
            FileInputStream keyfis = new FileInputStream("KEYS/pubkey.txt"); //import encoded public key
            byte[] encKey = new byte[keyfis.available()];  
            keyfis.read(encKey);
            keyfis.close();
            Security.insertProviderAt(new BouncyCastleProvider(), 1);
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