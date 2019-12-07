package com.madebyaron.betterhealth.view.home;

import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.madebyaron.betterhealth.R;
import com.madebyaron.betterhealth.databinding.FragmentHomeBinding;
import com.madebyaron.betterhealth.view.components.GraphView;
import com.madebyaron.betterhealth.viewmodel.HomeModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {

	private FragmentHomeBinding binding;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = FragmentHomeBinding.inflate(inflater, container, false);
		binding.setLifecycleOwner(getViewLifecycleOwner());
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		if (getContext() != null) {
			binding.homeSwipeRefresh.setColorSchemeColors(
					ContextCompat.getColor(getContext(), R.color.color_control)
			);
		}

		initGraph();
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getActivity() != null) {
			HomeModel model = ViewModelProviders.of(getActivity()).get(HomeModel.class);
			binding.setModel(model);
			model.weightPoints.observe(getViewLifecycleOwner(), this::updateGraph);
			model.loading.observe(getViewLifecycleOwner(), binding.homeSwipeRefresh::setRefreshing);
			binding.homeSwipeRefresh.setOnRefreshListener(model::reload);
		}
	}

	private void initGraph() {
		GraphView graph = binding.dashboardWeightGraph;

		DisplayMetrics displayMetrics = graph
				.getContext()
				.getResources()
				.getDisplayMetrics();

		graph.setLineColor(ContextCompat.getColor(graph.getContext(), R.color.color_accent));
		graph.setLineWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, displayMetrics));

		graph.setAxisColor(ContextCompat.getColor(graph.getContext(), R.color.color_content));
		graph.setAxisWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, displayMetrics));

		graph.setGridColor(ContextCompat.getColor(graph.getContext(), R.color.color_content_faded));
		graph.setGridWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, displayMetrics));

		graph.setTextColor(ContextCompat.getColor(graph.getContext(), R.color.color_content_light));
		graph.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, displayMetrics));
		graph.setTextPadding(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, displayMetrics));

		TypedArray style = graph.getContext().obtainStyledAttributes(new int[]{R.attr.fontFamily});
		Typeface font = ResourcesCompat.getFont(graph.getContext(), style.getResourceId(0, 0));
		if (font != null) graph.setTextFont(font);
		style.recycle();

		SimpleDateFormat monthDateFormat = new SimpleDateFormat("MMM", Locale.getDefault());
		Calendar calendar = Calendar.getInstance();

		List<GraphView.Label> xLabels = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			Calendar month = Calendar.getInstance();
			month.setTimeInMillis(0);
			month.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
			month.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
			month.add(Calendar.MONTH, -i);
			xLabels.add(new GraphView.Label(
					TimeUnit.MILLISECONDS.toDays(calendar.getTimeInMillis() - month.getTimeInMillis()),
					monthDateFormat.format(month.getTime())
			));
		}

		List<GraphView.Label> yLabels = new ArrayList<>();
		for (int i = 0; i <= 200; i += 2) yLabels.add(new GraphView.Label(i, i + " kg"));

		graph.setXLabels(xLabels);
		graph.setYLabels(yLabels);

		graph.setZoomX(-90f);
		graph.setOffsetX(90f);
	}

	private void updateGraph(List<HomeModel.WeightPoint> weightPoints) {
		GraphView graph = binding.dashboardWeightGraph;

		Date date = new Date();
		float minWeight = 0;
		float maxWeight = 0;
		List<GraphView.Point> points = new ArrayList<>();
		for (HomeModel.WeightPoint weightPoint : weightPoints) {
			if (points.isEmpty()) {
				minWeight = weightPoint.getWeight();
				maxWeight = weightPoint.getWeight();
			} else {
				if (weightPoint.getWeight() < minWeight) minWeight = weightPoint.getWeight();
				if (weightPoint.getWeight() > maxWeight) maxWeight = weightPoint.getWeight();
			}

			points.add(new GraphView.Point(
					TimeUnit.MILLISECONDS.toDays(date.getTime() - weightPoint.getDate().getTime()),
					weightPoint.getWeight()
			));
		}

		minWeight = (float) Math.floor(minWeight / 2.0) * 2 - 1;
		maxWeight = (float) Math.ceil(maxWeight / 2.0) * 2 + 1;

		graph.setOffsetY(minWeight);
		graph.setZoomY(maxWeight - minWeight);
		graph.setPoints(points);
	}
}