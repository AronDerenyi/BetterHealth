package com.madebyaron.betterhealth.stepcounter

import android.app.*
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.madebyaron.betterhealth.R
import com.madebyaron.betterhealth.database.StepsDB
import com.madebyaron.betterhealth.view.MainActivity
import java.util.*
import kotlin.collections.HashSet

class StepCounterService : Service(), SensorEventListener {

	companion object {
		private const val NOTIFICATION_CHANNEL_ID = "step_counter_notification_channel"
		private const val NOTIFICATION_CHANNEL_NAME = "Step counter notification channel"

		private val runningListeners: MutableSet<(Boolean) -> Unit> = HashSet()

		fun addRunningListener(listener: (Boolean) -> Unit) {
			runningListeners.add(listener)
		}

		fun removeRunningListener(listener: (Boolean) -> Unit) {
			runningListeners.remove(listener)
		}

		var running: Boolean = false
			private set(value) {
				field = value
				runningListeners.forEach { it(value) }
			}
	}

	private lateinit var stepsDB: StepsDB
	private lateinit var sensorManager: SensorManager
	private lateinit var notificationManager: NotificationManager

	private var firstEvent: Boolean = true
	private var prevSteps: Int = 0

	private val saveIntervalMillis = 2000
	private var lastSave: Date = Date()
	private var accumulatedSteps: Int = 0

	override fun onCreate() {
		super.onCreate()

		stepsDB = StepsDB(this)
		sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
		notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

		val stepsSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
		if (stepsSensor != null) {
			sensorManager.registerListener(this, stepsSensor, SensorManager.SENSOR_DELAY_NORMAL)
		} else {
			Toast.makeText(this, "No Step Counter Sensor!", Toast.LENGTH_SHORT).show()
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			notificationManager.createNotificationChannel(
					NotificationChannel(
							NOTIFICATION_CHANNEL_ID,
							NOTIFICATION_CHANNEL_NAME,
							NotificationManager.IMPORTANCE_DEFAULT
					)
			)
		}

		val contentIntent = Intent(this, MainActivity::class.java)
		val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
				.setSmallIcon(R.drawable.ic_notification)
				.setColor(ContextCompat.getColor(this, R.color.color_accent))
				.setContentTitle(getString(R.string.step_counter_notification_title))
				.setContentText(getString(R.string.step_counter_notification_text))
				.setContentIntent(PendingIntent.getActivity(this, 0, contentIntent, 0))
				.build()

		startForeground(1, notification)

		running = true
	}

	override fun onDestroy() {
		super.onDestroy()
		sensorManager.unregisterListener(this)

		running = false
	}

	override fun onBind(intent: Intent): IBinder? {
		return null
	}

	override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

	}

	override fun onSensorChanged(event: SensorEvent) {
		val currentSteps = event.values[0].toInt()
		if (firstEvent) {
			firstEvent = false
			prevSteps = currentSteps
		}
		val steps = currentSteps - prevSteps
		prevSteps = currentSteps

		accumulatedSteps += steps
		val now = Date()
		if (saveIntervalMillis < now.time - lastSave.time) {
			lastSave = now
			stepsDB.addSteps(now, accumulatedSteps) {}
			accumulatedSteps = 0
		}
	}
}
