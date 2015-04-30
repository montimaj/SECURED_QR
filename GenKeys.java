import java.io.FileOutputStream;
import java.io.File;

import edu.sxccal.utilities.Log;

import java.security.SecureRandom;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/* This module writes the public and private keys to files */
//If keys are already present then ignores the generation operation
public class GenKeys 
{
  
  public static void main(String args[]) throws Exception
  {
	    Process p1=null;
	    try
	    {	      
	      File dir=new File("KEYS");
	      if(!dir.exists())
		dir.mkdir();
	      File key1=new File("KEYS/pubkey"), key2=new File("KEYS/privkey"); 
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
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "SunRsaSign"); //Generate a key pair
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
		keyGen.initialize(3072, random); //initialize a 3072-bit key with a SecureRandom object
		KeyPair pair = keyGen.generateKeyPair(); //generate private and public key
		PrivateKey priv = pair.getPrivate();
		PublicKey pub = pair.getPublic();
		byte[] pubkey = pub.getEncoded(), privkey=priv.getEncoded();		
		  
		//Write public and private keys to files
		FileOutputStream keyfos = new FileOutputStream(key1); 
		keyfos.write(pubkey);
		keyfos.close();  	  
		keyfos=new FileOutputStream(key2);
		keyfos.write(privkey);
		keyfos.close();	
		String x1[]={"zenity","--info","--text=Keys have been written to "+dir.getAbsolutePath()};
		p1.destroy();
		p1=new ProcessBuilder(x1).start();
		p1.waitFor();
	      }
	    }
	    catch(Exception e)
	    {
	      String s=Log.create_log(e);
	      String[] x={"zenity","--error","--text="+s};
	      p1=new ProcessBuilder(x).start(); //Show error window
	      p1.waitFor();
	    }	    
  }
}

/*		***		LIBRARY OVERVIEW	***	                              		*/

/*SecureRandom: provides a cryptographically strong random number generator (RNG). 
  Class Details: https://docs.oracle.com/javase/7/docs/api/java/security/SecureRandom.html

  KeyPairGenerator: Generates pairs of public and private keys.
  KeyPairGenerator.initialize(int keysize, SecureRandom random): Initializes the key pair generator 
  for a certain keysize with the given source of randomness 
  Class Details: https://docs.oracle.com/javase/7/docs/api/java/security/KeyPairGenerator.html  

  KeyPair: Simple holder for a key pair (a public key and a private key).
  Class Details: https://docs.oracle.com/javase/7/docs/api/java/security/KeyPair.html
*/