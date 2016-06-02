package com.evansappwriter.mod000;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Random;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.XmlResourceParser;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class QuoteOfTheDayActivity extends QuoteActivity implements OnClickListener {
	SharedPreferences mQuoteSettings;	
	private ShakeListener mShaker;
	public Random mRandom = new Random();
	public Random mRandonImage = new Random();
	String[] mQuotes = new String[MAXDAYS]; 
	String[] mWebsites = new String[MAXDAYS];
	String mHeaderStr;
	int mCurrentQuote;
	int mCurrentImage;
	int mTotalQuotes;
	TextView mHeader;
	TextView mQuoteTxt;
	ScrollView mScrollview;
	private ImageSwitcher mQuoteImage;
	QuoteTask mDownloader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Retrieve the shared preferences
        mQuoteSettings = getSharedPreferences(QUOTE_PREFERENCES, Context.MODE_PRIVATE);        
        if (mQuoteSettings.getInt(QUOTE_PREFERENCES_USER_ID, -1) == -1) {
        	startActivity(new Intent(this, QuoteActivateActivity.class));
        	finish();
        } 
        
        // Setup Screen
        initScreen();
           
        // Display a random image
        displayCurrentImage(mRandonImage.nextInt(MAXIMAGES));
        
        // Set total quotes to 0
        mTotalQuotes = 0;
        mCurrentQuote = 0;
                
        // Get Quotes
        mDownloader = new QuoteTask();
        mDownloader.execute(QUOTE_SERVER_QUOTES);
        
        final Vibrator vibe = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        mShaker = new ShakeListener(this);
        mShaker.setOnShakeListener(new ShakeListener.OnShakeListener () {
          public void onShake()
          {
            vibe.vibrate(100);       
            if (mTotalQuotes == 0) {
            	displayQuote(mTotalQuotes);
            } else {
            	mCurrentQuote = mRandom.nextInt(mTotalQuotes)+1;
                displayQuote(mCurrentQuote);
            }
            
            displayCurrentImage(mRandonImage.nextInt(MAXIMAGES));
          }
        });        
    }   
    
    public void initScreen() {
    	
    	// Handle on ScrollView for changing of background
        mScrollview = (ScrollView) findViewById(R.id.ScrollView01);
    	
    	// Handle on Header getting from Quote Server
        mHeader = (TextView) findViewById(R.id.header);
        
        // Handle on Quote TextView
        mQuoteTxt = (TextView) findViewById(R.id.quote);
        
        // Set Header to default
        if (mHeaderStr == null)
        	mHeaderStr = getResources().getString(R.string.default_header_text);
        
        // Handle on ImageSwitcher
        mQuoteImage = (ImageSwitcher) findViewById(R.id.ImageSwitcher01);
        mQuoteImage.setFactory(new MyImageSwitcherFactory());
        
        // Handles on buttons
		View writeButton = findViewById(R.id.write_button);
        writeButton.setOnClickListener(this);        
        View lessonsButton = findViewById(R.id.lessons_button);
        lessonsButton.setOnClickListener(this);
        View websiteButton = findViewById(R.id.website_button);
        websiteButton.setOnClickListener(this);
        View exitButton = findViewById(R.id.exit_button);
        exitButton.setOnClickListener(this);
        
    }
    
    @Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Display getOrient = getWindowManager().getDefaultDisplay();
		if (getOrient.getOrientation() == Configuration.ORIENTATION_UNDEFINED) {
			setContentView(R.layout.main);
		} else {
			setContentView(R.layout.main_land);
		}
		
		initScreen();		
		displayHeader();        
		displayQuote(mCurrentQuote);        
        displayCurrentImage(mCurrentImage);		
        getPrefs();        
	}

	@Override
    public void onResume()
    {
    	mShaker.resume();
     	super.onResume();
    }
    
    @Override
    public void onPause()
    {
    	if (mDownloader != null && mDownloader.getStatus() != AsyncTask.Status.FINISHED) {
			mDownloader.cancel(true);			
		}
    	mShaker.pause();
    	super.onPause();
    }
        
    @Override
	protected void onStart() {
		getPrefs();
		super.onStart();
	}    
    
    public int getDayOfYear() {
    	
        Calendar cal = Calendar.getInstance();
        int doy = cal.get(Calendar.DAY_OF_YEAR);
        int year = cal.get(Calendar.YEAR);
        if (year > STARTYEAR) {       	
            doy = 365 * (year-STARTYEAR) + doy;
            if (year != 2012)
         	  doy++;
        }
        
        if (doy <= mTotalQuotes)
        	return(doy);
        else
        	return((mTotalQuotes > 0) ? (mRandom.nextInt(mTotalQuotes))+1 : 0);
    }
    
    public void displayCurrentImage(int next) {
    	mQuoteImage.setImageResource(IMAGES[next]);  
    	mCurrentImage = next;
    }

	public void displayQuote (int doy) {
    	    	    
		for (int j = doy; j >= 0; j--) {
    		if (mQuotes[j] != null && mQuotes[j].length() > 0) {
        		mQuoteTxt.setText(mQuotes[j]);
        		mCurrentQuote = j;
        		return;
        	}
    	}
    	for (int j = doy+1; j <= mTotalQuotes; j++) {
    		if (mQuotes[j] != null && mQuotes[j].length() > 0) {
        		mQuoteTxt.setText(mQuotes[j]);
        		mCurrentQuote = j;
        		return;
        	}
    	}    	
    }
	
	public void displayHeader() {
		mHeader.setText(mHeaderStr);
	}
	    
    public void handleNoQuotes() {
    	mQuoteTxt.setText(getResources().getString(R.string.noquotes));
    	mQuotes[0] = getResources().getString(R.string.noquotes);
    	displayHeader();
    }    
    
    public void refreshQuotes() {
    	mHeader.setText("");
    	java.util.Arrays.fill(mQuotes,"");  // Clear out the quotes
    	java.util.Arrays.fill(mWebsites,"");  // Clear out the websites
    	mHeaderStr = getResources().getString(R.string.default_header_text);
    	mTotalQuotes = 0;
    	
    	mDownloader = new QuoteTask();
        mDownloader.execute(QUOTE_SERVER_QUOTES);    		
    }
    
    
        
    public void onClick(View v) {
    	switch (v.getId()) {
		case R.id.write_button:
			Intent i = new Intent(this,LessonEdit.class);
			i.putExtra(QUOTE, mQuotes[mCurrentQuote]);
			i.putExtra(DAYOFYEAR, mCurrentQuote);
			startActivity(i);
    		break;
    	case R.id.lessons_button:   
    		startActivity(new Intent(this,LessonActivity.class)); 
    		break;   	
    	case R.id.website_button:
    		if (mWebsites[mCurrentQuote] != null && mWebsites[mCurrentQuote].trim().length()>0)
    			openBrowser(mWebsites[mCurrentQuote]);    		
    		break;
    	case R.id.exit_button:
    		finish();
    		break;    		
    	}		
	}
    
    // Open a browser on the URL 
    private void openBrowser(String Url) {
    	if (Url != null && Url.trim().length()>0) {
    		Uri uri = Uri.parse(Url);
        	Intent i = new Intent(Intent.ACTION_VIEW, uri);
        	startActivity(i);
    	} 	
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		getMenuInflater().inflate(R.menu.quoteoptions, menu);
		menu.findItem(R.id.instructions_menu_item).setIntent(new Intent(this, QuoteInstructionsActivity.class));
		menu.findItem(R.id.about_menu_item).setIntent(new Intent(this, QuoteAboutActivity.class));
		menu.findItem(R.id.refresh_menu_item).setIntent(new Intent(this, QuoteOfTheDayActivity.class));
		menu.findItem(R.id.settings_menu_item).setIntent(new Intent(this, MyPreferenceActivity.class));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {		
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.instructions_menu_item:			
		case R.id.about_menu_item:
		case R.id.settings_menu_item:
			startActivity(item.getIntent());
			break;			
		case R.id.refresh_menu_item:
			refreshQuotes();
			break;
		case R.id.share_menu_item:
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.setType("text/plain");
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, 
					getResources().getString(R.string.email_subject_text));
    	    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, 
    	    		getResources().getString(R.string.email_body_text) + 
    	    		mQuoteTxt.getText().toString() + "\n");
    	    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
			break;
				
		}
		
		return true;
	}
		
	private void getPrefs() {
    	// Get the xml/preferences.xml preferences
        SharedPreferences prefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());
        
        int size = prefs.getInt("textsizePref", 20);
        int color = Integer.valueOf(prefs.getString("textcolorPref", "7f070006"),16);
        int bakcolor = Integer.valueOf(prefs.getString("backgroundcolorPref", "1"));
        int font = Integer.valueOf(prefs.getString("fontPref", "1"));
        mQuoteTxt.setTextColor(getResources().getColor(color));
                     
        SharedPreferences sp =  PreferenceManager.getDefaultSharedPreferences(this);
        Editor editor = sp.edit();
        switch (bakcolor) {
        case 1:        	
        	mScrollview.setBackgroundColor(getResources().getColor(R.color.white));
        	if (color == 1) {
        		mQuoteTxt.setTextColor(getResources().getColor(R.color.black));
        		editor.putString("textcolorPref", "2");
        	}        		
        	mHeader.setTextColor(getResources().getColor(R.color.black));
        	break;
        case 2:
        	mScrollview.setBackgroundColor(getResources().getColor(R.color.black));
        	if (color == 2) {
        		mQuoteTxt.setTextColor(getResources().getColor(R.color.white));
        		editor.putString("textcolorPref", "1");
        	}        		
        	mHeader.setTextColor(getResources().getColor(R.color.white));
            break;
        }       
        editor.commit();
        switch (font) {
        case 1:
        	mQuoteTxt.setTypeface(Typeface.DEFAULT);
        	break;
        case 2:
        	mQuoteTxt.setTypeface(Typeface.SANS_SERIF);
        	break;
        case 3:
        	mQuoteTxt.setTypeface(Typeface.SERIF);
        	break;
        case 4:
        	mQuoteTxt.setTypeface(Typeface.MONOSPACE);
        	break;
        }
        mQuoteTxt.setTextSize((float) size);
        
    }
       
    private class MyImageSwitcherFactory implements ViewSwitcher.ViewFactory {
		public View makeView() {
			ImageView imageView = new ImageView(QuoteOfTheDayActivity.this);
			imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			imageView.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.FILL_PARENT, 
					LayoutParams.FILL_PARENT));
			return imageView;			
		}
	}
    
    private class QuoteTask extends AsyncTask<Object, String, Boolean> {
    	private static final String DEBUG_TAG = "MessageTask";
    	ProgressDialog pleaseWaitDialog;
    	
		@Override
		protected Boolean doInBackground(Object... params) {
			boolean result = false;
			
			XmlPullParser content = null;
			try {
				Integer accountId = mQuoteSettings.getInt(QUOTE_PREFERENCES_USER_ID, -1);
				String pathToQuotes = params[0] + "?accountid=" + accountId;
				
				URL xmlUrl = new URL(pathToQuotes);
				content = XmlPullParserFactory.newInstance().newPullParser();
				content.setInput(xmlUrl.openStream(), null);								
			} catch (XmlPullParserException e) {
				content = null;
            } catch (IOException e) {
            	content = null;
            }
            
            if (content != null) {
                try {
                	result = processContent(content);          
                } catch (XmlPullParserException e) {
                    Log.e(DEBUG_TAG, "Pull Parser failure", e);
                } catch (IOException e) {
                    Log.e(DEBUG_TAG, "IO Exception parsing XML", e);
                }
            }
            
			return result;
		}
		
		/**
	     * Churn through an XML quote information and populate a {@code TableLayout}
	     * 
	     * @param contents
	     *            A standard {@code XmlPullParser} containing the quotes
	     * @throws XmlPullParserException
	     *             Thrown on XML errors
	     * @throws IOException
	     *             Thrown on IO errors reading the XML
	     */
	    private boolean processContent(XmlPullParser contents) throws XmlPullParserException, IOException {
	        boolean elements = false;
	    	int eventType = -1;
	    	 
	    	
	        // Find Category records from XML
	        while (eventType != XmlResourceParser.END_DOCUMENT) {
	            if (eventType == XmlResourceParser.START_TAG) {
	            	String strName = contents.getName();
	                if (strName.equals(XML_TAG_QUOTE)) {
	                	mTotalQuotes++;
	                	int index = Integer.parseInt(contents.getAttributeValue(null, XML_TAG_QUOTE_ATTRIBUTE_QUOTENUM));	
	                	if (index == mQuotes.length) {
	                		mQuotes = expand(mQuotes,(index+MAXDAYS));
	                		mWebsites = expand(mWebsites,(index+MAXDAYS));
	                	}
	                	mWebsites[index] = validateLink(contents.getAttributeValue(null, XML_TAG_QUESTION_ATTRIBUTE_WEBSITE));	
	                	mQuotes[index] = addLinefeeds(contents.getAttributeValue(null, XML_TAG_QUOTE_ATTRIBUTE_TEXT));	                		                	
	                	elements = true;
	                }
	                if (strName.equals(XML_TAG_ACCOUNT)) {
	                	mHeaderStr = contents.getAttributeValue(null, XML_TAG_ACCOUNT_HDR);	                	
	                }
	            }
	            eventType = contents.next();
	        }
	        return elements;
	    }	 
	    
	    private String validateLink(String s) {
	    	if (s.trim().length() == 0 || s == null || s.toLowerCase().startsWith("http://")) {
	    		return s;
	    	} else {
	    		return ("http://" + s);
	    	}
	    }
	    
	    private String addLinefeeds(String s) {
	    	return (s.replaceAll("<br>","\n"));
	    	
	    }
	    
	    private String[] expand(String[] array, int size) {
	        String[] temp = new String[size];
	        System.arraycopy(array, 0, temp, 0, array.length);
	        for(int j = array.length; j < size; j++)
	            temp[j] = "";
	        return temp;
	    }
	    
	    @Override
		protected void onPreExecute() {
			pleaseWaitDialog = ProgressDialog.show(QuoteOfTheDayActivity.this, getResources().getString(R.string.app_name), 
					getResources().getString(R.string.quote_dialog_text) ,true, true);
			pleaseWaitDialog.setOnCancelListener(new OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					QuoteTask.this.cancel(true);
				}
			});
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				mCurrentQuote = getDayOfYear();
				displayQuote(mCurrentQuote);
				displayHeader();
			} else {
				handleNoQuotes();
			}
			pleaseWaitDialog.dismiss();
		}    	
    }
}