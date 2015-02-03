package com.siddworks.simguard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.siddworks.simguard.bean.KeyValue;
import com.siddworks.simguard.dao.DatabaseHelper;

public class PasswordAuthenticationActivity extends ActionBarActivity {

	private DatabaseHelper dbHelper;
	private EditText passwordAuthenticationPasswordEditText;
	private PasswordAuthenticationActivity mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_password_authentication);
		
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);

        }
        
		mContext = this;
		// Set up database
		dbHelper = new DatabaseHelper(this);
        dbHelper.open();
		        
		View loginButton = findViewById(R.id.passwordAuthenticationLoginButton);
		loginButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
		
		passwordAuthenticationPasswordEditText = (EditText) findViewById(R.id.passwordAuthenticationPasswordEditText);
		passwordAuthenticationPasswordEditText
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

	}
	
	@Override
    protected void onDestroy() {
    	super.onDestroy();
    	
    	dbHelper.close();
    }

	protected void attemptLogin() {
		passwordAuthenticationPasswordEditText.setError(null);
		KeyValue loginKV = dbHelper.getKeyValueByKey("password");
		Editable text = passwordAuthenticationPasswordEditText.getText();
		if(loginKV != null && text != null && 
				loginKV.getValue().equals(text.toString()))
		{
			Intent dashboardIntent = new Intent(mContext, DashboardActivity.class);
			dashboardIntent.putExtra("isAuthenticated", true);
			startActivity(dashboardIntent);
			this.finish();
		}
		else if(loginKV != null && text != null && 
				!loginKV.getValue().equals(text.toString()))
		{
			passwordAuthenticationPasswordEditText.setError(getString(R.string.incorrect_password));
		}
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.password_authentication, menu);
//		return true;
//	}

//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle action bar item clicks here. The action bar will
//		// automatically handle clicks on the Home/Up button, so long
//		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
}
