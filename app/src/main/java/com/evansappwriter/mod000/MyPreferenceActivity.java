package com.evansappwriter.mod000;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class MyPreferenceActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preferences);
		PreferenceManager.setDefaultValues(MyPreferenceActivity.this, R.xml.preferences, false);
	}
}	
	
