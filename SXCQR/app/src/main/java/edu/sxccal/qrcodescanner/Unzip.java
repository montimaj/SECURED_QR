package edu.sxccal.qrcodescanner;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Extract zip file
*/
public class Unzip
{    
    private static final int BUFFER_SIZE = 4096;

	/**
	 * Extract entries in the zip file
	 * @param zipFilePath input zip file
	 * @param destDirectory output directory where the extracted files will be stored
	 * @return array of String containing filePaths of the extracted files
	 * @throws IOException
	 */
	public static String[] unzip(String zipFilePath, String destDirectory) throws IOException
    {
    	String f1=destDirectory + "/sig";
    	String f2="";
    	ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
	    ZipEntry entry = zipIn.getNextEntry();    	        
	    while (entry != null) 
	    {
	    	String ent=entry.getName(),filePath = destDirectory + "/" + ent;
	        boolean flag=true;
	        if(ent.equals("sig"))
	        	flag=false;
	        if (!entry.isDirectory()) 
	        {                
	        	if(flag)	             
	        		f2=filePath;
	            extractFile(zipIn, filePath);
	        } 
	        else 
	        {                
	        	File dir = new File(filePath);
	            dir.mkdir();
	        }
	        zipIn.closeEntry();
	        entry = zipIn.getNextEntry();
	    }
	    zipIn.close();                
    	String files[]={f1,f2};
		File zfile=new File(zipFilePath);
		zfile.delete();
        return files;
    }

	/**
	 * Writes extracted files to output directory
	 * @param zipIn input zip file
	 * @param filePath output directory
	 * @throws IOException
	 */
    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException
    {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1)
        {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }    
}