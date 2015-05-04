package edu.sxccal.qrcodescanner;

import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Create log.txt for Android OS
 */
public class Log
{
	/**
	 * Appends error messages to Log.txt and raises { @link android.widget.Toast } for Android OS
	 * @param e Exception object
	 * @param context Activity context where the { @link android.widget.Toast } will be shown
	 */
	public static void create_log(Exception e, Context context)
	{		
		try
		 {		 
			 String log=QRCode.filePath+"/log.txt"; //create log file
			 String s="Oops!\nErrors have been detected\nCheck: "+log;			 
			 Toast toast = Toast.makeText(context,s,Toast.LENGTH_SHORT);
			 toast.setGravity(Gravity.CENTER,0,0);
			 toast.show();		 
			 PrintWriter pw=new PrintWriter((new BufferedWriter(new FileWriter(log, true))));
			 pw.println("-> "+e.toString());			
			 pw.close();			 			 
		 }
		catch(Exception e2)
		{
			e2.printStackTrace();
		}
     }
}