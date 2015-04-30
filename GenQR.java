import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.oracle.GenSig;
import edu.sxccal.utilities.ZipCreator;
import edu.sxccal.utilities.ImgtoBlackandWhite;
import edu.sxccal.utilities.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/*                                   Main QRCode generation module.                                  */

//Compresses input file, 'sig' and 'pubkey' files(generated by GenKeys and com.oracle.GenSig)
//using edu.sxccal.utitlities.ZipCreator
//to Result.zip. Result.zip is then converted into a string which is encoded using ISO-8859-1 standard
//The string is then encoded in QRCode.png using com.goggle.zxing libraries

class QRCode 
{	
  public static String gen_qrcode(String[] args) throws Exception
  {		
    //ISO-8859-1 is used to encode bytes read from the input file, args[1] is the directory path of the QR image
    String charset = "ISO-8859-1", file = args[1]+"/QRCode.png", data = args[0]; 
    data=read_from_file(data, charset);  //if this exceeds ~2.9KB then Exception will occur    
    Map<EncodeHintType, ErrorCorrectionLevel> hint_map1 = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
    hint_map1.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L); //hints used by QRCodeWriter.encode for efficient generation of the QRCode image
    createQRCode(data, file,hint_map1,500,500);
    String s="ISO-8859-1 encoded data size of result.zip: "+data.length()+" bytes\nQR Code image created successfully!";
    return s;
  }	
  public static String read_from_file(String s, String charset) throws IOException
  {
    FileInputStream fp=new FileInputStream(s);
    int c;
    String data="";
    while((c=fp.read())!=-1)
      data+=(char)c; //convert the data read from file to a string
    data = new String(data.getBytes(), charset);
    fp.close();
    return data;
  }	
  public static void createQRCode(String data, String file, Map<EncodeHintType, ErrorCorrectionLevel> hint_map, int qrh, int qrw) throws WriterException, IOException
  {		
    BitMatrix matrix = new QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, qrw, qrh, hint_map); //Zxing libraries--> BitMatrix, MatrixToImageWriter
    MatrixToImageWriter.writeToFile(matrix, "png",new File(file));
  }	
}

class GenQR
{
  public static boolean is_image(String s)
  {
    String ext=s.substring(s.lastIndexOf('.')+1,s.length()); //get file extension
    if(ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg") || ext.equalsIgnoreCase("png"))
      return true;
    return false;
  }
  public static void main(String args[]) throws Exception //args[0]-> input file directory, args[1]-> output directory
  {
    Process p1=null;
    try
    {     
      if(args[0].isEmpty() || args[1].isEmpty())
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
      boolean flag;
      if((flag=is_image(args[0])))
      {
	//if the input file is an image convert it to black and white image
      	args[0]=ImgtoBlackandWhite.toBW(args[0],filePath);
      	file=new File(args[0]);
      	result="Black and White image is: "+args[0]+"\n";
      }      
      if(file.length()>3000) //File size must be less than 3KB
      {
	p1.destroy();
	if(flag) //if B&W image is too large delete it
	  file.delete();
	throw new IOException("File too large!");	
      }
      GenSig.Gen_sig(args[0],filePath);
      String pub="KEYS/pubkey",sign=filePath+"/sig",zipin=filePath+"/result.zip";      
      String files[]={sign,pub,args[0]};
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
      p1.destroy();
      String s=Log.create_log(e);
      String[] x={"zenity","--error","--text="+s};
      p1=new ProcessBuilder(x).start(); //Show error window
      p1.waitFor();     
    }    
  }
}

/*		***		LIBRARY OVERVIEW	***	                              		   */

/*MatrixToImageWriter: Writes a BitMatrix to BufferedImage, file or stream.
  Class Details: http://zxing.github.io/zxing/apidocs/com/google/zxing/client/j2se/MatrixToImageWriter.html

  BitMatrix: Represents a 2D matrix of bits.  
  Internally the bits are represented in a 1-D array of 32-bit ints. 
  The ordering of bits is row-major.
  Class Details: http://zxing.github.io/zxing/apidocs/com/google/zxing/common/BitMatrix.html

  QRCodeWriter: Renders a QR Code as a BitMatrix 2D array of greyscale values.
  Class Details: http://zxing.github.io/zxing/apidocs/com/google/zxing/qrcode/QRCodeWriter.html

  EncodeHintType: These are a set of hints that you may pass to QRCodeWriter to specify its behavior.
  Class Details: http://zxing.github.io/zxing/apidocs/com/google/zxing/EncodeHintType.html
  
  BarcodeFormat: Specifies the barcode format(Ex: QR_CODE, AZTEC etc)
  Class Details: http://zxing.github.io/zxing/apidocs/com/google/zxing/BarcodeFormat.html

  ErrorCorrectionLevel: This enum encapsulates the four error correction levels defined by the QR code standard.
  Class Details: http://zxing.github.io/zxing/apidocs/com/google/zxing/qrcode/decoder/ErrorCorrectionLevel.html

  HashMap: Hash table based implementation of the Map interface. 
  Class Details: http://docs.oracle.com/javase/7/docs/api/java/util/HashMap.html

  Map: Maps keys to values. A map cannot contain duplicate keys; each key can map to at most one value. 
  Class Details: http://docs.oracle.com/javase/7/docs/api/java/util/Map.html
  
  Runtime: Every Java application has a single instance of class Runtime that allows the application to interface
  with the environment in which the application is running. 
  The current runtime can be obtained from the getRuntime method.
  An application cannot create its own instance of this class.
  Class Details: http://docs.oracle.com/javase/7/docs/api/java/lang/Runtime.html
  
  ProcessBuilder: This class is used to create operating system processes. 
  Class Details: http://docs.oracle.com/javase/7/docs/api/java/lang/ProcessBuilder.html
  
  Process: Runtime.exec create a native process and return an instance of a subclass of Process 
  that can be used to control the process and obtain information about it
  Class Details: http://docs.oracle.com/javase/7/docs/api/java/lang/Process.html
*/
