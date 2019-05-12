package com.madebyaron.betterhealth.view.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.madebyaron.betterhealth.R
import com.madebyaron.betterhealth.data.Data
import com.madebyaron.betterhealth.databinding.FragmentListBinding
import com.madebyaron.betterhealth.view.list.dialogs.AddCaloriesDialog
import com.madebyaron.betterhealth.view.list.dialogs.AddCigarettesDialog
import com.madebyaron.betterhealth.view.list.dialogs.AddDrinkDialog
import com.madebyaron.betterhealth.view.list.dialogs.AddWeightDialog
import com.madebyaron.betterhealth.viewmodel.ListModel
import kotlinx.android.synthetic.main.fragment_list.*

class ListFragment : Fragment() {

	private lateinit var binding: FragmentListBinding
	private lateinit var adapter: ListAdapter
	private var addButtonExpanded = false

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		binding = FragmentListBinding.inflate(inflater, container, false)
		binding.lifecycleOwner = viewLifecycleOwner
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		activity?.let {
			adapter = ListAdapter(it)
			adapter.attachToRecyclerView(list_recycler_view)
			list_recycler_view.layoutManager = LinearLayoutManager(context)

			list_swipe_refresh.setColorSchemeColors(
					ContextCompat.getColor(it, R.color.color_control)
			)
		}
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		activity?.let {
			val model = ViewModelProviders.of(it).get(ListModel::class.java)

			binding.fragment = this
			binding.model = model

			model.dataList.observe(viewLifecycleOwner, Observer<List<Data>> { dataList ->
				adapter.dataList = dataList
			})
			model.loading.observe(viewLifecycleOwner, Observer<Boolean> { loading ->
				list_swipe_refresh.isRefreshing = loading
			})
			list_swipe_refresh.setOnRefreshListener {
				model.reload()
			}
		}
	}

	override fun onPause() {
		super.onPause()
		compressAddButton()
	}

	fun add() {
		if (addButtonExpanded) {
			compressAddButton()
		} else {
			expandAddButton()
		}
	}

	fun addWeight() {
		compressAddButton()
		activity?.let { activity -> AddWeightDialog(activity).show() }
	}

	fun addCalories() {
		compressAddButton()
		activity?.let { activity -> AddCaloriesDialog(activity).show() }
	}

	fun addDrink() {
		compressAddButton()
		activity?.let { activity -> AddDrinkDialog(activity).show() }
	}

	fun addCigarettes() {
		compressAddButton()
		activity?.let { activity -> AddCigarettesDialog(activity).show() }
	}

	private fun compressAddButton() {
		addButtonExpanded = false

		list_add_weight_button.hide()
		list_add_calories_button.hide()
		list_add_drink_button.hide()
		list_add_cigarettes_button.hide()

		list_add_button.startAnimation(
				AnimationUtils.loadAnimation(
						context,
						R.anim.compress_floating_action_button
				)
		)
	}

	private fun expandAddButton() {
		addButtonExpanded = true

		list_add_weight_button.show()
		list_add_calories_button.show()
		list_add_drink_button.show()
		list_add_cigarettes_button.show()

		list_add_button.startAnimation(
				AnimationUtils.loadAnimation(
						context,
						R.anim.expand_floating_action_button
				)
		)
	}
}