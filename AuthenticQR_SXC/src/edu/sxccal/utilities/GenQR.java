package edu.sxccal.utilities;

import java.io.File;
import java.io.IOException;

import com.oracle.GenSig;
import org.apache.pdfbox.ExtractText;

/**
* QRCode generation module
* <p>
* Uses
* <p>
* {@link com.oracle.GenSig} to generate the 'sig' file
* <p>
* {@link edu.sxccal.utilities.ZipCreator} to create result.zip containing 'sig' and input file
* <p>
* {@link edu.sxccal.utilities.QRCode} to generate QRCode.png from result.zip
* <p>
* {@link edu.sxccal.utilities.Log} to create Log.txt in case of any Exception thrown
* @since 1.0
*/

public class GenQR
{  
	/**
	 * Checks whether the input file is a pdf file
	 * @param file Input file 
	 * @return true if pdf false otherwise
	 */
	private static boolean is_pdf(String file)
	{
		String ext=file.substring(file.lastIndexOf('.')+1, file.length());
		if(ext.equalsIgnoreCase("pdf"))
			return true;
		return false;		
	}
	/**
	  * @param args Input arguments where args[0] should be input file directory, args[1] the output directory
	  * @throws Exception 
	*/   
	public static void main(String args[]) throws Exception //args[0]-> input file directory, args[1]-> output directory
	{
	    Process p1=null;
	    try
	    {     
	      if(args.length<2 || args[0].isEmpty() || args[1].isEmpty())
	    	  throw new IOException("Invalid input!");      
	      File file=new File(args[0]);
	      if(!file.exists())
	    	  throw new IOException("Input file doesn't exist!");      
	      String[] x={"zenity","--progress","--pulsate","--no-cancel","--text=Generating Signature, QRCode..."};
	      p1=new ProcessBuilder(x).start();
	      String filePath = args[1]; 
	      File dir = new File(filePath);   
	      if(!dir.exists())      
	    	  dir.mkdir(); //create directory to store 'sig' and 'suepk' 
	      String result="";
	      if(is_pdf(args[0]))
	      {	    	  
	    	  String[] p={args[0], args[1]+"/out.txt"};
	    	  ExtractText.main(p); //extract text from pdf file
	    	  args[0]=args[1]+"/out.txt";
	    	  result="Text file generated from the pdf file: "+args[0]+"\n";	    			  
	      }
	      GenSig.Gen_sig(args[0],filePath);
	      String sign=filePath+"/sig",zipin=filePath+"/result.zip";      
	      String files[]={sign,args[0]};
	      ZipCreator.create_zip(zipin,files); //create result.zip
	      String f[]={zipin,filePath};     
	      result+=QRCode.gen_qrcode(f); //generate QRCode image       
	      String[] x1={"zenity","--info","--title=Result","--text="+result};
	      Process p2=new ProcessBuilder(x1).start(); //Display window to notify about successful generation
	      p1.destroy(); //Destroy progress process
	      p2.waitFor(); //Run process p2 until "OK" is pressed      
	      String[] x2={"xdg-open",args[1]+"/QRCode.png"};
	      p1=new ProcessBuilder(x2).start(); //Display QRCode image      
	      p1.waitFor();
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