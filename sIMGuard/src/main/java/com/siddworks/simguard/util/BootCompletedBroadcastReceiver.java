package com.siddworks.simguard.util;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import com.siddworks.simguard.bean.Contact;
import com.siddworks.simguard.bean.KeyValue;
import com.siddworks.simguard.dao.DatabaseHelper;

import java.util.ArrayList;

public class BootCompletedBroadcastReceiver extends BroadcastReceiver {
	private DatabaseHelper dbHelper;
	private boolean isLoggingEnabled = true;
	private String LOGTAG = "BootCompletedBroadcastReceiver";
	private Context mContext;
	private ArrayList<Contact> cnts;
	private String message;
	private String line1Number;
	private boolean isSendingFailed = false;

	@Override
    public void onReceive(Context context, Intent intent) {
	 
		mContext = context;
	 	// Set up database
		dbHelper = new DatabaseHelper(context);
        dbHelper.open();
        KeyValue isActiveKeyValue = dbHelper.getKeyValueByKey("isActive");
//	        Toast.makeText(context, "isActiveKeyValue:"+isActiveKeyValue.getValue()
//	        		, Toast.LENGTH_LONG).show();
        SIMGuardUtil.showNotification(context, "BootCompletedBroadcastReceiver", "entering if", 1);
        if(isActiveKeyValue == null){
        	SIMGuardUtil.showNotification(context, "isActiveKeyValue", "null", 6);
        }
        if(isActiveKeyValue != null && isActiveKeyValue.getValue() != null
        		&& isActiveKeyValue.getValue().equals("true"))
        {
        	SIMGuardUtil.showNotification(context, "isActiveKeyValue", isActiveKeyValue.toString(), 2);
        	KeyValue simserialKeyValue = dbHelper.getKeyValueByKey("simserial");
        	TelephonyManager tMgr =(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
	        String simSerialNumber = tMgr.getSimSerialNumber();  
	        line1Number = tMgr.getLine1Number();
//		        Log.i(isLoggingEnabled, LOGTAG, "simSerialNumber:"+simSerialNumber+
//		        		", simserialKeyValue.getValue():"+simserialKeyValue.getValue());
//		        Toast.makeText(context, "simSerialNumber:"+simSerialNumber+
//		        		", simserialKeyValue.getValue():"+simserialKeyValue.getValue()
//    	        		, Toast.LENGTH_LONG).show();
		        SIMGuardUtil.showNotification(context, "Alert", "simSerialNumber:"+simSerialNumber+
		        		", simserialKeyValue.getValue():"+simserialKeyValue.getValue(), 3);
	        if(!simSerialNumber.equals(simserialKeyValue.getValue()))
	        {
	        	// Send SMS
	        	SIMGuardUtil.showNotification(context, "Alert", "Sim card changed", 4);
	        	
	        	KeyValue messageKeyValue = dbHelper.getKeyValueByKey("message");
	        	message = messageKeyValue.getValue();
	        	
	        	cnts = dbHelper.fetchContacts();
	        	sendSms();
	        }
        }
        dbHelper.close();
    }
	
	private void sendSms() {
		 for (Contact contact : cnts) {
     		SIMGuardUtil.sendSms(contact.getNumber(), message+"::"+line1Number, false, mContext);
		 }
		 isSendingFailed = false;
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

	        @Override
	        public void onReceive(Context context, Intent intent) {
	            try {
		            switch (getResultCode()) {
			            case Activity.RESULT_OK:
			                break;
			            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
			            	if(!isSendingFailed){
				            	isSendingFailed = true;
								Thread.sleep(30000);
								sendSms();
			            	}
			                break;
			            case SmsManager.RESULT_ERROR_NO_SERVICE:
			            	if(!isSendingFailed){
				            	isSendingFailed = true;
								Thread.sleep(30000);
								sendSms();
			            	}
			                break;
			            case SmsManager.RESULT_ERROR_NULL_PDU:
			            	if(!isSendingFailed){
				            	isSendingFailed = true;
								Thread.sleep(30000);
								sendSms();
			            	}
			                break;
			            case SmsManager.RESULT_ERROR_RADIO_OFF:
			            	if(!isSendingFailed){
				            	isSendingFailed = true;
								Thread.sleep(30000);
								sendSms();
			            	}
			                break;
		            }
	            } catch (InterruptedException e) {
					e.printStackTrace();
				}

	      }
	  };
}