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
import kotlinx.android.synthetic.main.dialog_add_cigarettes.*
import java.util.*

class AddCigarettesDialog(private val activity: FragmentActivity) : Dialog(activity) {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.dialog_add_cigarettes)
		window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

		val model = ViewModelProviders.of(activity).get(ListModel::class.java)
		var date = Date()

		DateTimePicker.setDateTimePickerText(add_cigarettes_date_input, date)
		add_cigarettes_date_input.setOnClickListener {
			DateTimePicker.openDateTimePickerDialog(context, date) { dateTime ->
				date = dateTime
				DateTimePicker.setDateTimePickerText(add_cigarettes_date_input, date)
			}
		}

		add_cigarettes_add_button.setOnClickListener {
			dismiss()
			val countString = add_cigarettes_count_input.text.toString()
			model.addCigarettes(date,
					if (countString.isBlank()) {
						0
					} else {
						countString.toInt()
					}
			)
		}

		add_cigarettes_cancel_button.setOnClickListener {
			dismiss()
		}
	}
}