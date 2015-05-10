package com.news;

public interface Config {

	
	// CONSTANTS
	//static final String YOUR_SERVER_URL =  "http://php-minddotss.rhcloud.com/register.php";
	// YOUR_SERVER_URL : Server url where you have placed your server files
    // Google project id
    static final String GOOGLE_SENDER_ID = "417921999398";  // Place here your Google project id

    /**
     * Tag used on log messages.
     */
    static final String TAG = "GCM push notification";

    static final String DISPLAY_MESSAGE_ACTION =
            "com.news.DISPLAY_MESSAGE";

    static final String EXTRA_MESSAGE = "NS_MSG";
		
	
}
