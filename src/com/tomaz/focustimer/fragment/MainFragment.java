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
	private View btn_start;
	private View btn_pAndR;
	private View btn_done;
	private View btn_discard;

	private TextView txt_clock;

	private View.OnClickListener generalOnClickListener;

	private TimerCallBack uiHandler;
	private TimerStates timerStates = TimerStates.RESET;
	private Sections nextSections = Sections.WORKING;

	// --- service things
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

		btn_pAndR = view.findViewById(R.id.btn_pauseAndResume);
		btn_done = view.findViewById(R.id.btn_done);
		btn_start = view.findViewById(R.id.btn_start);
		btn_discard = view.findViewById(R.id.btn_discard);

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

			// @Override
			// public void stopClock() {
			//
			// if (isRunning) {
			// view_fNs.setVisibility(View.GONE);
			// btn_start.setVisibility(View.VISIBLE);
			// isRunning = false;
			// }
			// }

			private void setClock(int secRemain, int secTotal) {

				int degree = ((secTotal - secRemain) * 360 / secTotal);
				pw_spinner.setProgress(degree);
				pw_spinner.pauseSpinning();

				txt_clock.setText(calSecToMS(secRemain));
				
			}

		};

		generalOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {

				MainActivity parent = (MainActivity) getActivity();

				switch (v.getId()) {

				// on click start
				case R.id.btn_start:
					view_fNs.setVisibility(View.VISIBLE);
					btn_start.setVisibility(View.GONE);
					startTimer();
					pw_spinner.spin();
					break;

				// on click pause or resume
				case R.id.btn_pauseAndResume:

					switch (timerStates) {

					case COUNTING:
						pause();
						pw_spinner.pauseSpinning();
						((Button) btn_pAndR).setText("Resume");

						break;

					case PAUSE:
						resume();
						((Button) btn_pAndR).setText("Pause");

					default:
						// not suppose to click this btn when RESET states
						Log.w(tag,
								"click pause/resume button while RESET states");
						break;
					}

					break;

				// on click discard
				// TODO

				case R.id.btn_discard:
					discard();
					break;

				// on click done
				case R.id.btn_done:
					done();
					// view_fNs.setVisibility(View.GONE);
					// btn_start.setVisibility(View.VISIBLE);
					// parent.stopTimer();
					// pw_spinner.stopSpinning();
					// txt_clock.setText("00 : 00");
					// isRunning = false;
					break;

				}
			}
		};

		btn_start.setOnClickListener(generalOnClickListener);
		btn_done.setOnClickListener(generalOnClickListener);
		btn_pAndR.setOnClickListener(generalOnClickListener);
		btn_discard.setOnClickListener(generalOnClickListener);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		bindService();
	}

	private void bindService() {
		Intent bindServiceIntent = new Intent(getActivity(), TimerService.class);
		connection = new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {
				Log.d(tag, "service disconnected");
				if (timerBinder != null) {
					timerBinder.unregisterActivity();
				}
				timerBinder = null;
				isBound = false;
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				Log.d(tag, "service connected!!");
				timerBinder = (TimerBinder) service;
				timerBinder.registerActivity(getActivity(), uiHandler);
				isBound = true;
			}
		};

		boolean isBindCall = getActivity().bindService(bindServiceIntent,
				connection, Service.BIND_AUTO_CREATE);
		Log.d(tag, "is binding call successfully? : " + isBindCall);

	}

	@Override
	public void onPause() {
		super.onPause();
		getActivity().unbindService(connection);
	}

	private void startTimer() {
		Log.i(tag, "start timer");
		Intent startTimerIntent = new Intent(getActivity(), TimerService.class);
		

		Bundle bundle = new Bundle();
		bundle.putInt(TimerService.KEY_TIMES_TO_COUNT, nextSections.getSec());
		startTimerIntent.putExtras(bundle);
		getActivity().startService(startTimerIntent);
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
	
	
	public static String calSecToMS(int s){
		
		int min = s / 60;
		int sec = s % 60;

		String sMin = (min < 10) ? ("0" + min) : ("" + min);
		String sSec = (sec < 10) ? ("0" + sec) : ("" + sec);

		return (sMin + " : " + sSec);
	}
}
