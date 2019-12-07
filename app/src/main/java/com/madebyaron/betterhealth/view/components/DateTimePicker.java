package com.madebyaron.betterhealth.view.components;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.widget.EditText;

import com.madebyaron.betterhealth.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.function.Consumer;

public class DateTimePicker {

	private static SimpleDateFormat format = new SimpleDateFormat(
			"EEE, MMM dd, yyyy   hh:mm a",
			Locale.getDefault()
	);

	public static void openDateTimePickerDialog(
			Context context,
			Date date,
			Consumer<Date> onSelected
	) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		TimePickerDialog.OnTimeSetListener onTimeSelected = (view, hourOfDay, minute) -> {
			calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
			calendar.set(Calendar.MINUTE, minute);
			onSelected.accept(calendar.getTime());
		};

		TimePickerDialog timePickerDialog = new TimePickerDialog(context, R.style.DatePickerDialog,
				onTimeSelected,
				calendar.get(Calendar.HOUR),
				calendar.get(Calendar.MINUTE),
				false
		);

		DatePickerDialog.OnDateSetListener onDateSelected = (view, year, month, dayOfMonth) -> {
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, month);
			calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			timePickerDialog.show();
		};

		DatePickerDialog datePickerDialog = new DatePickerDialog(context, R.style.DatePickerDialog,
				onDateSelected,
				calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH)
		);

		datePickerDialog.show();
	}

	public static void openDateTimePickerDialog(
			Context context,
			Consumer<Date> onSelected
	) {
		openDateTimePickerDialog(context, new Date(), onSelected);
	}

	public static void setDateTimePickerText(EditText input, Date dateTime) {
		Editable text = input.getText();
		if (text != null) {
			text.clear();
			text.append(format.format(dateTime));
		}
	}
}