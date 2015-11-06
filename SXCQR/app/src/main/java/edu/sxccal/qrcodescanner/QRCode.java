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

/**
 * Main activity
 */

public class QRCode extends Activity implements OnClickListener
{
	private Button scanBtn,ver,ab;
	private String scanContent="No result";
	/**
	 * The directory where the generated files are to be kept
	 */
	public static final String filePath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/QR";

	@Override
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

	@Override
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

	@Override
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

	/**
	 * Display a {@link android.widget.Toast}
	 * @param message Message to be shown
	 * @param length Length of the popup window
	 */
	public void raise_toast(String message, int length)
	{
		Toast toast = Toast.makeText(this, message, length);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	/**
	 * Checks if there is read and write access to device storage
	 * @return true if both RW is possible, false otherwise
	 */
	public boolean checkExternalMedia()
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

	/**
	 * Write result.zip to the specified external storage directory
	 * @see #filePath
	 */
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