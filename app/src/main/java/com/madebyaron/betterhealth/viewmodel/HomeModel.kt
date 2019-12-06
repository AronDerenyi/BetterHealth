package com.madebyaron.betterhealth.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.madebyaron.betterhealth.model.Data
import com.madebyaron.betterhealth.database.DataDB
import com.madebyaron.betterhealth.database.StepsDB
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class HomeModel(application: Application) : AndroidViewModel(application) {

	private val context: Context
		get() = getApplication()

	private val stepsApi = StepsDB(context)
	private val dataApi = DataDB(context)

	private var loadingCount: Int = 0
	val loading = MutableLiveData<Boolean>()

	val stepsThisDay = MutableLiveData<Int>()
	val stepsThisWeek = MutableLiveData<Int>()
	val stepsDailyAverage = MutableLiveData<Int>()

	val caloriesThisDay = MutableLiveData<Int>()
	val caloriesThisWeek = MutableLiveData<Int>()
	val caloriesDailyAverage = MutableLiveData<Int>()

	val weightPoints = MutableLiveData<List<WeightPoint>>()

	val daysSinceDrank = MutableLiveData<Int>()
	val unitsThisWeek = MutableLiveData<Int>()
	val unitsThisMonth = MutableLiveData<Int>()
	val unitsWeeklyAverage = MutableLiveData<Int>()

	val daysSinceSmoked = MutableLiveData<Int>()
	val cigarettesThisWeek = MutableLiveData<Int>()
	val cigarettesThisMonth = MutableLiveData<Int>()
	val cigarettesWeeklyAverage = MutableLiveData<Int>()

	init {
		reload()
	}

	fun reload() {
		reloadSteps()
		reloadCalories()
		reloadWeights()
		reloadDrinking()
		reloadSmoking()
	}

	private fun reloadSteps() {
		startLoading()
		stepsApi.getStepCount(getLastDay(), Date()) {
			stepsThisDay.postValue(it)
			stopLoading()
		}

		startLoading()
		stepsApi.getStepCount(getLastWeek(), Date()) {
			stepsThisWeek.postValue(it)
			stopLoading()
		}

		startLoading()
		stepsApi.getStepCount(getLastMonth(), Date()) {
			stepsDailyAverage.postValue((it / 30.0).roundToInt())
			stopLoading()
		}
	}

	private fun reloadCalories() {
		startLoading()
		dataApi.getDataSum(Data.TYPE_CALORIES, getLastDay(), Date()) {
			caloriesThisDay.postValue(it.roundToInt())
			stopLoading()
		}

		startLoading()
		dataApi.getDataSum(Data.TYPE_CALORIES, getLastWeek(), Date()) {
			caloriesThisWeek.postValue(it.roundToInt())
			stopLoading()
		}

		startLoading()
		dataApi.getDataSum(Data.TYPE_CALORIES, getLastMonth(), Date()) {
			caloriesDailyAverage.postValue((it / 30.0).roundToInt())
			stopLoading()
		}
	}

	private fun reloadWeights() {
		startLoading()
		dataApi.getDataList(Data.TYPE_WEIGHT) {
			val points: MutableList<WeightPoint> = ArrayList(it.size)
			for (data in it) points.add(WeightPoint(data.date, data.amount))
			weightPoints.postValue(points)
			stopLoading()
		}
	}

	private fun reloadDrinking() {
		startLoading()
		dataApi.getLastData(Data.TYPE_DRINK) {
			daysSinceDrank.postValue(
					if (it.date.time != 0L) {
						TimeUnit.MILLISECONDS.toDays(Date().time - it.date.time).toInt()
					} else {
						0
					}
			)
			stopLoading()
		}

		startLoading()
		dataApi.getDataSum(Data.TYPE_DRINK, getLastWeek(), Date()) {
			unitsThisWeek.postValue(it.roundToInt())
			stopLoading()
		}

		startLoading()
		dataApi.getDataSum(Data.TYPE_DRINK, getLastMonth(), Date()) {
			unitsThisMonth.postValue(it.roundToInt())
			stopLoading()
		}

		startLoading()
		dataApi.getDataSum(Data.TYPE_DRINK, getLastMonth(), Date()) {
			unitsWeeklyAverage.postValue((it / (30.0 / 7.0)).roundToInt())
			stopLoading()
		}
	}

	private fun reloadSmoking() {
		startLoading()
		dataApi.getLastData(Data.TYPE_CIGARETTES) {
			daysSinceSmoked.postValue(
					if (it.date.time != 0L) {
						TimeUnit.MILLISECONDS.toDays(Date().time - it.date.time).toInt()
					} else {
						0
					}
			)
			stopLoading()
		}

		startLoading()
		dataApi.getDataSum(Data.TYPE_CIGARETTES, getLastWeek(), Date()) {
			cigarettesThisWeek.postValue(it.roundToInt())
			stopLoading()
		}

		startLoading()
		dataApi.getDataSum(Data.TYPE_CIGARETTES, getLastMonth(), Date()) {
			cigarettesThisMonth.postValue(it.roundToInt())
			stopLoading()
		}

		startLoading()
		dataApi.getDataSum(Data.TYPE_CIGARETTES, getLastMonth(), Date()) {
			cigarettesWeeklyAverage.postValue((it / (30.0 / 7.0)).roundToInt())
			stopLoading()
		}
	}

	private fun startLoading() {
		if (loadingCount == 0) loading.postValue(true)
		loadingCount++
	}

	private fun stopLoading() {
		loadingCount--
		if (loadingCount == 0) loading.postValue(false)
	}

	private fun getLastDay(): Date {
		val date = Date()
		date.time -= TimeUnit.DAYS.toMillis(1)
		return date
	}

	private fun getLastWeek(): Date {
		val date = Date()
		date.time -= TimeUnit.DAYS.toMillis(7)
		return date
	}

	private fun getLastMonth(): Date {
		val date = Date()
		date.time -= TimeUnit.DAYS.toMillis(30)
		return date
	}

	data class WeightPoint(val date: Date, val weight: Float)
}