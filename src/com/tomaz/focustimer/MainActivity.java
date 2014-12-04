package com.tomaz.focustimer;

import com.tomaz.focustimer.R;
import com.tomaz.focustimer.components.progressbar.ProgressWheel;
import com.tomaz.focustimer.exception.UIHandlerMissingException;
import com.tomaz.focustimer.service.TimerService;
import com.tomaz.focustimer.service.TimerService.TimerBinder;
import com.tomaz.focustimer.service.TimerService.TimerUIHandler;

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

	private int secToCount = 0;
	private TimerService timerService;
	private ServiceConnection connection;
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

	/**
	 * create and bind a TimerService and start the timer
	 * 
	 * @param sec
	 *            : total seconds to count
	 * @param uiHandler
	 *            : the TimerUIHandler to handle time change event
	 */
	private void bindTimerService(int sec, TimerUIHandler uiHandler) {
		Intent bindServiceIntent = new Intent(MainActivity.this,
				TimerService.class);
		Bundle bundle = new Bundle();
		bundle.putInt(TimerService.KEY_TIMES_TO_COUNT, sec);
		bindServiceIntent.putExtras(bundle);
		// TODO bind the service

		connection = new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub
				Log.d(tag, "service disconnected");

			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				// TODO Auto-generated method stub
				Log.d(tag, "service connected");
				TimerBinder binder = (TimerBinder) service;
				timerService = binder.getService();
			}
		};

		this.bindService(bindServiceIntent, connection, BIND_AUTO_CREATE);
		timerService.registerUIHandler(uiHandler);
	}

	private void startTiemr() {
		timerService.startCount();
	}

	/**
	 * pause the TimerService and return remainSeconds
	 * 
	 * @return remain sec
	 */
	private int pauseTimer() {
		return timerService.stopCount();
	}


	private void stopTimer(){
		unbindService(connection);
	}
	
	
	
	public int getSecToCount() {
		return secToCount;
	}

	public void setSecToCount(int secToCount) {
		this.secToCount = secToCount;
	}







	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class MainFragment extends Fragment {

		private ProgressWheel pw_spinner;
		private Button btn_start;
		private Button btn_pAndR;
		private Button btn_stop;
		
		private TextView txt_clock;
		
		private View.OnClickListener generalOnClickListener;
		
		private TimerUIHandler uiHandler;
		
		
		private static final String tag = "MainFragment";
		

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.fragment_main, container,
					false);

			pw_spinner = (ProgressWheel) view.findViewById(R.id.pw_spinner);
			pw_spinner.setBarLength(0);

			btn_pAndR = (Button)view.findViewById(R.id.btn_pauseAndResume);
			btn_stop = (Button)view.findViewById(R.id.btn_stop);
			btn_start = (Button) view.findViewById(R.id.btn_start);

			txt_clock = (TextView)view.findViewById(R.id.txt_clock);
			
			final View view_fNs = view.findViewById(R.id.view_fNs);

			
			uiHandler = new TimerUIHandler() {
				
				@Override
				public void timeChange(int secRemain, int secTotal) {
					// TODO Auto-generated method stub
					int degree = (secRemain/secTotal)*360;
					pw_spinner.setProgress(degree);
					
					int min = secRemain/60;
					int sec = secRemain%60;
					
					String sMin = (min<10)?("0"+min):(""+min);
					String sSec = (sec<10)?("0"+sec):(""+sec);
					
					if(sec%2 == 0){
						txt_clock.setText(sMin+" : "+sSec);
					}else{
						txt_clock.setText(sMin+"   "+sSec);
					}
					
				}
			};
			
			generalOnClickListener = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO
					switch(v.getId()){
					case R.id.btn_start:
						view_fNs.setVisibility(View.VISIBLE);
						btn_start.setVisibility(View.GONE);
						((MainActivity)getActivity()).bindTimerService(((MainActivity)getActivity()).getSecToCount(), uiHandler);
						((MainActivity)getActivity()).startTiemr();
						pw_spinner.spin();
						break;
						
					case R.id.btn_pauseAndResume:
						((MainActivity)getActivity()).pauseTimer();
						pw_spinner.pauseSpinning();
						break;
						
					case R.id.btn_stop:
						view_fNs.setVisibility(View.GONE);
						btn_start.setVisibility(View.VISIBLE);
						((MainActivity)getActivity()).stopTimer();
						pw_spinner.stopSpinning();
						break;
					
					}
				}
			};
			
			btn_start.setOnClickListener(generalOnClickListener);
			btn_stop.setOnClickListener(generalOnClickListener);
			btn_pAndR.setOnClickListener(generalOnClickListener);
			
			//
			// btn_test.setOnTouchListener(new OnTouchListener() {
			//
			// @Override
			// public boolean onTouch(View v, MotionEvent event) {
			// if(event.getAction() == MotionEvent.ACTION_DOWN){
			// Log.d(tag, "onDown start spinning");
			// pw_spinner.spin();
			// increaseProgress.run();
			// }
			//
			// if(event.getAction() == MotionEvent.ACTION_UP){
			// Log.d(tag, "onUp stop spinning");
			// pw_spinner.stopSpinning();
			// timer.removeCallbacks(increaseProgress);
			// }
			//
			// return false;
			// }
			// });


			return view;
		}

	}

}
