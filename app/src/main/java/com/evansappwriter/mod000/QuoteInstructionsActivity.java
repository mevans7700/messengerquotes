package com.evansappwriter.mod000;

import android.os.Bundle;
import android.widget.TextView;

public class QuoteInstructionsActivity extends QuoteActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.instructions);
						
		TextView instructTextView = (TextView) findViewById(R.id.instructions);
		instructTextView.setText(getResources().getString(R.string.instructions_text));
		
		setTitle(getResources().getString(R.string.instructions_title));
	}

}
