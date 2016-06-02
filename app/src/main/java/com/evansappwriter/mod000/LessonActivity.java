package com.evansappwriter.mod000;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class LessonActivity extends ListActivity {
	private static final int ACTIVITY_EDIT = 1;
	LessonDbAdapter mDbHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lesson_list);
				
		mDbHelper = new LessonDbAdapter(this);
        mDbHelper.open();
		filldata();		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDbHelper.close();
	}
	
	public void filldata() {
		Cursor lessonCursor = mDbHelper.fetchAllEntries();
		startManagingCursor(lessonCursor);
        
        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{LessonDbAdapter.KEY_QUESTION, LessonDbAdapter.KEY_ANSWER};
        
        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.question, R.id.answer};
        
        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter adapt = 
        	    new SimpleCursorAdapter(this, R.layout.lesson_row, lessonCursor, from, to);
        setListAdapter(adapt);
        
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Cursor lessonCursor = mDbHelper.fetchEntry(id);
		startManagingCursor(lessonCursor);
		String quote = lessonCursor.getString(lessonCursor.getColumnIndexOrThrow(LessonDbAdapter.KEY_QUESTION));
		
		Intent i = new Intent(this, LessonEdit.class);
		i.putExtra(QuoteActivity.QUOTE, quote);
        i.putExtra(LessonDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        filldata();        
    }

}
