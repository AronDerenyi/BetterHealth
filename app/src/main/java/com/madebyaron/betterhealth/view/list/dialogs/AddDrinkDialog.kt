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
import kotlinx.android.synthetic.main.dialog_add_drink.*
import java.util.*

class AddDrinkDialog(private val activity: FragmentActivity) : Dialog(activity) {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.dialog_add_drink)
		window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

		val model = ViewModelProviders.of(activity).get(ListModel::class.java)
		var date = Date()

		DateTimePicker.setDateTimePickerText(add_drink_date_input, date)
		add_drink_date_input.setOnClickListener {
			DateTimePicker.openDateTimePickerDialog(context, date) { dateTime ->
				date = dateTime
				DateTimePicker.setDateTimePickerText(add_drink_date_input, date)
			}
		}

		add_drink_add_button.setOnClickListener {
			dismiss()
			val amountString = add_drink_amount_input.text.toString()
			val percentageString = add_drink_percentage_input.text.toString()
			model.addDrink(date,
					if (amountString.isBlank()) {
						0.0f
					} else {
						amountString.toFloat()
					},
					if (percentageString.isBlank()) {
						0.0f
					} else {
						percentageString.toFloat()
					}
			)
		}

		add_drink_cancel_button.setOnClickListener {
			dismiss()
		}
	}
}