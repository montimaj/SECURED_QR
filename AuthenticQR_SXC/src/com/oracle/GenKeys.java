package com.oracle;

import java.io.FileOutputStream;
import java.io.File;
import java.util.Base64;

import java.security.SecureRandom;
import java.security.Security;
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import edu.sxccal.utilities.Log;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.ECNamedCurveTable;

/**
 * Writes the public and private keys to files 
 * <p>
 * If keys are already present then ignores the generation operation
 * @see java.security
 *</p>
*/
public class GenKeys 
{
  
  /**
   * Creates pubkey.txt and privkey.txt
   * <p>
   * ECDSA is used as encryption algorithm and SHA256 as the hash algorithm
   * </p>
   * <p>
   * prime256v1 curve is used
   * </p> 
   * @param args
   * @throws Exception
   */
	public static void main(String args[]) throws Exception
	{
	    Process p1=null;
	    try
	    {	      
	      File dir=new File("KEYS");
	      if(!dir.exists())
	    	  dir.mkdir();
	      File key1=new File("KEYS/pubkey.txt"), key2=new File("KEYS/privkey.txt"); 
	      if(key1.exists() || key2.exists())
	      {		
	    	  String[] x={"zenity","--info","--text=Keys have already been generated!\nTo regenerate keys delete previous keys in the KEYS directory"};
	    	  p1=new ProcessBuilder(x).start();
	    	  p1.waitFor();
	      }
	      else
	      {
			  String[] x={"zenity","--progress","--pulsate","--no-cancel","--text=Generating Keys..."};
			  p1=new ProcessBuilder(x).start();
			  Security.insertProviderAt(new BouncyCastleProvider(), 1);
			  ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("prime256v1");
			  KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC"); //Generate a key pair 		
			  keyGen.initialize(ecSpec, new SecureRandom()); //initialize a 256-bit key with a SecureRandom object
			  KeyPair pair = keyGen.generateKeyPair(); //generate private and public key
			  PrivateKey priv = pair.getPrivate();
			  PublicKey pub = pair.getPublic();
			  byte[] b64pubkey=Base64.getEncoder().encode(pub.getEncoded()), b64privkey=Base64.getEncoder().encode(priv.getEncoded()); 
			  //Write Base64 encoded public and private keys to files
			  FileOutputStream keyfos = new FileOutputStream(key1); 
			  keyfos.write(b64pubkey);
			  keyfos.close();  	  
			  keyfos=new FileOutputStream(key2);
			  keyfos.write(b64privkey);
			  keyfos.close();	
			  String x1[]={"zenity","--info","--text=Keys have been written to "+dir.getAbsolutePath()};
			  p1.destroy();
			  p1=new ProcessBuilder(x1).start();
			  p1.waitFor();		    
	      }
	    }
	    catch(Exception e)
	    {
		    if(p1!=null)
		    	p1.destroy();
	    	String s=Log.create_log(e);
		    String[] x={"zenity","--error","--text="+s};
		    p1=new ProcessBuilder(x).start(); //Show error window
		    p1.waitFor();
	    }   
  }
}