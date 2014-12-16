package com.tomaz.focustimer.service;

import java.util.HashMap;
import java.util.Map;

import com.tomaz.focustimer.exception.UIHandlerMissingException;
import com.tomaz.focustimer.other.TimerStates;

import android.app.Activity;
import android.app.Notification;
import android.app.Service;
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
			secTotal = bundle.getInt(KEY_TIMES_TO_COUNT);
			resetTimer();
		} else {
			Log.d(tag, "bundle is null");
		}
		startCount();

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

	public void startCount() {
		startForeground(FOREGROUND_NOTIFICATION_ID, buildForegroundNotification());
		countingRunnable.run();
	}

	public int stopCount() {
		mainTimer.removeCallbacks(countingRunnable);
		return secToCount;
	}

	public void resetTimer() {
		secToCount = secTotal;
	}

	private void doWhenCountDown(int sec) {
		if (sec <= 0) {
			// times up
			stopCount();
			doAfterTimesUp();
			if (callBack != null) {
				callBack.timeChange(sec, secTotal);
			}
		} else {
			// keep counting
			Log.i(tag, "counting : " + secToCount + " / " + secTotal);
			if (callBack != null) {
				callBack.clockRunning();
				callBack.timeChange(sec, secTotal);
			}
		}
	}

	private void doWhenPause() {
	}

	private void doAfterTimesUp() {
		// TODO
		resetTimer();
		Log.i(tag, "times up");
	}

	private Runnable buildCountdownTimerRunnable(){
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
	
	
	private Notification buildForegroundNotification() {
		Notification.Builder builder = new Notification.Builder(this);
		builder.setOngoing(true);

		builder.setContentTitle("FocusTimer");
		builder.setSmallIcon(android.R.drawable.presence_busy);
		builder.setTicker("Timer Running...");

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
		void timeChange(int secRemain, int secTotal);

		void clockRunning();

		void stopClock();
	}
}