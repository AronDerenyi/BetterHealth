package com.madebyaron.betterhealth.stepcounter;

import android.app.*;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import com.madebyaron.betterhealth.R;
import com.madebyaron.betterhealth.database.StepsDB;
import com.madebyaron.betterhealth.view.MainActivity;
import java.util.*;
import java.util.function.Consumer;

public class StepCounterService extends Service implements SensorEventListener {

	private static final String NOTIFICATION_CHANNEL_ID = "step_counter_notification_channel";
	private static final String NOTIFICATION_CHANNEL_NAME = "Step counter notification channel";

	private static final Set<Consumer<Boolean>> runningListeners = new HashSet<>();
	private static boolean running = false;

	public static void addRunningListener(Consumer<Boolean> listener) {
		runningListeners.add(listener);
	}

	public static void removeRunningListener(Consumer<Boolean> listener) {
		runningListeners.remove(listener);
	}

	public static boolean isRunning() {
		return running;
	}

	private static void setRunning(boolean running) {
		StepCounterService.running = running;
		runningListeners.forEach(listener -> listener.accept(running));
	}

	private StepsDB stepsDB;
	private SensorManager sensorManager;
	private NotificationManager notificationManager;

	private boolean firstEvent = true;
	private int prevSteps = 0;

	private final int saveIntervalMillis = 2000;
	private Date lastSave = new Date();
	private int accumulatedSteps = 0;

	@Override
	public void onCreate() {
		super.onCreate();

		stepsDB = new StepsDB(this);
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		Sensor stepsSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
		if (stepsSensor != null) {
			sensorManager.registerListener(this, stepsSensor, SensorManager.SENSOR_DELAY_NORMAL);
		} else {
			Toast.makeText(this, "No Step Counter Sensor!", Toast.LENGTH_SHORT).show();
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			notificationManager.createNotificationChannel(new NotificationChannel(
					NOTIFICATION_CHANNEL_ID,
					NOTIFICATION_CHANNEL_NAME,
					NotificationManager.IMPORTANCE_DEFAULT
			));
		}

		Intent contentIntent = new Intent(this, MainActivity.class);
		Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
				.setSmallIcon(R.drawable.ic_notification)
				.setColor(ContextCompat.getColor(this, R.color.color_accent))
				.setContentTitle(getString(R.string.step_counter_notification_title))
				.setContentText(getString(R.string.step_counter_notification_text))
				.setContentIntent(PendingIntent.getActivity(this, 0, contentIntent, 0))
				.build();

		startForeground(1, notification);

		setRunning(true);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		sensorManager.unregisterListener(this);

		setRunning(false);
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		int currentSteps = (int) event.values[0];
		if (firstEvent) {
			firstEvent = false;
			prevSteps = currentSteps;
		}
		int steps = currentSteps - prevSteps;
		prevSteps = currentSteps;

		accumulatedSteps += steps;
		Date now = new Date();
		if (saveIntervalMillis < now.getTime() - lastSave.getTime()) {
			lastSave = now;
			stepsDB.addSteps(now, accumulatedSteps, () -> {});
			accumulatedSteps = 0;
		}
	}
}
