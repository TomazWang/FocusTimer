package com.tomaz.focustimer;

import com.tomaz.focustimer.R;
import com.tomaz.focustimer.components.progressbar.ProgressWheel;
import com.tomaz.focustimer.exception.UIHandlerMissingException;
import com.tomaz.focustimer.fragment.MainFragment;
import com.tomaz.focustimer.service.TimerService;
import com.tomaz.focustimer.service.TimerService.TimerBinder;
import com.tomaz.focustimer.service.TimerService.TimerCallBack;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.os.Build;

public class MainActivity extends Activity {

	private int secToCount = 2 * 60;
//	private TimerService timerService;
//	private TimerBinder timerBinder;
//	private ServiceConnection connection;
	private static final String tag = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new MainFragment()).commit();
		}

	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(tag, "on Stop");
//		if (isBound) {
//			unbindService(connection);
//		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		Log.d(tag, "onResume");
		// if (!isBound) {
		// bindTimerService();
		// }
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(tag, "onDestroy");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

//	private boolean isBound = false;

//	/**
//	 * create and bind a TimerService and start the timer
//	 * 
//	 * @param sec
//	 *            : total seconds to count
//	 * @param uiHandler
//	 *            : the TimerUIHandler to handle time change event
//	 */
//	public void bindTimerService() {
//		Intent bindServiceIntent = new Intent(MainActivity.this,
//				TimerService.class);
//
//		connection = new ServiceConnection() {
//
//			@Override
//			public void onServiceDisconnected(ComponentName name) {
//				Log.d(tag, "service disconnected");
//				if (timerBinder != null) {
//					timerBinder.unregisterActivity();
//				}
//				timerService = null;
//				isBound = false;
//			}
//
//			@Override
//			public void onServiceConnected(ComponentName name, IBinder service) {
//				Log.d(tag, "service connected");
//				timerBinder = (TimerBinder) service;
//				timerService = timerBinder.getService();
//				isBound = true;
//			}
//		};
//
//		boolean isBindCall = this.bindService(bindServiceIntent, connection,
//				BIND_AUTO_CREATE);
//		Log.d(tag, "isBindcall :" + isBindCall);
//
//	}

	/*
	 * ==== timer control ============================
	 */

	//
	// public void startTimer(int sec) {
//	 Intent startServiceIntent = new Intent(MainActivity.this,
//	 TimerService.class);
//	
//	 Bundle bundle = new Bundle();
//	 bundle.putInt(TimerService.KEY_TIMES_TO_COUNT, sec);
//	 startServiceIntent.putExtras(bundle);
//	 startService(startServiceIntent);
	//
	// }
	//
	// public void startTimer() {
	// startTimer(getSecToCount());
	// }
	//
	// public void stopTimer() {
//	 timerService.stopCount();
//	 Intent stopServiceIntent = new Intent(MainActivity.this,
//	 TimerService.class);
	//
	// stopService(stopServiceIntent);
	// // unbindService(connection);
	// }
	//
	// public void resumeTimer() {
	// if (isBound) {
	// timerService.startCount();
	// } else {
	// Log.e(tag, "Service not bound");
	// }
	// }
	//
	// /**
	// * pause the TimerService and return remainSeconds
	// *
	// * @return remain sec
	// */
	// public int pauseTimer() {
	// if (isBound) {
	// return timerService.stopCount();
	// } else {
	// Log.e(tag, "Service not bound");
	// }
	// return -1;
	// }
	//

	/*
	 * ==== Getter and Setter ==================================
	 */

	public int getSecToCount() {
		return secToCount;
	}

	public void setSecToCount(int secToCount) {
		this.secToCount = secToCount;
	}
	
	

}
