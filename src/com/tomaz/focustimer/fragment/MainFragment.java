package com.tomaz.focustimer.fragment;

import com.tomaz.focustimer.MainActivity;
import com.tomaz.focustimer.R;
import com.tomaz.focustimer.components.progressbar.ProgressWheel;
import com.tomaz.focustimer.other.Sections;
import com.tomaz.focustimer.other.TimerStates;
import com.tomaz.focustimer.service.TimerService.TimerCallBack;

import android.app.Fragment;
import android.os.Bundle;
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
			public void timeChange(int secRemain, int secTotal) {

				int degree = ((secTotal - secRemain) * 360 / secTotal);
				pw_spinner.setProgress(degree);
				pw_spinner.pauseSpinning();
				// Log.d(tag, "set progress =" + degree);

				int min = secRemain / 60;
				int sec = secRemain % 60;

				String sMin = (min < 10) ? ("0" + min) : ("" + min);
				String sSec = (sec < 10) ? ("0" + sec) : ("" + sec);

				txt_clock.setText(sMin + " : " + sSec);

			}

			@Override
			public void clockRunning() {
				if (!isRunning) {
					view_fNs.setVisibility(View.VISIBLE);
					btn_start.setVisibility(View.GONE);
					isRunning = true;

					btn_pAndR.setText("Pause");
					isPauseing = false;
				}
			}

			@Override
			public void stopClock() {

				if (isRunning) {
					view_fNs.setVisibility(View.GONE);
					btn_start.setVisibility(View.VISIBLE);
					isRunning = false;
				}
			}

			// @Override
			// public void clockPause() {
			// pw_spinner.pauseSpinning();
			// btn_pAndR.setText("Resume");
			// isPauseing = true;
			// }
		};

		((MainActivity) getActivity()).setUiHandler(uiHandler);

		generalOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {

				MainActivity parent = (MainActivity) getActivity();

				switch (v.getId()) {
				case R.id.btn_start:
					view_fNs.setVisibility(View.VISIBLE);
					btn_start.setVisibility(View.GONE);
					parent.startTimer();
					// ((MainActivity) getActivity()).startTiemr();
					pw_spinner.spin();
					isRunning = true;
					break;

				case R.id.btn_pauseAndResume:

					if (!isPauseing) {
						// Pause
						parent.pauseTimer();
						pw_spinner.pauseSpinning();
						btn_pAndR.setText("Resume");
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
