package com.evansappwriter.mod000;

import android.os.Bundle;
import android.widget.TextView;

public class QuoteAboutActivity extends QuoteActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
						
		TextView aboutTextView = (TextView) findViewById(R.id.about);
		aboutTextView.setText(getResources().getString(R.string.about_text));
		
		setTitle(getResources().getString(R.string.titlebar_name));
	}

}
