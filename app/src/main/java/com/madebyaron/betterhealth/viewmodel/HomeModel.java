package com.madebyaron.betterhealth.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.madebyaron.betterhealth.database.DataDB;
import com.madebyaron.betterhealth.database.StepsDB;
import com.madebyaron.betterhealth.model.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HomeModel extends AndroidViewModel {

	private StepsDB stepsApi = new StepsDB(getApplication());
	private DataDB dataApi = new DataDB(getApplication());

	private int loadingCount = 0;
	public final MutableLiveData<Boolean> loading = new MutableLiveData<>();

	public final MutableLiveData<Integer> stepsThisDay = new MutableLiveData<>();
	public final MutableLiveData<Integer> stepsThisWeek = new MutableLiveData<>();
	public final MutableLiveData<Integer> stepsDailyAverage = new MutableLiveData<>();

	public final MutableLiveData<Integer> caloriesThisDay = new MutableLiveData<>();
	public final MutableLiveData<Integer> caloriesThisWeek = new MutableLiveData<>();
	public final MutableLiveData<Integer> caloriesDailyAverage = new MutableLiveData<>();

	public final MutableLiveData<List<WeightPoint>> weightPoints = new MutableLiveData<>();

	public final MutableLiveData<Integer> daysSinceDrank = new MutableLiveData<>();
	public final MutableLiveData<Integer> unitsThisWeek = new MutableLiveData<>();
	public final MutableLiveData<Integer> unitsThisMonth = new MutableLiveData<>();
	public final MutableLiveData<Integer> unitsWeeklyAverage = new MutableLiveData<>();

	public final MutableLiveData<Integer> daysSinceSmoked = new MutableLiveData<>();
	public final MutableLiveData<Integer> cigarettesThisWeek = new MutableLiveData<>();
	public final MutableLiveData<Integer> cigarettesThisMonth = new MutableLiveData<>();
	public final MutableLiveData<Integer> cigarettesWeeklyAverage = new MutableLiveData<>();

	public HomeModel(Application application) {
		super(application);

		reload();
	}

	public void reload() {
		reloadSteps();
		reloadCalories();
		reloadWeights();
		reloadDrinking();
		reloadSmoking();
	}

	private void reloadSteps() {
		startLoading();
		stepsApi.getStepCount(getLastDay(), new Date(), steps -> {
			stepsThisDay.postValue(steps);
			stopLoading();
		});

		startLoading();
		stepsApi.getStepCount(getLastWeek(), new Date(), steps -> {
			stepsThisWeek.postValue(steps);
			stopLoading();
		});

		startLoading();
		stepsApi.getStepCount(getLastMonth(), new Date(), steps -> {
			stepsDailyAverage.postValue(Math.round(steps / 30.0f));
			stopLoading();
		});
	}

	private void reloadCalories() {
		startLoading();
		dataApi.getDataSum(Data.TYPE_CALORIES, getLastDay(), new Date(), calories -> {
			caloriesThisDay.postValue(Math.round(calories));
			stopLoading();
		});

		startLoading();
		dataApi.getDataSum(Data.TYPE_CALORIES, getLastWeek(), new Date(), calories -> {
			caloriesThisWeek.postValue(Math.round(calories));
			stopLoading();
		});

		startLoading();
		dataApi.getDataSum(Data.TYPE_CALORIES, getLastMonth(), new Date(), calories -> {
			caloriesDailyAverage.postValue(Math.round(calories / 30.0f));
			stopLoading();
		});
	}

	private void reloadWeights() {
		startLoading();
		dataApi.getDataList(Data.TYPE_WEIGHT, weights -> {
			List<WeightPoint> points = new ArrayList<>(weights.size());
			for (Data data : weights) points.add(new WeightPoint(data.getDate(), data.getAmount()));
			weightPoints.postValue(points);
			stopLoading();
		});
	}

	private void reloadDrinking() {
		startLoading();
		dataApi.getLastData(Data.TYPE_DRINK, drink -> {
			daysSinceDrank.postValue(drink.getDate().getTime() != 0L ?
					(int) TimeUnit.MILLISECONDS.toDays(new Date().getTime() - drink.getDate().getTime()) :
					0
			);
			stopLoading();
		});

		startLoading();
		dataApi.getDataSum(Data.TYPE_DRINK, getLastWeek(), new Date(), drinks -> {
			unitsThisWeek.postValue(Math.round(drinks));
			stopLoading();
		});

		startLoading();
		dataApi.getDataSum(Data.TYPE_DRINK, getLastMonth(), new Date(), drinks -> {
			unitsThisMonth.postValue(Math.round(drinks));
			stopLoading();
		});

		startLoading();
		dataApi.getDataSum(Data.TYPE_DRINK, getLastMonth(), new Date(), drinks -> {
			unitsWeeklyAverage.postValue(Math.round(drinks / (30.0f / 7.0f)));
			stopLoading();
		});
	}

	private void reloadSmoking() {
		startLoading();
		dataApi.getLastData(Data.TYPE_CIGARETTES, cigarette -> {
			daysSinceSmoked.postValue(cigarette.getDate().getTime() != 0L ?
					(int) TimeUnit.MILLISECONDS.toDays(new Date().getTime() - cigarette.getDate().getTime()) :
					0
			);
			stopLoading();
		});

		startLoading();
		dataApi.getDataSum(Data.TYPE_CIGARETTES, getLastWeek(), new Date(), cigarettes -> {
			cigarettesThisWeek.postValue(Math.round(cigarettes));
			stopLoading();
		});

		startLoading();
		dataApi.getDataSum(Data.TYPE_CIGARETTES, getLastMonth(), new Date(), cigarettes -> {
			cigarettesThisMonth.postValue(Math.round(cigarettes));
			stopLoading();
		});

		startLoading();
		dataApi.getDataSum(Data.TYPE_CIGARETTES, getLastMonth(), new Date(), cigarettes -> {
			cigarettesWeeklyAverage.postValue(Math.round(cigarettes / (30.0f / 7.0f)));
			stopLoading();
		});
	}

	private void startLoading() {
		if (loadingCount == 0) loading.postValue(true);
		loadingCount++;
	}

	private void stopLoading() {
		loadingCount--;
		if (loadingCount == 0) loading.postValue(false);
	}

	private Date getLastDay() {
		Date date = new Date();
		date.setTime(date.getTime() - TimeUnit.DAYS.toMillis(1));
		return date;
	}

	private Date getLastWeek() {
		Date date = new Date();
		date.setTime(date.getTime() - TimeUnit.DAYS.toMillis(7));
		return date;
	}

	private Date getLastMonth() {
		Date date = new Date();
		date.setTime(date.getTime() - TimeUnit.DAYS.toMillis(30));
		return date;
	}

	public static class WeightPoint {

		private final Date date;
		private final float weight;

		public WeightPoint(Date date, float weight) {
			this.date = date;
			this.weight = weight;
		}

		public Date getDate() {
			return date;
		}

		public float getWeight() {
			return weight;
		}
	}
}