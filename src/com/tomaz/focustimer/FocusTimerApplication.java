package com.tomaz.focustimer;

import com.tomaz.focustimer.other.Sessions;

import android.app.Application;

public class FocusTimerApplication extends Application {

	private int sessionID = 0;
	private boolean isNowRestTime = false;

	public int getSessionID() {
		return sessionID;
	}

	public boolean isNowRestTime() {
		return isNowRestTime;
	}

	public Sessions getNextSession() {
		if (isNowRestTime) {
			return (sessionID == 3) ? (Sessions.LONG_REST)
					: (Sessions.SHORT_REST);
		} else {
			return Sessions.WORKING;
		}
	}

	/**
	 * you should call {@link #movOn()} after timeUp
	 */
	public Sessions moveOn() {
		if (!isNowRestTime) {
			isNowRestTime = true;
		} else {
			isNowRestTime = false;
			sessionID++;
		}

		if (sessionID >= 4) {
			sessionID = sessionID % 4;
		}

		return getNextSession();
	}

	public Sessions setTimeSession(int sessionId, boolean isRest) {
		this.sessionID = sessionId;
		this.isNowRestTime = isRest;

		return getNextSession();
	}

	public Sessions timeSessionReset() {

		setTimeSession(0, false);

		return getNextSession();
	}
}
