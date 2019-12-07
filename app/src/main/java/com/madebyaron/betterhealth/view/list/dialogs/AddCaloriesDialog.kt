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
import kotlinx.android.synthetic.main.dialog_add_calories.*
import java.util.*

class AddCaloriesDialog(private val activity: FragmentActivity) : Dialog(activity) {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.dialog_add_calories)
		window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

		val model = ViewModelProviders.of(activity).get(ListModel::class.java)
		var date = Date()

		DateTimePicker.setDateTimePickerText(add_calories_date_input, date)
		add_calories_date_input.setOnClickListener {
			DateTimePicker.openDateTimePickerDialog(context, date) { dateTime ->
				date = dateTime
				DateTimePicker.setDateTimePickerText(add_calories_date_input, date)
			}
		}

		add_calories_add_button.setOnClickListener {
			dismiss()
			val caloriesString = add_calories_calories_input.text.toString()
			model.addCalories(date,
					if (caloriesString.isBlank()) {
						0
					} else {
						caloriesString.toInt()
					}
			)
		}

		add_calories_cancel_button.setOnClickListener {
			dismiss()
		}
	}
}