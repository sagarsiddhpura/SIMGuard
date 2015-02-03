package com.siddworks.simguard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.noveogroup.android.log.Log;
import com.siddworks.simguard.bean.KeyValue;
import com.siddworks.simguard.dao.DatabaseHelper;
import android.view.MenuItem;
import java.util.ArrayList;


/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends ActionBarActivity {

	// Values for email and password at the time of the login attempt.
	private String mNameString;
	private String mPasswordString;
	private String mRePasswordString;

	// UI references.
	private EditText mNameEditText;
	private EditText mPasswordEditText;
	private EditText mRePasswordEditText;
	private String LOGTAG = "LoginActivity";
	private LoginActivity mContext;
	private DatabaseHelper dbHelper;
	private String simSerialNumber;
	private String mPhoneNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("In.onCreate");
		setContentView(R.layout.activity_login);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);

        }
        
		mContext = this;
		
		// Set up database
		dbHelper = new DatabaseHelper(this);
        dbHelper.open();
        
        TelephonyManager tMgr =(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        mPhoneNumber = tMgr.getLine1Number();
        simSerialNumber = tMgr.getSimSerialNumber();  
        
        ArrayList<KeyValue> keyValues = dbHelper.fetchKeyValues();
        Log.i("keyValues:"+keyValues);
        for (KeyValue keyValue : keyValues) {
			if(keyValue.getKey().equals("isSetup") && keyValue.getValue().equals("true"))
			{
				 Intent passAuthActivityIntent = new Intent(mContext, PasswordAuthenticationActivity.class);
				 startActivity(passAuthActivityIntent);
				 this.finish();
			}
		}
				
		// Set up the login form.
		mNameEditText = (EditText) findViewById(R.id.login_name_EditText);
		mPasswordEditText = (EditText) findViewById(R.id.login_password_EditText);
		mRePasswordEditText = (EditText) findViewById(R.id.login_re_password_EditText);
		mRePasswordEditText
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptSetup();
							return true;
						}
						return false;
					}
				});

		View setupButton = findViewById(R.id.login_setup_Button);
		setupButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptSetup();
					}
				});
		
		findViewById(R.id.exitButton).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						LoginActivity.this.finish();
					}
				});
		
		TextView simSerialValueTextView = (TextView) findViewById(R.id.simSerialValueTextView);
		simSerialValueTextView.setText(simSerialNumber+"");
		if(simSerialNumber == null || simSerialNumber.equals(""))
		{
			simSerialValueTextView.setText("Not Available");
			LinearLayout errorLinearLayout = (LinearLayout) findViewById(R.id.errorLinearLayout);
			errorLinearLayout.setVisibility(View.VISIBLE);
			LinearLayout setupLinearLayout = (LinearLayout) findViewById(R.id.setupLinearLayout);
			setupLinearLayout.setVisibility(View.GONE);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
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
    protected void onDestroy() {
    	super.onDestroy();
    	
    	dbHelper.close();
    }

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptSetup() {
		Log.d("In.attemptLogin");

		// Reset errors.
		mNameEditText.setError(null);
		mRePasswordEditText.setError(null);
		mPasswordEditText.setError(null);

		// Store values at the time of the login attempt.
		mNameString = mNameEditText.getText().toString();
		mPasswordString = mPasswordEditText.getText().toString();
		mRePasswordString = mRePasswordEditText.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid mUsername.
		if (TextUtils.isEmpty(mNameString)) {
			mNameEditText.setError(getString(R.string.error_field_required));
			focusView = mNameEditText;
			cancel = true;
		}

		// Check for a valid password
		if (TextUtils.isEmpty(mPasswordString)) {
			mPasswordEditText.setError(getString(R.string.error_field_required));
			focusView = mPasswordEditText;
			cancel = true;
		}
		
		// Check for a valid domain name.
		if (TextUtils.isEmpty(mRePasswordString)) {
			mRePasswordEditText.setError(getString(R.string.error_field_required));
			focusView = mRePasswordEditText;
			cancel = true;
		}
		
		if (!mPasswordString.equals(mRePasswordString)) {
			mRePasswordEditText.setError(getString(R.string.error_password_not_match));
			focusView = mRePasswordEditText;
			cancel = true;
		}


		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
		        
			KeyValue kv = new KeyValue("name", mNameString);
			dbHelper.createKeyValue(kv);
			kv = new KeyValue("password", mPasswordString);
			dbHelper.createKeyValue(kv);
			kv = new KeyValue("isSetup", "true");
			dbHelper.createKeyValue(kv);
			kv = new KeyValue("phoneno", mPhoneNumber);
			dbHelper.createKeyValue(kv);
			kv = new KeyValue("simserial", simSerialNumber);
			dbHelper.createKeyValue(kv);
			kv = new KeyValue("message", "");
			dbHelper.createKeyValue(kv);
			kv = new KeyValue("isActive", "true");
        	dbHelper.createKeyValue(kv);
			
			Intent dashboardIntent = new Intent(mContext, DashboardActivity.class);
			dashboardIntent.putExtra("isAuthenticated", true);
			startActivity(dashboardIntent);
			this.finish();
		}
	}

}

