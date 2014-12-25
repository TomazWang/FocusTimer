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

	private View.OnClickListener generalOnClickListener;

	private TimerStates timerStates = TimerStates.RESET;
	private Sections nextSections = Sections.WORKING;

	// --- service things
	private TimerCallBack uiHandler;
	private TimerBinder timerBinder = null;
	private ServiceConnection connection;
	private boolean isBound = false;

	private static final String tag = "MainFragment";

	// --- widgets
	private View view_fNs;
	private ProgressWheel pw_spinner;
	private View btn_start;
	private View btn_pAndR;
	private View btn_done;
	private View btn_discard;
	private TextView txt_clock;

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

		view_fNs = view.findViewById(R.id.view_fNs);

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

				switch (v.getId()) {

				// on click start
				case R.id.btn_start:
					startTimer();
					break;

				// on click pause or resume
				case R.id.btn_pauseAndResume:

					switch (timerStates) {

					case COUNTING:
						// when pause click
						pause();
						break;

					case PAUSE:
						resume();
						break;

					case RESET:
						// not suppose to click this btn when RESET states
						Log.w(tag, "click pause/resume btn while RESET states");
						break;
					}

					break;

				case R.id.btn_discard:
					discard();
					break;

				// on click done
				case R.id.btn_done:
					done();
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
				TimerStates ts = timerBinder.getService().getTimerStates();
				changeStates(ts);
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

		// change states
		changeStates(TimerStates.COUNTING);

		// start timer service
		Intent startTimerIntent = new Intent(getActivity(), TimerService.class);
		Bundle bundle = new Bundle();
		bundle.putInt(TimerService.KEY_TIMES_TO_COUNT, nextSections.getSec());
		startTimerIntent.putExtras(bundle);
		getActivity().startService(startTimerIntent);

	}

	private void stopTimer() {
		Log.i(tag, "stop timer");

		// change states
		changeStates(TimerStates.RESET);

		// stop timer service
		timerBinder.getService().stopCount(true);
		Intent stopServiceIntent = new Intent(getActivity(), TimerService.class);
		getActivity().stopService(stopServiceIntent);

		// set next section in done() or discard()
	}

	private void pause() {
		changeStates(TimerStates.PAUSE);
		timerBinder.getService().stopCount(false);
		// TODO
	}

	private void resume() {
		changeStates(TimerStates.COUNTING);
		timerBinder.getService().resumeCount();
		// TODO
	}

	private void done() {
		stopTimer();
		setNextSections(Sections.WORKING);
	}

	private void discard() {
		// TODO
		// - add AlertDialog

		stopTimer();
		setNextSections(Sections.WORKING);
	}

	private void timeUp() {
		// change states
		changeStates(TimerStates.RESET);

		// TODO
		// - add AlerDialog
		// to ask user to continue(either rest or next working section) or done

	}

	private void changeStates(TimerStates state) {
		switch (state) {

		case RESET:
			view_fNs.setVisibility(View.GONE);
			btn_start.setVisibility(View.VISIBLE);

			pw_spinner.stopSpinning();
			txt_clock.setText("00 : 00");
			break;
		case COUNTING:
			view_fNs.setVisibility(View.VISIBLE);
			btn_start.setVisibility(View.GONE);

			pw_spinner.spin();
			((Button) btn_pAndR).setText("Pause");
			break;
		case PAUSE:
			view_fNs.setVisibility(View.VISIBLE);
			btn_start.setVisibility(View.GONE);

			pw_spinner.pauseSpinning();
			((Button) btn_pAndR).setText("Resume");
			break;
		}
		setState(state);
	}

	// -- SP method
	// TODO save states in shared preference.
	

	// -- getter and setter
	public TimerStates getState() {
		return timerStates;
	}

	public void setState(TimerStates state) {
		this.timerStates = state;
	}

	public Sections getNextSections() {
		return nextSections;
	}

	public void setNextSections(Sections nextSections) {
		this.nextSections = nextSections;
	}

	// -- public method
	public static String calSecToMS(int s) {

		int min = s / 60;
		int sec = s % 60;

		String sMin = (min < 10) ? ("0" + min) : ("" + min);
		String sSec = (sec < 10) ? ("0" + sec) : ("" + sec);

		return (sMin + " : " + sSec);
	}

}
