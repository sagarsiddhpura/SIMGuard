package com.siddworks.simguard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.noveogroup.android.log.Log;
import com.siddworks.simguard.bean.Contact;
import com.siddworks.simguard.bean.KeyValue;
import com.siddworks.simguard.dao.DatabaseHelper;
import com.siddworks.simguard.util.ContactsListAdapter;

import java.util.ArrayList;


/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class DashboardActivity extends ActionBarActivity {

	// UI references.
	private String LOGTAG = "LoginActivity";
	private DashboardActivity mContext;
	private DatabaseHelper dbHelper;
	ArrayList<Contact> cnts;
	private ContactsListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("In.onCreate");
		setContentView(R.layout.activity_dashboard);

		Intent intent = getIntent();
		boolean isAuthenticated = intent.getBooleanExtra("isAuthenticated", false);
		if(!isAuthenticated)
		{
			this.finish();
		}
		
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        
        TelephonyManager tMgr =(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();
        String getSimSerialNumber = tMgr.getSimSerialNumber();  
        Log.i("mPhoneNumber:"+mPhoneNumber);
        Log.i("getSimSerialNumber:"+getSimSerialNumber);
        
		mContext = this;
		
		// Set up database
		dbHelper = new DatabaseHelper(this);
        dbHelper.open();
        
        EditText messageEditText = (EditText) findViewById(R.id.messageEditText);
        final TextView messageTextView = (TextView) findViewById(R.id.messageTextView);
        
        KeyValue messageKV = dbHelper.getKeyValueByKey("message");
        if(messageKV != null && messageKV.getValue() != null)
        {
        	messageEditText.setText(messageKV.getValue());
        }

		final TextWatcher mTextEditorWatcher = new TextWatcher() {
	        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	        }

	        public void onTextChanged(CharSequence s, int start, int before, int count) {
	        	KeyValue kv = new KeyValue("message", s.toString());
				dbHelper.updateKeyValue(kv);
	        }

	        public void afterTextChanged(Editable s) {
	        }
		};
		messageEditText.addTextChangedListener(mTextEditorWatcher);

		ListView contactsListView = (ListView) findViewById(R.id.contactsListView);
		
		cnts = dbHelper.fetchContacts();
		adapter = new ContactsListAdapter(mContext, R.layout.row_settings_checkbox, cnts, "GroupContacts");
		contactsListView.setAdapter(adapter);
		
		for (Contact cnt : cnts) {
			cnt.loadImage(mContext, adapter);
		}

        android.support.v7.widget.SwitchCompat isActiveCheckBox =
                (android.support.v7.widget.SwitchCompat) findViewById(R.id.isActiveSwitchCompat);
		KeyValue isActiveKV = dbHelper.getKeyValueByKey("isActive");
        if(isActiveKV != null && isActiveKV.getValue() != null)
        {
        	if(isActiveKV.getValue().equals("true"))
        	{
        		isActiveCheckBox.setChecked(true);
        	}
        	else
        	{
        		isActiveCheckBox.setChecked(false);
        	}
        }
	}
	
	@Override  
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
    	
		if (resultCode == Activity.RESULT_OK) { 
			Log.d(LOGTAG, "In onActivityResult.RESULT_OK");
	        Uri contactData = data.getData(); 

        	Cursor c =  managedQuery(contactData, null, null, null, null);  
	        if (c.moveToFirst()) {  
	            String name = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
	            String phone = c.getString(c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
	            String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
//	            System.out.println(c.getColumnCount()); 
	            Log.d(LOGTAG, name);
	            Log.d(LOGTAG, phone);
	            Log.d(LOGTAG, id);
	            
	            if(name != null && phone != null && !phone.equals(""))
	            {
	            	Contact cnt = new Contact();
	            	cnt.setName(name);
	            	cnt.setNumber(phone.replaceAll(" ", "").replaceAll("-", ""));
	            	cnt.setId(Long.parseLong(id));
	            	dbHelper.addContact(cnt);
	            }
	            
	        }  
	    } 
		
		refreshList();
	}
	
	public void addContactClickHandler(View v) {
		Intent intentAddContact = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
		startActivityForResult(intentAddContact, 1);
	}
	
	protected void refreshList() {

		ArrayList<Contact> cnts = dbHelper.fetchContacts();
		
		for (Contact cnt : cnts) {
			cnt.loadImage(mContext, adapter);
		}
		
//		adapter = new ContactsListAdapter(mContext, R.layout.row_settings_checkbox, cnts, "GroupContacts");
//		groupContactsListView.setAdapter(adapter);
		
		adapter.clear();
		adapter.addAll(cnts);
		adapter.notifyDataSetChanged();
	}
	
	public void deleteClickHandler(View v) {
		   
		   LinearLayout ll = (LinearLayout) v.getParent().getParent();
		   final TextView description = (TextView) ll.findViewById(R.id.description);
		   
		   new AlertDialog.Builder(this)
		    .setTitle("Delete entry")
		    .setMessage("Are you sure you want to delete this contact from SMS list")
		    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		            	Contact cnt = new Contact();
		            	cnt.setNumber(description.getText().toString());
		            	dbHelper.deleteContactFromGroup(cnt);
		            	
		            	refreshList();
		        }
		     })
		    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		            // do nothing
		        }
		     })
		    .setIcon(android.R.drawable.ic_dialog_alert)
		    .show();
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_info:
                Intent fAQIntent = new Intent(mContext, FAQ.class);
                startActivity(fAQIntent);
                return true;
        }
        // Otherwise, pass the item to the super implementation for handling, as described in the
        // documentation.
        return super.onOptionsItemSelected(item);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	@Override
    protected void onDestroy() {
    	super.onDestroy();
    	
    	dbHelper.close();
    }

	public void onToggleClicked(View view) {
	    // Is the toggle on? 
	    boolean on = ((android.support.v7.widget.SwitchCompat) view).isChecked();
	    if (on) {
	        Log.i("on toggle on");
        	KeyValue kv = new KeyValue("isActive", "true");
        	dbHelper.updateKeyValue(kv);
	    } else { 
	        Log.i("on toggle off");
        	KeyValue kv = new KeyValue("isActive", "false");
        	dbHelper.updateKeyValue(kv);
	    } 
	    dbHelper.showTable(DatabaseHelper.TABLE_KEYVALUE);
	} 

}

