package com.tomaz.focustimer.service;

import java.util.HashMap;
import java.util.Map;

import com.tomaz.focustimer.MainActivity;
import com.tomaz.focustimer.exception.UIHandlerMissingException;
import com.tomaz.focustimer.fragment.MainFragment;
import com.tomaz.focustimer.other.Sections;
import com.tomaz.focustimer.other.TimerStates;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

/**
 * This Service run in background and calculate the time. Use Intent to push a
 * integer with key "times"
 * 
 * You have to register a handler which control your UI
 * 
 * @author Tomaz Wang
 * 
 */
public class TimerService extends Service {

	public final static String KEY_TIMES_TO_COUNT = "times";
	public final static String tag = "TimerServices";
	private int secToCount = 0;
	private int secTotal = 0;

	private TimerCallBack callBack;
	private Activity client;
	private final Handler mainTimer = new Handler();

	private TimerStates timerStates = TimerStates.RESET;

	private static final int FOREGROUND_NOTIFICATION_ID = 1;
	private NotificationManager nManager;

	private Runnable countingRunnable = buildCountdownTimerRunnable();

	@Override
	public void onCreate() {
		Log.d(tag, "Service Created");
	};

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(tag, "on Bind");
		return new TimerBinder();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.d(tag, "onStartCommand");
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			int secTotal = bundle.getInt(KEY_TIMES_TO_COUNT);
			startNewCount(secTotal);
		} else {
			Log.e(tag, "bundle is null");
		}

		return START_REDELIVER_INTENT;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(tag, "Service Destory");
		mainTimer.removeCallbacks(countingRunnable);
	}

	/*
	 * === core timer functions ===================
	 */
	public void startNewCount(int sec) {
		secTotal = sec;
		secToCount = sec;
		startNewCount();
	}

	public void startNewCount() {

		// start mainTimer
		countingRunnable.run();

		// change states
		setTimerStates(TimerStates.COUNTING);

		// start notification
		startForeground(FOREGROUND_NOTIFICATION_ID,
				buildForegroundNotification(timerStates, secToCount));
		nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}

	public int stopCount(boolean clearTimer) {

		// stop notification
		mainTimer.removeCallbacks(countingRunnable);

		// change states
		// change notification
		stopForeground(true);
		if (!clearTimer) {
			setTimerStates(TimerStates.PAUSE);
			startForeground(FOREGROUND_NOTIFICATION_ID,
					buildForegroundNotification(timerStates, secToCount));
			nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		} else {
			setTimerStates(TimerStates.RESET);
		}
		return secToCount;
	}

	public void resumeCount() {
		// resume timer
		countingRunnable.run();
		
		// change states
		setTimerStates(TimerStates.COUNTING);
		
		// change notification
		startForeground(FOREGROUND_NOTIFICATION_ID,
				buildForegroundNotification(timerStates, secToCount));
		nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}

	private void doWhenCountDown(int sec) {
		if (sec <= 0) {
			// times up
			stopCount(true);
			doAfterTimesUp();
			if (callBack != null) {
				callBack.onCounting(sec, secTotal);
			}
		} else {
			// keep counting
			Log.i(tag, "counting : " + secToCount + " / " + secTotal);
			if (callBack != null) {
				callBack.onCounting(sec, secTotal);
			}
			nManager.notify(FOREGROUND_NOTIFICATION_ID,
					buildForegroundNotification(timerStates, secToCount));
		}
	}

	private void doWhenPause() {
		// TODO
	}

	private void doAfterTimesUp() {
		// TODO
		Log.i(tag, "times up");
	}

	private Runnable buildCountdownTimerRunnable() {
		Runnable r = new Runnable() {

			@Override
			public void run() {
				mainTimer.postDelayed(countingRunnable, 1000);
				doWhenCountDown(secToCount);
				secToCount--;
			}
		};

		return r;
	}

	/**
	 * @param states
	 * @param remainSec
	 * @return
	 */
	private Notification buildForegroundNotification(TimerStates states,
			int remainSec) {

		Intent startMAIntent = new Intent(this, MainActivity.class);
		PendingIntent pendingStartMAIntent = PendingIntent.getActivity(this, 0,
				startMAIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification.Builder builder = new Notification.Builder(this);
		builder.setOngoing(true).setContentTitle("FocusTimer")
				.setContentText(MainFragment.calSecToMS(remainSec))
				.setSmallIcon(android.R.drawable.presence_busy)
				.setContentIntent(pendingStartMAIntent);

		switch (states) {
		case COUNTING:
			builder.setTicker("Timer Counting...");
			break;
		case PAUSE:
			builder.setTicker("Timer pause");
			break;

		case RESET:
			break;
		}

		return builder.build();
	}

	public class TimerBinder extends Binder {

		public TimerService getService() {
			return TimerService.this;
		}

		public void registerActivity(Activity activity,
				TimerCallBack timerCallBack) {
			client = activity;
			callBack = timerCallBack;
		}

		public void unregisterActivity() {
			client = null;
			callBack = null;
		}

	}

	public interface TimerCallBack {
		void onCounting(int secRemain, int secTotal);

		/**
		 * @return next time section.
		 */
		Sections onTimeUp();

		void onPause(int secRemain, int secTotal);

		void onDiscard();
	}

	// -- getter and setter
	public TimerStates getTimerStates() {
		return timerStates;
	}

	public void setTimerStates(TimerStates timerStates) {
		this.timerStates = timerStates;
	}

}