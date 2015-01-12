package com.tomaz.focustimer.service;

import java.util.HashMap;
import java.util.Map;

import com.tomaz.focustimer.MainActivity;
import com.tomaz.focustimer.exception.UIHandlerMissingException;
import com.tomaz.focustimer.fragment.MainFragment;
import com.tomaz.focustimer.other.Sessions;
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
	// private int sessionID = 0;
	// private boolean isRest = false ;
	
	
	private static final int FOREGROUND_NOTIFICATION_ID = 1;
	private NotificationManager nManager;
	private Notification.Builder fNoteBuilder;

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
		// 準備新的計時器
		secTotal = sec;
		secToCount = sec;
		
		// 開始計時
		startNewCount();
	}

	// start a new counter by default settings.
	public void startNewCount() {
		// change states
		setTimerStates(TimerStates.COUNTING);

		// start notification
		startForeground(FOREGROUND_NOTIFICATION_ID,
				buildForegroundNotification(timerStates, secToCount));
		nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		// start mainTimer
		countingRunnable.run();
	}

	// 停止計時器, 保留狀態
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
			callBack.onPause(secToCount, secTotal);
		} else {
			setTimerStates(TimerStates.RESET);
		}
		return secToCount;
	}

	// 繼續停止了的計時器
	public void resumeCount() {
		// change states
		setTimerStates(TimerStates.COUNTING);

		// change notification
		startForeground(FOREGROUND_NOTIFICATION_ID,
				buildForegroundNotification(timerStates, secToCount));
		nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		// resume timer
		countingRunnable.run();
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
		if (callBack == null){
			callBack.onTimeUp();
		}else{
			Intent intent = new Intent();
			intent.setClass(this,MainActivity.class);
			startActivity(intent);
			// TODO 
		}
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

		if (fNoteBuilder == null) {
			fNoteBuilder = new Notification.Builder(this);
		}
		fNoteBuilder.setOngoing(true).setContentTitle("FocusTimer")
				.setContentText(MainFragment.calSecToMS(remainSec))
				.setSmallIcon(android.R.drawable.presence_busy)
				.setOnlyAlertOnce(true).setContentIntent(pendingStartMAIntent);

		switch (states) {
		case COUNTING:
			fNoteBuilder.setTicker("Timer Counting...");
			break;
		case PAUSE:
			fNoteBuilder.setTicker("Timer pause");
			break;

		case RESET:
			break;
		}

		return fNoteBuilder.build();
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
		Sessions onTimeUp();

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