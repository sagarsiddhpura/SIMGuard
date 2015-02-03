package com.siddworks.simguard.util;

import android.os.Environment;
import android.os.SystemClock;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Log {

	static int count = 0;
	static String message = "";
	static boolean isGlobalLoggingEnabled = false;
	static boolean isLogToFileEnabled = false;
	
	public static void d(boolean loggingEnabled, String tag, String message)
	{
		if(loggingEnabled || isGlobalLoggingEnabled)
		{
			logToFile(tag, message);
			android.util.Log.d(tag, message);
		}
	}
	
	public static void i(boolean loggingEnabled, String tag, String message)
	{
		if(loggingEnabled || isGlobalLoggingEnabled)
		{
			logToFile(tag, message);
			android.util.Log.i(tag, message);
		}
	}
	
	public static void e(boolean loggingEnabled, String tag, String message)
	{
		if(loggingEnabled || isGlobalLoggingEnabled)
		{
			logToFile(tag, message);
			android.util.Log.e(tag, message);
		}
	}

	private static void logToFile(String tag, String message2) {
		if(isLogToFileEnabled)
		{
			if(count > 50)
			{
				 try {
					 File file = new File(Environment.getExternalStoragePublicDirectory(
					            Environment.DIRECTORY_DOWNLOADS), "DriveThroughLog.txt");
			            FileWriter out = new FileWriter(file, true);
			            out.write(message+"\n");
			            out.close();
			        } catch (IOException e) {
			        }
				count = 0;
				message = "";
			}
			else
			{
				count ++;
				message += SystemClock.elapsedRealtime() + " : " + tag+" : "+message2+"\n";
			}
		}
	}
}
