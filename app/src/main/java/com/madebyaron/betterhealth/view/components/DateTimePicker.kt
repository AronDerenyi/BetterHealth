package com.madebyaron.betterhealth.view.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.EditText
import com.madebyaron.betterhealth.R
import kotlinx.android.synthetic.main.dialog_add_weight.*
import java.text.SimpleDateFormat
import java.util.*

fun openDateTimePickerDialog(
		context: Context,
		date: Date = Date(),
		onSelected: (Date) -> Unit) {

	val calendar = Calendar.getInstance()
	calendar.time = date

	val onTimeSelected = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
		calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
		calendar.set(Calendar.MINUTE, minute)
		onSelected(calendar.time)
	}

	val timePickerDialog = TimePickerDialog(context, R.style.DatePickerDialog,
			onTimeSelected,
			calendar.get(Calendar.HOUR),
			calendar.get(Calendar.MINUTE),
			false
	)

	val onDateSelected = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
		calendar.set(Calendar.YEAR, year)
		calendar.set(Calendar.MONTH, month)
		calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
		timePickerDialog.show()
	}

	val datePickerDialog = DatePickerDialog(context, R.style.DatePickerDialog,
			onDateSelected,
			calendar.get(Calendar.YEAR),
			calendar.get(Calendar.MONTH),
			calendar.get(Calendar.DAY_OF_MONTH)
	)

	datePickerDialog.show()
}

fun setDateTimePickerText(input: EditText, dateTime: Date) {
	input.text?.let {
		it.clear()
		it.append(SimpleDateFormat("EEE, MMM dd, yyyy   hh:mm a", Locale.getDefault()).format(dateTime))
	}
}