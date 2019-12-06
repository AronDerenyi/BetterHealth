package com.madebyaron.betterhealth.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.madebyaron.betterhealth.stepcounter.StepCounterService
import com.madebyaron.betterhealth.database.StepsDB
import java.util.*
import java.util.concurrent.TimeUnit

class StepsModel(application: Application) : AndroidViewModel(application) {

	private val context: Context
		get() = getApplication()

	private val stepsApi = StepsDB(context)

	private val refreshInterval = 1000L
	private var refreshTimer: Timer? = null

	val steps = MutableLiveData<String>()
	val showStartButton = MutableLiveData<Boolean>()
	val showStopButton = MutableLiveData<Boolean>()

	init {
		onServiceRunning(StepCounterService.running)
		StepCounterService.addRunningListener(this::onServiceRunning)
	}

	override fun onCleared() {
		StepCounterService.removeRunningListener(this::onServiceRunning)
	}

	fun startStepCounter() {
		context.startService(Intent(context, StepCounterService::class.java))
	}

	fun stopStepCounter() {
		context.stopService(Intent(context, StepCounterService::class.java))
	}

	fun startRefreshing() {
		stopRefreshing()

		refreshTimer = Timer()
		refreshTimer?.scheduleAtFixedRate(object : TimerTask() {
			override fun run() {
				val from = Date()
				from.time -= TimeUnit.DAYS.toMillis(1)
				stepsApi.getStepCount(from, Date()) { steps.postValue(it.toString()) }
			}
		}, 0, refreshInterval)
	}

	fun stopRefreshing() {
		refreshTimer?.cancel()
		refreshTimer = null
	}

	private fun onServiceRunning(running: Boolean) {
		showStartButton.postValue(!running)
		showStopButton.postValue(running)
	}
}