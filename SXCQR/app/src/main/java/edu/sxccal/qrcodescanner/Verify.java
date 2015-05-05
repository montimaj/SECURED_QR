package edu.sxccal.qrcodescanner;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.oracle.android.VerSig;

import java.io.InputStream;

/**
 *  This activity displays the verification result
 */

public class Verify extends Activity implements View.OnClickListener
{	
	private Button bt;
	private final int PICKFILE_RESULT_CODE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_ver_sig);		
		bt=(Button)findViewById(R.id.verify_file);
		bt.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
        fileintent.setType("file/*");
        try 
        {
            startActivityForResult(fileintent,PICKFILE_RESULT_CODE);            
        } 
        catch (Exception e) 
        {
            Log.create_log(e, getApplicationContext());
        }	
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{		  
		  switch(requestCode)
		  {
			  case PICKFILE_RESULT_CODE:
			   if(resultCode==RESULT_OK)
			   {
				    String f = data.getData().getPath();
				    verify_data(f);
			   }
			   break;
		  }
	}

	/**
	 * Displays verification result as a {@link android.widget.Toast}
	 * @param f2 File to be verified
	 */
	public void verify_data(String f2)
	{
		try
		{
			//get absolute paths of the files
			String f1=QRCode.filePath + "/sig";
			AssetManager assetManager=getAssets();
			InputStream is=assetManager.open("pubkey.txt");
			boolean result=VerSig.verify(is,f1,f2);
			Toast toast = Toast.makeText(this,"Digital Signature verification result: " + result, Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
		catch(Exception e)
		{
			Log.create_log(e, this);
		}
	}
}