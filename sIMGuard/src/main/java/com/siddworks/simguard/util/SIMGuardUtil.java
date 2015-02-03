package com.siddworks.simguard.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.SmsManager;

import com.siddworks.simguard.DashboardActivity;
import com.siddworks.simguard.R;

import java.util.ArrayList;

public class SIMGuardUtil {
	private static final String SMS_DELIVERED = "smsdelivered";
	private static final short SMS_PORT = 0;
	private static boolean loggingEnabled;
	private static String LOGTAG = "SIMGuardUtil";
	private static int MAX_SMS_MESSAGE_LENGTH = 160;
	private static String SMS_SENT = "smssent";

	public static void showNotification(Context context,
			String title, String message, int id) {
		Log.d(loggingEnabled, LOGTAG , "In "+LOGTAG+".showNotification. title:"+title+", message:"+message+", id:"+id);
	//		 Toast.makeText(context, "NotificationUtil.showNotification",
	//					Toast.LENGTH_LONG).show();
			
		Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(context)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle(title)
		        .setSound(soundUri)
		        .setContentText(message);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(context, DashboardActivity.class);
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		resultIntent.putExtra("ID", id);
		
		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(DashboardActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(id, mBuilder.build());
	}
	
	public static void sendSms(String phonenumber,String message, boolean isBinary, Context mContext)
	{
		Log.i(loggingEnabled, LOGTAG, "phonenumber:"+phonenumber+", message:"+message);
	    SmsManager manager = SmsManager.getDefault();

	    PendingIntent piSend = PendingIntent.getBroadcast(mContext, 0, new Intent(SMS_SENT ), 0);
	    PendingIntent piDelivered = PendingIntent.getBroadcast(mContext, 0, new Intent(SMS_DELIVERED), 0);

	    if(isBinary)
	    {
	            byte[] data = new byte[message.length()];

	            for(int index=0; index<message.length() && index < MAX_SMS_MESSAGE_LENGTH; ++index)
	            {
	                    data[index] = (byte)message.charAt(index);
	            }

	            manager.sendDataMessage(phonenumber, null, (short) SMS_PORT, data,piSend, piDelivered);
	    }
	    else
	    {
	            int length = message.length();

	            if(length > MAX_SMS_MESSAGE_LENGTH )
	            {
	                    ArrayList<String> messagelist = manager.divideMessage(message);

	                    manager.sendMultipartTextMessage(phonenumber, null, messagelist, null, null);
	            }
	            else
	            {
	                    manager.sendTextMessage(phonenumber, null, message, piSend, piDelivered);
	            }
	    }
	}
}
