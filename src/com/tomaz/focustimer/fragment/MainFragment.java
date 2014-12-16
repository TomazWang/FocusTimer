package com.tomaz.focustimer.fragment;

import com.tomaz.focustimer.MainActivity;
import com.tomaz.focustimer.R;
import com.tomaz.focustimer.components.progressbar.ProgressWheel;
import com.tomaz.focustimer.other.Sections;
import com.tomaz.focustimer.other.TimerStates;
import com.tomaz.focustimer.service.TimerService;
import com.tomaz.focustimer.service.TimerService.TimerBinder;
import com.tomaz.focustimer.service.TimerService.TimerCallBack;

import android.app.Fragment;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainFragment extends Fragment {

	private TimerStates state = TimerStates.RESET;

	private ProgressWheel pw_spinner;
	private Button btn_start;
	private Button btn_pAndR;
	private Button btn_stop;

	private TextView txt_clock;

	private View.OnClickListener generalOnClickListener;

	private TimerCallBack uiHandler;
	private TimerStates timerStates = TimerStates.RESET;
	private Sections nextSections = Sections.WORKING;

	//--- service things
	private TimerBinder timerBinder = null;
	private ServiceConnection connection;
	private boolean isBound = false;
	
	
	
	
	private static final String tag = "MainFragment";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main, container, false);

		pw_spinner = (ProgressWheel) view.findViewById(R.id.pw_spinner);
		pw_spinner.setBarLength(0);

		btn_pAndR = (Button) view.findViewById(R.id.btn_pauseAndResume);
		btn_stop = (Button) view.findViewById(R.id.btn_stop);
		btn_start = (Button) view.findViewById(R.id.btn_start);

		txt_clock = (TextView) view.findViewById(R.id.txt_clock);

		final View view_fNs = view.findViewById(R.id.view_fNs);

		uiHandler = new TimerCallBack() {

			@Override
			public void onCounting(int secRemain, int secTotal) {
				changeStates(TimerStates.COUNTING);
				setClock(secRemain, secTotal);

			}

			@Override
			public void onPause(int secRemain, int secTotal) {
				changeStates(TimerStates.PAUSE);
				setClock(secRemain, secTotal);
			}

			
			@Override
			public Sections onTimeUp() {

				timeUp();
				return nextSections;
			}
			
			
			@Override
			public void onDiscard() {
				// TODO
			}
			
			
//			@Override
//			public void stopClock() {
//
//				if (isRunning) {
//					view_fNs.setVisibility(View.GONE);
//					btn_start.setVisibility(View.VISIBLE);
//					isRunning = false;
//				}
//			}

			private void setClock(int secRemain, int secTotal) {

				int degree = ((secTotal - secRemain) * 360 / secTotal);
				pw_spinner.setProgress(degree);
				pw_spinner.pauseSpinning();

				int min = secRemain / 60;
				int sec = secRemain % 60;

				String sMin = (min < 10) ? ("0" + min) : ("" + min);
				String sSec = (sec < 10) ? ("0" + sec) : ("" + sec);

				txt_clock.setText(sMin + " : " + sSec);
			}

		};

		generalOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {

				MainActivity parent = (MainActivity) getActivity();

				switch (v.getId()) {
				case R.id.btn_start:
					view_fNs.setVisibility(View.VISIBLE);
					btn_start.setVisibility(View.GONE);
					startTimer();
					pw_spinner.spin();
					break;

				case R.id.btn_pauseAndResume:

					switch(timerStates){
					
					
					case COUNTING:
						pause();
						pw_spinner.pauseSpinning();
						btn_pAndR.setText("Resume");
						break;
						
						
						//TODO
					}
					
				
					
					if (timerStates == TimerStates.COUNTING) {
						// Pause
					
						isPauseing = true;
					} else {
						// Resume
						parent.resumeTimer();
						btn_pAndR.setText("Pause");
						isPauseing = false;
					}
					break;

				case R.id.btn_stop:
					view_fNs.setVisibility(View.GONE);
					btn_start.setVisibility(View.VISIBLE);
					parent.stopTimer();
					pw_spinner.stopSpinning();
					txt_clock.setText("00 : 00");
					isRunning = false;
					break;

				}
			}
		};

		btn_start.setOnClickListener(generalOnClickListener);
		btn_stop.setOnClickListener(generalOnClickListener);
		btn_pAndR.setOnClickListener(generalOnClickListener);

		return view;
	}

	
	@Override
	public void onResume() {
		super.onResume();
		bindService();
	}
	
	private void bindService(){
		Intent bindServiceIntent = new Intent(getActivity(), TimerService.class);
		connection = new ServiceConnection() {
			
			@Override
			public void onServiceDisconnected(ComponentName name) {
				Log.d(tag, "service disconnected");
				if(timerBinder != null){
					timerBinder.unregisterActivity();
				}
				timerBinder = null;
				isBound = false;
			}
			
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				Log.d(tag,"service connected!!");
				timerBinder = (TimerBinder)service;
				timerBinder.registerActivity(getActivity(), uiHandler);
				isBound = true;
			}
		};
		
		
		boolean isBindCall = getActivity().bindService(bindServiceIntent, connection, Service.BIND_AUTO_CREATE);
		Log.d(tag,"is binding call successfully? : "+isBindCall);
	
	}
	
	
	
	@Override
	public void onPause() {
		super.onPause();
		getActivity().unbindService(connection);
	}
	
	
	

	
	private void startTimer() {

	}

	private void stopTimer() {

	}

	private void pause() {

	}

	private void resume() {

	}

	private void done() {

	}

	private void discard() {

	}

	private void timeUp() {

	}

	private void changeStates(TimerStates state) {
		switch (state) {

		case RESET:
			break;
		case COUNTING:
			break;
		case PAUSE:
			break;
		}
	}
}
