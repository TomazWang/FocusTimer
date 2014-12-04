package com.tomaz.focustimer;

import com.tomaz.focustimer.R;
import com.tomaz.focustimer.components.progressbar.ProgressWheel;
import com.tomaz.focustimer.service.TimerService;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import android.os.Build;

public class MainActivity extends Activity {

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
	 * A placeholder fragment containing a simple view.
	 */
	public static class MainFragment extends Fragment {

		private ProgressWheel pw_spinner;
		private Button btn_test;
		private Handler timer = new Handler();
		private Runnable increaseProgress;
		private static final String tag = "MainFragment";
		private static boolean isTesting = false;
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.fragment_main, container,
					false);

			pw_spinner = (ProgressWheel) view.findViewById(R.id.pw_spinner);
			pw_spinner.setBarLength(0);
			
			
			increaseProgress = new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Log.d(tag,"increse progress");
					pw_spinner.incrementProgress();
					timer.postDelayed(this, 60*1000/360);
				}
			};
			
			
			btn_test = (Button) view.findViewById(R.id.btn_add);
			btn_test.setText("Test");
			
//			
//			btn_test.setOnTouchListener(new OnTouchListener() {
//				
//				@Override
//				public boolean onTouch(View v, MotionEvent event) {
//					if(event.getAction() == MotionEvent.ACTION_DOWN){
//						Log.d(tag, "onDown start spinning");
//						pw_spinner.spin();
//						increaseProgress.run();
//					}
//					
//					if(event.getAction() == MotionEvent.ACTION_UP){
//						Log.d(tag, "onUp stop spinning");
//						pw_spinner.stopSpinning();
//						timer.removeCallbacks(increaseProgress);
//					}
//					
//					return false;
//				}
//			});
			
			
			btn_test.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(isTesting){
						// test off
						Log.d(tag,"test off");
						Intent stopServiceIntent = new Intent(getActivity(),TimerService.class);
						getActivity().stopService(stopServiceIntent);
						
						isTesting = false;
						btn_test.setText("Test");
					}else{
						// test on
						Log.d(tag,"test on");
						Intent startServiceIntent = new Intent(getActivity(),TimerService.class);
						Bundle bundle = new Bundle();
						bundle.putInt(TimerService.KEY_TIMES_TO_COUNT, 30);
						startServiceIntent.putExtras(bundle);
						getActivity().startService(startServiceIntent);
						
						isTesting = true;
						btn_test.setText("OFF");
					}
				}
			});
			
			
			return view;
		}
	}
	
	
}
