package com.evansappwriter.mod000;

import android.app.Activity;

public class QuoteActivity extends Activity {
	// Type of App: -1 for Stand alone generic  Number greater than 0 is the customized app account ID
	public static final Integer QUOTE_ACCOUNT_ID = 43001; 
	
	public static final String DEBUG_TAG = "Message of the Day Log";
	public static final String QUOTE_PREFERENCES = "QuotePrefs";
	public static final String QUOTE_PREFERENCES_USER_ID = "ServerId"; // Integer
	public static final String QUOTE_PREFERENCES_EMAIL = "Email"; // String
	
	public static final int STARTYEAR = 2012;
	public static final int MAXDAYS = 367;
	public static final int MAXIMAGES = 50;
	public static final String QUOTE = "quote";
	public static final String DAYOFYEAR = "dayofyear";
		
	// XML Tag Names
    public static final String XML_TAG_QUOTE_BLOCK = "quotes";
    public static final String XML_TAG_QUOTE = "quote";
    public static final String XML_TAG_ACCOUNT = "account";
    public static final String XML_TAG_ACCOUNT_HDR = "header";
    public static final String XML_TAG_QUOTE_ATTRIBUTE_QUOTENUM = "quoteNum";
    public static final String XML_TAG_QUOTE_ATTRIBUTE_DAY = "day";
    public static final String XML_TAG_QUOTE_ATTRIBUTE_MONTH = "month";
    public static final String XML_TAG_QUOTE_ATTRIBUTE_YEAR = "year";
    public static final String XML_TAG_QUESTION_ATTRIBUTE_WEBSITE = "website";
    public static final String XML_TAG_QUOTE_ATTRIBUTE_TEXT = "text";
	
	// Server URLs
    //public static final String QUOTE_SERVER_BASE = "http://droid-messengernetwork.appspot.com/";
    public static final String QUOTE_SERVER_BASE = "http://msgoftheday.com/phone/";                     // MEE 08/15/2012 - New server
	public static final String QUOTE_SERVER_ACCOUNT_EDIT = QUOTE_SERVER_BASE + "activate";
	//public static final String QUOTE_SERVER_QUOTES = QUOTE_SERVER_BASE + "quotelistbyinteger.jsp";
	public static final String QUOTE_SERVER_QUOTES = QUOTE_SERVER_BASE + "quotelistbyintegerMSGR.cfm";  // MEE 08/15/2012 - Coldfusion xml
	
	public static final int IMAGES [] = {0x7f020002,0x7f020003,0x7f020004,0x7f020005,0x7f020006,0x7f020007,
		0x7f020008,0x7f020009,0x7f02000a,0x7f02000b,0x7f02000c,0x7f02000d,0x7f02000e,0x7f02000f,
		0x7f020010,0x7f020011,0x7f020012,0x7f020013,0x7f020014,0x7f020015,0x7f020016,0x7f020017,0x7f020018,
		0x7f020019,0x7f02001a,0x7f02001b,0x7f02001c,0x7f02001d,0x7f02001e,0x7f02001f,0x7f020020,0x7f020021,
		0x7f020022,0x7f020023,0x7f020024,0x7f020025,0x7f020026,0x7f020027,0x7f020028,0x7f020029,0x7f02002a,
		0x7f02002b,0x7f02002c,0x7f02002d,0x7f02002e,0x7f02002f,0x7f020030,0x7f020031,0x7f020032,0x7f020033};
}
