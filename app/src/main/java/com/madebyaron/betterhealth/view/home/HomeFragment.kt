package com.madebyaron.betterhealth.view.home

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.madebyaron.betterhealth.R
import com.madebyaron.betterhealth.databinding.FragmentHomeBinding
import com.madebyaron.betterhealth.view.components.GraphView
import com.madebyaron.betterhealth.viewmodel.HomeModel
import kotlinx.android.synthetic.main.fragment_home.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {

	private lateinit var binding: FragmentHomeBinding

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		binding = FragmentHomeBinding.inflate(inflater, container, false)
		binding.lifecycleOwner = viewLifecycleOwner
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		context?.let {
			home_swipe_refresh.setColorSchemeColors(
					ContextCompat.getColor(it, R.color.color_control)
			)
		}

		initGraph()
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		activity?.let {
			val model = ViewModelProviders.of(it).get(HomeModel::class.java)
			binding.model = model
			model.weightPoints.observe(viewLifecycleOwner, Observer<List<HomeModel.WeightPoint>> {
				weightPoints -> updateGraph(weightPoints)
			})
			model.loading.observe(viewLifecycleOwner, Observer<Boolean> { loading ->
				home_swipe_refresh.isRefreshing = loading
			})
			home_swipe_refresh.setOnRefreshListener {
				model.reload()
			}
		}
	}

	private fun initGraph() {
		val graph = dashboard_weight_graph

		val displayMetrics = graph.context.resources.displayMetrics

		graph.lineColor = ContextCompat.getColor(graph.context, R.color.color_accent)
		graph.lineWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, displayMetrics)

		graph.axisColor = ContextCompat.getColor(graph.context, R.color.color_content)
		graph.axisWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, displayMetrics)

		graph.gridColor = ContextCompat.getColor(graph.context, R.color.color_content_faded)
		graph.gridWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, displayMetrics)

		graph.textColor = ContextCompat.getColor(graph.context, R.color.color_content_light)
		graph.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, displayMetrics)
		graph.textPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, displayMetrics)

		val style = graph.context.obtainStyledAttributes(arrayOf(R.attr.fontFamily).toIntArray())
		ResourcesCompat.getFont(graph.context, style.getResourceId(0, 0))?.let { graph.textFont = it }
		style.recycle()

		val monthDateFormat = SimpleDateFormat("MMM", Locale.getDefault())
		val calendar = Calendar.getInstance()

		val xLabels = mutableListOf<GraphView.Label>()
		for (i in 0..2) {
			val month = Calendar.getInstance()
			month.timeInMillis = 0
			month.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
			month.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
			month.add(Calendar.MONTH, -i)
			xLabels.add(GraphView.Label(
					TimeUnit.MILLISECONDS.toDays(calendar.timeInMillis - month.timeInMillis).toFloat(),
					monthDateFormat.format(month.time)
			))
		}

		val yLabels = mutableListOf<GraphView.Label>()
		for (i in 0..200 step 2) yLabels.add(GraphView.Label(i.toFloat(), "$i kg"))

		graph.setXLabels(xLabels)
		graph.setYLabels(yLabels)

		graph.zoomX = -90f
		graph.offsetX = 90f
	}

	private fun updateGraph(weightPoints: List<HomeModel.WeightPoint>) {
		val graph = dashboard_weight_graph

		val date = Date()
		var minWeight = 0f
		var maxWeight = 0f
		val points = mutableListOf<GraphView.Point>()
		for (weightPoint in weightPoints) {
			if (points.isEmpty()) {
				minWeight = weightPoint.weight
				maxWeight = weightPoint.weight
			} else {
				if (weightPoint.weight < minWeight) minWeight = weightPoint.weight
				if (weightPoint.weight > maxWeight) maxWeight = weightPoint.weight
			}

			points.add(GraphView.Point(
					TimeUnit.MILLISECONDS.toDays(date.time - weightPoint.date.time).toFloat(),
					weightPoint.weight
			))
		}

		minWeight = (Math.floor(minWeight / 2.0) * 2).toFloat() - 1
		maxWeight = (Math.ceil(maxWeight / 2.0) * 2).toFloat() + 1

		graph.offsetY = minWeight
		graph.zoomY = maxWeight - minWeight
		graph.setPoints(points)
	}
}