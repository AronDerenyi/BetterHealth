package com.madebyaron.betterhealth.view.list.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.madebyaron.betterhealth.databinding.DialogAddCaloriesBinding;
import com.madebyaron.betterhealth.view.components.DateTimePicker;
import com.madebyaron.betterhealth.viewmodel.ListModel;

import java.util.Date;

public class AddCaloriesDialog extends Dialog {

    private final FragmentActivity activity;

    public AddCaloriesDialog(FragmentActivity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DialogAddCaloriesBinding binding = DialogAddCaloriesBinding.inflate(LayoutInflater.from(getContext()));
        setContentView(binding.getRoot());

        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        ListModel model = ViewModelProviders.of(activity).get(ListModel.class);
        Date date = new Date();

        DateTimePicker.setDateTimePickerText(binding.addCaloriesDateInput, date);
        binding.addCaloriesDateInput.setOnClickListener(view ->
                DateTimePicker.openDateTimePickerDialog(getContext(), date, dateTime -> {
                            date.setTime(dateTime.getTime());
                            DateTimePicker.setDateTimePickerText(binding.addCaloriesDateInput, date);
                        }
                )
        );

        binding.addCaloriesAddButton.setOnClickListener(view -> {
            dismiss();
            Editable caloriesInput = binding.addCaloriesCaloriesInput.getText();
            String caloriesString = caloriesInput == null ? "" : caloriesInput.toString();
            model.addCalories(date, caloriesString.isEmpty() ? 0 : Integer.parseInt(caloriesString));
        });

        binding.addCaloriesCancelButton.setOnClickListener(view -> dismiss());
    }
}