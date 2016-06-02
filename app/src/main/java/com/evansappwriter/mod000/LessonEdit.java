package com.evansappwriter.mod000;

import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LessonEdit extends QuoteActivity {	
	private TextView mQuestionText;
	private EditText mAnswerText;
	private Long mRowId;
	private LessonDbAdapter mDbHelper;
	String mQuote;
	int mDayofYear;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mDbHelper = new LessonDbAdapter(this);
        mDbHelper.open();
        
        setContentView(R.layout.lesson_edit);
       
        mQuestionText = (TextView) findViewById(R.id.question_edit);
        mAnswerText = (EditText) findViewById(R.id.answer_edit);
      
        Button confirmButton = (Button) findViewById(R.id.confirm);
       
        mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(LessonDbAdapter.KEY_ROWID);
        Bundle extras = getIntent().getExtras();
        mQuote = extras.getString(QUOTE);
        if (mRowId == null) {
            
            mRowId = extras != null ? extras.getLong(LessonDbAdapter.KEY_ROWID)
                                    : null;
                        
            if (mRowId == null || mRowId == 0) {            	
            	mDayofYear = extras.getInt(QuoteOfTheDayActivity.DAYOFYEAR);
            	checkForLessons();
            }
        }
        
        populateFields();
        
        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
          
        });
	}
	
	public void checkForLessons() {
		Cursor lessonCursor = null;
		try {
			lessonCursor = mDbHelper.fetchEntryByDay(mDayofYear);	        
		} catch (SQLException e) {
			lessonCursor = null;        	        	
		}  		
				
		if (lessonCursor.getCount() == 0) {			
			mRowId = mDbHelper.createEntry(mDayofYear, mQuote, " ");
		} else {
			mRowId = lessonCursor.getLong(lessonCursor.getColumnIndexOrThrow(LessonDbAdapter.KEY_ROWID));
		}
				
	}
	
	private void populateFields() {
		Cursor entry;
        if (mRowId != null && mRowId != 0) {
            entry = mDbHelper.fetchEntry(mRowId);            
            
        } else {
        	entry = mDbHelper.fetchEntryByDay(mDayofYear);        	
        }        
        startManagingCursor(entry);
        mQuestionText.setText(mQuote);
        mAnswerText.setText(entry.getString(
        		entry.getColumnIndexOrThrow(LessonDbAdapter.KEY_ANSWER)));   
    } 
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
        outState.putSerializable(LessonDbAdapter.KEY_ROWID, mRowId);
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}

	@Override
	protected void onResume() {
		super.onResume();
		populateFields();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDbHelper.close();
	}
    
	private void saveState() {
        String answer = mAnswerText.getText().toString();
        String question = mQuestionText.getText().toString();
        
        mDbHelper.updateEntry(mRowId, question, answer);       
    }

}
