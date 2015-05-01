package edu.sxccal.qrcodescanner;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class QRCode extends Activity implements OnClickListener
{
	private Button scanBtn,ver,ab;
	public static String scanContent="No result";
	public static final String filePath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/QR";

	protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);        
        //load the main activity layout
        setContentView(R.layout.activity_qr);        
        //Create directory 
        File dir=new File(filePath);
        if(!dir.exists())
        	dir.mkdir();        
        //Check which button is pressed
        scanBtn = (Button)findViewById(R.id.scan_button);
        ver=(Button)findViewById(R.id.ver_sig);
        ab=(Button)findViewById(R.id.ab);
        ab.setOnClickListener(this);    
        ver.setOnClickListener(this);         
        scanBtn.setOnClickListener(this);
    }	    
	public void onClick(View v)
	{
		if(v.getId()==R.id.scan_button)
		{			
			IntentIntegrator scanner = new IntentIntegrator(this); //Zxing android interface library 
			scanner.initiateScan(); //Requires BarcodeScanner app by Zxing to be installed in the phone			
		}	
		if(v.getId()==R.id.ab)
		{
			Intent about=new Intent(QRCode.this,About.class);
			startActivity(about);
		}
		if(v.getId()==R.id.ver_sig)
		{
			Intent verify= new Intent(QRCode.this,Verify.class);                               
        	startActivity(verify);      
		}
	}	
	public void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		if(intent!=null)
		{
			IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);		
			if (scanningResult != null)
			{
				scanContent = scanningResult.getContents();		
				if(checkExternalMedia())
				{
					write_to_file();	        			
					String zipin=filePath+"/result.zip";
					try
					{
						String files[]=Unzip.unzip(zipin, filePath);
						if(files[1].equals(""))
							raise_toast("Image not scanned properly! Try again", Toast.LENGTH_SHORT);
						else
							raise_toast("Decoded files are: \n" + files[0] + "\n" + files[1], Toast.LENGTH_LONG);
					}
					catch(Exception e)
					{
						Log.create_log(e,getApplicationContext());
					}
				}									
			}
			else
				raise_toast("Device doesn't support read/write", Toast.LENGTH_SHORT);
		}
		else
			raise_toast("No scan data received!", Toast.LENGTH_SHORT);
	}
	public void raise_toast(String message, int length)
	{
		Toast toast = Toast.makeText(this, message, length);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	public boolean checkExternalMedia() //checks if there is read and write access to device storage
	{
		    boolean readable = false;
		    boolean writeable = false;
		    String state = Environment.getExternalStorageState();
		    if (Environment.MEDIA_MOUNTED.equals(state)) 
		    	readable = writeable = true;
		    else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
		    {		        
		        readable = true;
		        writeable = false;
		    }
		    else
		    	readable = writeable = false;
		    return (readable && writeable);
	}		
	public void write_to_file()
	{			 			
		    File dir = new File (filePath);		    
		    File file = new File(dir, "/result.zip");		    
		    try 
		    {
		        FileOutputStream f = new FileOutputStream(file);
				byte[] data=Base64.decode(scanContent.getBytes(), Base64.DEFAULT);
		        f.write(data);
		        f.close();
		    } 
		    catch(IOException e)
		    {		    	
		    	Log.create_log(e, this); //Write logs to log.txt
		    }
	}	
}

/*                               *** LIBRARY OVERVIEW ***                                                              */

/*Button: Represents a push-button widget.
  Push-buttons can be pressed, or clicked, by the user to perform an action. 
  Class Details: http://developer.android.com/reference/android/widget/Button.html

  Activity: An activity is a single, focused thing that the user can do. Almost all activities interact with the user,
  so the Activity class takes care of creating a window for you in which you can place your UI with setContentView(View).
  There are two methods almost all subclasses of Activity will implement:
  onCreate(Bundle) is where you initialize your activity. 
  Most importantly, here you will usually call setContentView(int) with a layout resource defining your UI, and 
  using findViewById(int) to retrieve the widgets in that UI that you need to interact with programmatically.
  onPause() is where you deal with the user leaving your activity. 
  Class Details: http://developer.android.com/reference/android/app/Activity.html

  View: This class represents the basic building block for user interface components. 
  A View occupies a rectangular area on the screen and is responsible for drawing and event handling. 
  View is the base class for widgets, which are used to create interactive UI components (buttons, text fields, etc.). 
  Class Details: http://developer.android.com/reference/android/view/View.html

  Intent: An intent is an abstract description of an operation to be performed.
  It can be used with startActivity to launch an Activity.
  Class Details: http://developer.android.com/reference/android/content/Intent.html

  Environment: Provides access to environment variables. 
  Class Details: http://developer.android.com/reference/android/os/Environment.html
 
  View.OnClickListener: Interface definition for a callback to be invoked when a view is clicked. 
  Interface Details: http://developer.android.com/reference/android/view/View.OnClickListener.html

  Bundle: Generally used for passing data between various Activities of android
  Class Details: http://developer.android.com/reference/android/os/Bundle.html

  IntentIntegrator: A utility class which helps ease integration with Barcode Scanner. 
  This is a simple way to invoke barcode scanning and receive the result,
  without any need to integrate, modify, or learn the project's source code.
  Class Details: http://zxing.github.io/zxing/apidocs/com/google/zxing/integration/android/IntentIntegrator.html

  IntentResult: Encapsulates the result of a barcode scan invoked through IntentIntegrator
  Class Details: http://zxing.github.io/zxing/apidocs/com/google/zxing/integration/android/IntentResult.html

  Toast: A toast provides simple feedback about an operation in a small popup.
  Class details: http://developer.android.com/reference/android/widget/Toast.html 
*/