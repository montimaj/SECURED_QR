package edu.sxccal.qrcodescanner;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.oracle.android.VerSig;

import java.io.InputStream;

/* This activity displays the verification result */

public class Verify extends Activity implements View.OnClickListener
{	
	private Button bt;
	public static TextView tv;
	private final int PICKFILE_RESULT_CODE = 1;
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_ver_sig);		
		bt=(Button)findViewById(R.id.verify_file);
		bt.setOnClickListener(this);
	}
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
	public void verify_data(String f2)
	{
		tv= (TextView)findViewById(R.id.file_verify);
		tv.setText("");
		try
		{
			//get absolute paths of the files
			String f1=QRCode.filePath + "/sig";
			AssetManager assetManager=getAssets();
			InputStream is=assetManager.open("pubkey");
			VerSig.verify(is,f1,f2 );
		}
		catch(Exception e)
		{
			Log.create_log(e, getApplicationContext());
		}
	}	
}
