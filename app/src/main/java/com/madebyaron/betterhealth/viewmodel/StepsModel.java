package com.madebyaron.betterhealth.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.madebyaron.betterhealth.stepcounter.StepCounterService;
import com.madebyaron.betterhealth.database.StepsDB;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class StepsModel extends AndroidViewModel {

	private StepsDB stepsApi = new StepsDB(getApplication());

	private long refreshInterval = 1000L;
	private Timer refreshTimer = null;

	public MutableLiveData<String> steps = new MutableLiveData<>();
	public MutableLiveData<Boolean> showStartButton = new MutableLiveData<>();
	public MutableLiveData<Boolean> showStopButton = new MutableLiveData<>();

	public StepsModel(Application application) {
		super(application);
		onServiceRunning(StepCounterService.isRunning());
		StepCounterService.addRunningListener(this::onServiceRunning);
	}

	@Override
	protected void onCleared() {
		StepCounterService.removeRunningListener(this::onServiceRunning);
	}

	public void startStepCounter() {
		Context context = getApplication();
		context.startService(new Intent(context, StepCounterService.class));
	}

	public void stopStepCounter() {
		Context context = getApplication();
		context.stopService(new Intent(context, StepCounterService.class));
	}

	public void startRefreshing() {
		stopRefreshing();

		refreshTimer = new Timer();
		refreshTimer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				Date from = new Date();
				from.setTime(from.getTime() - TimeUnit.DAYS.toMillis(1));
				stepsApi.getStepCount(
						from, new Date(),
						stepCount -> steps.postValue(stepCount.toString())
				);
			}
		}, 0, refreshInterval);
	}

	public void stopRefreshing() {
		if (refreshTimer != null) refreshTimer.cancel();
		refreshTimer = null;
	}

	private void onServiceRunning(boolean running) {
		showStartButton.postValue(!running);
		showStopButton.postValue(running);
	}
}