package com.madebyaron.betterhealth.view.steps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.madebyaron.betterhealth.databinding.FragmentStepsBinding
import com.madebyaron.betterhealth.viewmodel.StepsModel

class StepsFragment : Fragment() {

	private lateinit var binding: FragmentStepsBinding
	private lateinit var model: StepsModel

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		binding = FragmentStepsBinding.inflate(inflater, container, false)
		binding.lifecycleOwner = viewLifecycleOwner
		return binding.root
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		activity?.let {
			model = ViewModelProviders.of(it).get(StepsModel::class.java)
			binding.model = model
		}
	}

	override fun onResume() {
		super.onResume()
		model.startRefreshing()
	}

	override fun onPause() {
		super.onPause()
		model.stopRefreshing()
	}
}