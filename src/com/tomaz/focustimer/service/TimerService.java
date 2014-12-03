package com.tomaz.focustimer.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

/**
 * the Service run in background to calculate the time.
 * use Intent to push a integer with key "times"
 * @author Tomaz Wang 
 * 
 */
public class TimerService extends Service {

	public final static String KEY_TIMES_TO_COUNT = "times";
	public final static String tag = "TimerServices";
	private int secToCount = 0;
	private Handler mainTimer = new Handler();
	private Runnable countingRunnable = new Runnable() {

		@Override
		public void run() {
			mainTimer.postDelayed(countingRunnable, 1000);
			contorlViews(secToCount);
			secToCount --;
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if (intent != null) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				secToCount = bundle.getInt(KEY_TIMES_TO_COUNT);
			}else{
				Log.d(tag,"bundle is null");
			}
		}
		return START_NOT_STICKY;
	}

	
	
	private void startCount(){
		countingRunnable.run();
	}
	
	private void stopCount(){
		mainTimer.removeCallbacks(countingRunnable);
	}
	
	private void contorlViews(int sec){
		if(sec <= 0){
			// times up
			Log.d(tag,"times up");
			stopCount();
			doWhenTimesUp();
		}else{
			// keep counting
			Log.d(tag, msg))
			// TODO
		}
	}
	
	private void doWhenTimesUp(){
		// TODO 
	}
	
	
	
}