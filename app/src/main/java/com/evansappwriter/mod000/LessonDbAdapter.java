package com.evansappwriter.mod000;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LessonDbAdapter {
	private static final String TAG = "LessonDbAdapter";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mCtx;
	
	/**
     * Database creation sql statement
     */    
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "quoteoftheday";
    private static final String DATABASE_TABLE = "lessons";    
    public static final String KEY_ROWID = "_id";
    public static final String KEY_DAYOFYEAR = "dayofyear";
    public static final String KEY_QUESTION = "question";
    public static final String KEY_ANSWER = "answer";    
    private static final String DATABASE_CREATE =
        "create table " + DATABASE_TABLE 
                + " (_id integer primary key autoincrement, "
        		+ KEY_DAYOFYEAR + " integer, "
        		+ KEY_QUESTION + " text not null, "
        		+ KEY_ANSWER + " text not null);";
    
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }
    
    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public LessonDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }
    
    /**
     * Open the database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public LessonDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
        mDbHelper.close();
    }
    
    /**
     * Create a new entry using the quote and answer provided. If the entry is
     * successfully created return the new rowId for that entry, otherwise return
     * a -1 to indicate failure.
     * 
     * @param wisdomid the Day of the Year of the lessons table
     * @param question the question of the lessons table
     * @param answer the answer of the lessons table
     * @return rowId or -1 if failed
     */
    public long createEntry(int dayofyear, String question, String answer) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_DAYOFYEAR, dayofyear);
        initialValues.put(KEY_QUESTION, question);
        initialValues.put(KEY_ANSWER, answer);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the entry with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteEntry(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    /**
     * Return a Cursor over the list of all entries in the database
     * 
     * @return Cursor over all entries
     */
    public Cursor fetchAllEntries() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_DAYOFYEAR, KEY_QUESTION,
                KEY_ANSWER}, null, null, null, null, null);
    }
    
    /**
     * Return a Cursor positioned at the entries that match the given day of year
     * 
     * @param day of year of entry to retrieve
     * @return Cursor positioned to matching entry, if found
     * @throws SQLException if entry could not be found/retrieved
     */
    public Cursor fetchEntryByDay(int dayofyear) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_DAYOFYEAR,
                        KEY_QUESTION, KEY_ANSWER}, KEY_DAYOFYEAR + "=" + dayofyear, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    
    /**
     * Return a Cursor positioned at the entry that matches the given rowId
     * 
     * @param rowId id of entry to retrieve
     * @return Cursor positioned to matching entry, if found
     * @throws SQLException if entry could not be found/retrieved
     */
    public Cursor fetchEntry(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_DAYOFYEAR,
                        KEY_QUESTION, KEY_ANSWER}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Update the entry using the details provided. The entry to be updated is
     * specified using the rowId, and it is altered to use the question and answer
     * values passed in
     * 
     * @param rowId id of note to update
     * @param question value to set entry question to
     * @param answer value to set entry answer to
     * @return true if the entry was successfully updated, false otherwise
     */
    public boolean updateEntry(long rowId, String question, String answer) {
        ContentValues args = new ContentValues();
        args.put(KEY_QUESTION, question);
        args.put(KEY_ANSWER, answer);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

}
