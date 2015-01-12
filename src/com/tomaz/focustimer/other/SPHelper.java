package com.tomaz.focustimer.other;

public interface SPHelper {
	
	// -- main file name
	final public static String SP_NAME = "FT_PrefsFile";
	
	
	// -- keys

	/**
	 * <ul>
	 *  	<li>SessionID is an int from 0 ~ 3.</li>
	 *  	<li>every session have a Working Session before a Rest Session.</li>
	 *  	<li>Session 3 has LONG_REST.</li>
	 *	</ul>
	 */
	public final static String KEY_SessionID = "timeSessionID";
	
	
	public final static String KEY_isRest = "isRest";
	
}
