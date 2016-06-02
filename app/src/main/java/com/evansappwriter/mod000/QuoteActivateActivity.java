package com.evansappwriter.mod000;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class QuoteActivateActivity extends QuoteActivity {
	AccountTask accountUpload;
	SharedPreferences mQuoteSettings;
		
	static final int NEW_ACCOUNT_DIALOG_ID = 0;
		
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activate);
        
        // Retrieve the Account ID
        mQuoteSettings = getSharedPreferences(QUOTE_PREFERENCES, Context.MODE_PRIVATE);        
        Integer serverId = -1;
        serverId = mQuoteSettings.getInt(QUOTE_PREFERENCES_USER_ID, -1);               
        
        // if we don't have a serverId yet, display activation dialog
        if (serverId == -1 && QUOTE_ACCOUNT_ID == -1) {
            showDialog(NEW_ACCOUNT_DIALOG_ID);
            
            // Initialize the email entry
            initEmailEntry();
        } else {
        	if (QUOTE_ACCOUNT_ID != -1 && serverId == -1) { 
        		// set for customized app Account ID
        		Editor editor = mQuoteSettings.edit();
                editor.putInt(QUOTE_PREFERENCES_USER_ID, QUOTE_ACCOUNT_ID);
                editor.commit();
        	}
        	startActivity(new Intent(this, QuoteOfTheDayActivity.class));
        	finish();
        }                           
        
    }
    
    @Override
    protected void onPause() {
        if (accountUpload != null) {
            accountUpload.cancel(true);
        }

        super.onPause();
    }
    
    @Override
    protected void onDestroy() {
    	Log.d(DEBUG_TAG, "SHARED PREFERENCES");
        Log.d(DEBUG_TAG, "Email is: " + mQuoteSettings.getString(QUOTE_PREFERENCES_EMAIL, "Not set"));
        
        super.onDestroy();
    }
    
    /**
     * update the server with the latest settings data - everything but the image
     * 
     */
    private void updateServerData() {
        // make sure we don't collide with another pending update
        if (accountUpload == null || accountUpload.getStatus() == AsyncTask.Status.FINISHED || accountUpload.isCancelled()) {
            accountUpload = new AccountTask();
            if (mQuoteSettings.getInt(QUOTE_PREFERENCES_USER_ID, -1) == -1) {
            	accountUpload.execute();
            }            
        } else {
            Log.w(DEBUG_TAG, "Warning: update task already going");
        }
    }
    
    void initEmailEntry() {
    	final EditText emailText = (EditText) findViewById(R. id. EditText_Email);
    	
        if (mQuoteSettings.contains(QUOTE_PREFERENCES_EMAIL)) {
        	emailText.setText(mQuoteSettings.getString(QUOTE_PREFERENCES_EMAIL, ""));
        }
        emailText.setOnKeyListener(new View.OnKeyListener() {
        	public boolean onKey(View v, int keyCode, KeyEvent event) {
        		if ((event. getAction() == KeyEvent.ACTION_DOWN) && 
        				(keyCode == KeyEvent.KEYCODE_ENTER)) {
        			String strEmail = emailText.getText().toString() ;
        			Editor editor = mQuoteSettings.edit();
        	        editor.putString(QUOTE_PREFERENCES_EMAIL, strEmail);
        	        editor.commit();
        	        // ... and server data
                    updateServerData();
                    return true;
        		}
        		return false;
        	}      	
        });
    }
    
        
    @Override
	protected Dialog onCreateDialog(int id) {
    	super.onCreateDialog(id);
    	LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		switch (id) {
		case NEW_ACCOUNT_DIALOG_ID:
			final View activateLayout = layoutInflater.inflate(R.layout.newaccount_dialog, (ViewGroup) findViewById(R.id.root));
			
			AlertDialog.Builder activateBuilder = new AlertDialog.Builder(this);
			activateBuilder.setView(activateLayout);
			activateBuilder.setTitle(R.string.activiation_account_dialog_title);
			activateBuilder.setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	        	public void onClick(DialogInterface dialog, int whichButton) {
	        		QuoteActivateActivity.this.removeDialog(NEW_ACCOUNT_DIALOG_ID);
	        	}
	        });
	        
	        AlertDialog activateDialog = activateBuilder.create();
	        return activateDialog;
		}
		
		return null;
	}
    
    private class AccountTask extends AsyncTask<Object, String, Boolean> {
    	String responseBody;
    	
    	@Override
        protected void onPostExecute(Boolean result) {
            QuoteActivateActivity.this.setProgressBarIndeterminateVisibility(false);
            if (!result) {
            	if (responseBody.equals("0"))
            		Toast.makeText(QuoteActivateActivity.this, "Error! There is no account setup with the email entered.  Please re-enter...", Toast.LENGTH_LONG).show();
            	else
            		Toast.makeText(QuoteActivateActivity.this, "Error! This email is associated with another account. Either re-enter the email or push the menu button and go to the 'About' section and email the support department", Toast.LENGTH_LONG).show();
            } else {
            	startActivity(new Intent(QuoteActivateActivity.this, QuoteOfTheDayActivity.class));
            	finish();
            }          
            
        }

        @Override
        protected void onPreExecute() {
        	QuoteActivateActivity.this.setProgressBarIndeterminateVisibility(true);
        }
    	
		@Override
		protected Boolean doInBackground(Object... params) {
			Boolean succeeded = false;
			
			String email = mQuoteSettings.getString(QUOTE_PREFERENCES_EMAIL, "");
            
            Vector<NameValuePair> vars = new Vector<NameValuePair>();            
            
            // if we don't have a playerId yet, we must pass up a uniqueId
            // A good place to get a unique identifier from is the device Id
            // which is conveniently store in the TelephonyManager data
            // This requires the use of READ_PHONE_STATE permission
            TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String uniqueId = telManager.getDeviceId();

            // hash the value to get a unique, but non-identifiable value to use
            String mdUniqueId;
            try {
            	MessageDigest sha = MessageDigest.getInstance("SHA");

                byte[] enc = sha.digest(uniqueId.getBytes());
                StringBuilder sb = new StringBuilder();

                for (byte enc1 : enc) {
                	sb.append(Integer.toHexString(enc1 & 0xFF));
                }
                mdUniqueId = sb.toString();
            } catch (NoSuchAlgorithmException e) {
                Log.w(DEBUG_TAG, "Failed to get SHA, using hashcode()");
                mdUniqueId = String.valueOf(uniqueId.hashCode());
            }
            vars.add(new BasicNameValuePair("uniqueId", mdUniqueId));
            vars.add(new BasicNameValuePair("email", email));
                        
            String url = QUOTE_SERVER_ACCOUNT_EDIT + "?" + URLEncodedUtils.format(vars, null);

            HttpGet request = new HttpGet(url);

            try {
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                HttpClient client = new DefaultHttpClient();
                responseBody = client.execute(request, responseHandler);

                if (responseBody != null && responseBody.length() > 0) {
                	if (responseBody.equals("0") || responseBody.equals("1")) {  // errors
                	}
                	else {
                		Integer resultId = Integer.parseInt(responseBody);
                        Editor editor = mQuoteSettings.edit();
                        editor.putInt(QUOTE_PREFERENCES_USER_ID, resultId);
                        editor.commit();
                        succeeded = true;
                	}                    
                }
            } catch (ClientProtocolException e) {
                Log.e(DEBUG_TAG, "Failed to get playerId (protocol): ", e);
            } catch (IOException e) {
                Log.e(DEBUG_TAG, "Failed to get playerId (io): ", e);
            }
            return succeeded;
		}   	
    }

}
