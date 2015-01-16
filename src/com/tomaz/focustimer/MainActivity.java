package com.tomaz.focustimer;

import com.tomaz.focustimer.R;
import com.tomaz.focustimer.components.progressbar.ProgressWheel;
import com.tomaz.focustimer.exception.UIHandlerMissingException;
import com.tomaz.focustimer.fragment.MainFragment;
import com.tomaz.focustimer.other.SPHelper;
import com.tomaz.focustimer.service.TimerService;
import com.tomaz.focustimer.service.TimerService.TimerBinder;
import com.tomaz.focustimer.service.TimerService.TimerCallBack;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
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

	private static final String tag = "MainActivity";

	public static final String CALL_FORM_SERVICE = "CALL_FORM_SERVICE";
	public static final int FLAG_NORMAL = 0;
	public static final int FLAG_TIME_UP = 1;
	public static final int FLAG_KEEP_COUNTING = 2;
	public static final int FLAG_STOP_COUNTING = 3;
	public static final int FLAG_DONE = 4;

	public static boolean isActive = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(tag,"onCreate");
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			// total restart
			getFragmentManager().beginTransaction()
					.add(R.id.container, new MainFragment()).commit();
		}

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.d(tag,"onStart");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(tag, "onResume");
		Intent intentFromService = getIntent();
		if (intentFromService != null) {
			// Activity is call by someone

			int flag = intentFromService.getIntExtra(CALL_FORM_SERVICE, -1);
			Log.v(tag, "flag = " + flag);
			if (flag >= 0) {
				switch (flag) {
				case FLAG_NORMAL:
					// 0
					break;
				case FLAG_TIME_UP:
					// 1
					FragmentManager fm = getFragmentManager();
					try {
						MainFragment f = (MainFragment) fm
								.findFragmentById(R.id.container);
						f.timeUp();
					} catch (Exception e) {
						Log.e(tag,
								"Can't convert Fragment to MainFragment \n"
										+ e.getMessage());
					}
					break;
				case FLAG_KEEP_COUNTING:
					Log.d(tag,"call MA keep counting");
					break;
				case FLAG_STOP_COUNTING:
					Log.d(tag,"call MA stop counting");
					break;
				case FLAG_DONE:
					Log.d(tag,"call MA done");
					break;
				default:
					Log.w(tag, "flag != def num");
				}
			} else {
				Log.w(tag, "flag <= 0");
			}
		}
		isActive = true;
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(tag, "on Stop");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		isActive = false;
		Log.d(tag, "on Pause");
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

	
	@Override
	protected void onNewIntent(Intent newIntent) {
		// TODO Auto-generated method stub
		super.onNewIntent(newIntent);
		setIntent(newIntent);
	}
	
	
}
