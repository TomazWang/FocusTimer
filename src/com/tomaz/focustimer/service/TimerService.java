package com.tomaz.focustimer.service;

import com.tomaz.focustimer.exception.UIHandlerMissingException;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

/**
 * This Service run in background and calculate the time.
 * Use Intent to push a integer with key "times"
 * 
 * You have to register a handler which control your UI
 * @author Tomaz Wang 
 * 
 */
public class TimerService extends Service {

	public final static String KEY_TIMES_TO_COUNT = "times";
	public final static String tag = "TimerServices";
	private int secToCount = 0;
	private int secTotal = 0;
	private TimerUIHandler uiHandler;
	private Handler mainTimer = new Handler();
	private Runnable countingRunnable = new Runnable() {

		@Override
		public void run() {
			mainTimer.postDelayed(countingRunnable, 1000);
			doWhenCountDown(secToCount);
			secToCount --;
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(tag, "on Bind");
		Bundle bundle = intent.getExtras();
		if(bundle != null){
			secTotal = bundle.getInt(KEY_TIMES_TO_COUNT);
			resetTimer();
		}else{
			Log.d(tag,"bundle is null");
		}
		return new TimerBinder();
	}
	

	
	
	public void startCount(){
		try{
			checkUIHandler();
		}catch (UIHandlerMissingException e){
			Log.e(tag,e.getMessage());
			return;
		}
		countingRunnable.run();
	}
	
	public int stopCount(){
		mainTimer.removeCallbacks(countingRunnable);
		return secToCount;
	}
	
	public void resetTimer(){
		secToCount = secTotal;
	}
	
	
	private void doWhenCountDown(int sec){
		if(sec <= 0){
			// times up
			stopCount();
			doWhenAfterTimesUp();
			uiHandler.timeChange(sec, secTotal);
		}else{
			// keep counting
			Log.d(tag, "counting : "+secToCount +" / "+ secTotal);
			uiHandler.timeChange(sec,secTotal);
		}
	}
	
	private void doWhenAfterTimesUp(){
		// TODO 
		resetTimer();
		Log.d(tag,"times up");
	}
	
	
	
	
	public void registerUIHandler(TimerUIHandler uiHandler){
		this.uiHandler = uiHandler;
	}
	
	private void checkUIHandler() throws UIHandlerMissingException{
		if(uiHandler == null){
			throw new UIHandlerMissingException();
		}
	}
	
	public class TimerBinder extends Binder{
		
		public TimerService getService(){
			return TimerService.this;
		}
	}
	
	
	
	public interface TimerUIHandler {
		void timeChange(int secRemain, int secTotal);
		
	}
}