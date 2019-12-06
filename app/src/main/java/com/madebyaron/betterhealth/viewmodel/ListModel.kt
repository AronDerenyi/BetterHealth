package com.madebyaron.betterhealth.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.madebyaron.betterhealth.model.Data
import com.madebyaron.betterhealth.database.DataDB
import java.util.*

class ListModel(application: Application) : AndroidViewModel(application) {

	private val context: Context
		get() = getApplication()

	private val dataApi = DataDB(context)

	val loading = MutableLiveData<Boolean>()
	val dataList = MutableLiveData<List<Data>>()

	init {
		reload()

//		for (i in 1..50) {
//			val lunch = Calendar.getInstance()
//			lunch.set(Calendar.HOUR_OF_DAY, 13)
//			lunch.set(Calendar.MINUTE, (90 * Math.random()).toInt())
//			lunch.set(Calendar.SECOND, (60 * Math.random()).toInt())
//			lunch.set(Calendar.MILLISECOND, (1000 * Math.random()).toInt())
//			lunch.add(Calendar.DAY_OF_YEAR, -i)
//
//			val dinner = Calendar.getInstance()
//			dinner.set(Calendar.HOUR_OF_DAY, 20)
//			dinner.set(Calendar.MINUTE, (90 * Math.random()).toInt())
//			dinner.set(Calendar.SECOND, (60 * Math.random()).toInt())
//			dinner.set(Calendar.MILLISECOND, (1000 * Math.random()).toInt())
//			dinner.add(Calendar.DAY_OF_YEAR, -i)
//
//			addCalories(lunch.time, 500 + (300 * Math.random()).toInt())
//			addCalories(dinner.time, 300 + (300 * Math.random()).toInt())
//		}
	}

	fun reload() {
		loading.postValue(true)
		dataApi.getDataList {
			dataList.postValue(it)
			loading.postValue(false)
		}
	}

	fun deleteData(data: Data) {
		loading.postValue(true)
		dataApi.deleteData(data.id) {
			reload()
		}
	}

	fun addWeight(date: Date, weight: Float) {
		loading.postValue(true)
		dataApi.addData(Data.TYPE_WEIGHT, date, weight) {
			reload()
		}
	}

	fun addCalories(date: Date, calories: Int) {
		loading.postValue(true)
		dataApi.addData(Data.TYPE_CALORIES, date, calories.toFloat()) {
			reload()
		}
	}

	fun addDrink(date: Date, amount: Float, percentage: Float) {
		loading.postValue(true)
		dataApi.addData(Data.TYPE_DRINK, date, amount * percentage / 4.2f) {
			reload()
		}
	}

	fun addCigarettes(date: Date, count: Int) {
		loading.postValue(true)
		dataApi.addData(Data.TYPE_CIGARETTES, date, count.toFloat()) {
			reload()
		}
	}
}