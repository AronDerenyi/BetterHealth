package com.madebyaron.betterhealth.view.list.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.madebyaron.betterhealth.R
import com.madebyaron.betterhealth.view.components.DateTimePicker
import com.madebyaron.betterhealth.viewmodel.ListModel
import kotlinx.android.synthetic.main.dialog_add_weight.*
import java.util.*

class AddWeightDialog(private val activity: FragmentActivity) : Dialog(activity) {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.dialog_add_weight)
		window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

		val model = ViewModelProviders.of(activity).get(ListModel::class.java)
		var date = Date()

		DateTimePicker.setDateTimePickerText(add_weight_date_input, date)
		add_weight_date_input.setOnClickListener {
			DateTimePicker.openDateTimePickerDialog(context, date) { dateTime ->
				date = dateTime
				DateTimePicker.setDateTimePickerText(add_weight_date_input, date)
			}
		}

		add_weight_add_button.setOnClickListener {
			dismiss()
			val weightString = add_weight_weight_input.text.toString()
			model.addWeight(date,
					if (weightString.isBlank()) {
						0.0f
					} else {
						weightString.toFloat()
					}
			)
		}

		add_weight_cancel_button.setOnClickListener {
			dismiss()
		}
	}
}