package com.oracle;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.util.Base64;

import java.security.KeyFactory;
import java.security.Security;
import java.security.Signature;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Creates the detached digital signature 
*/
public class GenSig
{
    /**
     * Create sig encoded in {@link Base64} format
     * @param file Input file
     * @param dest Output directory to store 'sig' 
     * @throws Exception
     */
	public static void Gen_sig(String file,String dest) throws Exception
    {	   
            
            Security.insertProviderAt(new BouncyCastleProvider(), 1);
            /* Create a Signature object and initialize it with the private key */
            Signature ecdsa = Signature.getInstance("SHA256withECDSA","BC");
            FileInputStream keyfis = new FileInputStream("KEYS/privkey.txt"); //import encoded private key
            byte[] encKey = new byte[keyfis.available()];  
            keyfis.read(encKey);
            keyfis.close();            
            PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(encKey));
            KeyFactory keyFactory = KeyFactory.getInstance("ECDSA","BC");
            // Generate private key object from the provided key specification(key material)
            PrivateKey priv = keyFactory.generatePrivate(privKeySpec); 
            ecdsa.initSign(priv); 
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bufin = new BufferedInputStream(fis);
            byte[] buffer = new byte[256];
            int len;
            while (bufin.available() != 0) //update input file
            {
                len = bufin.read(buffer);
                ecdsa.update(buffer, 0, len);
            }
            bufin.close();
            byte[] realSig = ecdsa.sign(); //Now that all the data to be signed has been read in,generate a signature for it     
            FileOutputStream sigfos = new FileOutputStream(dest+"/sig"); //create 'sig' file
            sigfos.write(Base64.getEncoder().encode(realSig));//write the signature to 'sig' file
            sigfos.close();            	  
    }
}