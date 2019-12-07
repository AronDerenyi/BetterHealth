package com.madebyaron.betterhealth.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.madebyaron.betterhealth.database.DataDB;
import com.madebyaron.betterhealth.model.Data;

import java.util.Date;
import java.util.List;

public class ListModel extends AndroidViewModel {

	private DataDB dataApi = new DataDB(getApplication());

	public final MutableLiveData<Boolean> loading = new MutableLiveData<>();
	public final MutableLiveData<List<Data>> dataList = new MutableLiveData<>();

	public ListModel(Application application) {
		super(application);

		reload();

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

	public void reload() {
		loading.postValue(true);
		dataApi.getDataList(data -> {
			dataList.postValue(data);
			loading.postValue(false);
		});
	}

	public void deleteData(Data data) {
		loading.postValue(true);
		dataApi.deleteData(data.getId(), (id) -> reload());
	}

	public void addWeight(Date date, float weight) {
		loading.postValue(true);
		dataApi.addData(Data.TYPE_WEIGHT, date, weight, (id) -> reload());
	}

	public void addCalories(Date date, int calories) {
		loading.postValue(true);
		dataApi.addData(Data.TYPE_CALORIES, date, calories, (id) -> reload());
	}

	public void addDrink(Date date, float amount, float percentage) {
		loading.postValue(true);
		dataApi.addData(Data.TYPE_DRINK, date, amount * percentage / 4.2f, (id) -> reload());
	}

	public void addCigarettes(Date date, int count) {
		loading.postValue(true);
		dataApi.addData(Data.TYPE_CIGARETTES, date, count, (id) -> reload());
	}
}