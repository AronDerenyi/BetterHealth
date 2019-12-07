package com.madebyaron.betterhealth.view.list.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.madebyaron.betterhealth.databinding.DialogAddDrinkBinding;
import com.madebyaron.betterhealth.view.components.DateTimePicker;
import com.madebyaron.betterhealth.viewmodel.ListModel;

import java.util.*;

public class AddDrinkDialog extends Dialog {

    private final FragmentActivity activity;

    public AddDrinkDialog(FragmentActivity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DialogAddDrinkBinding binding = DialogAddDrinkBinding.inflate(LayoutInflater.from(getContext()));
        setContentView(binding.getRoot());

        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        ListModel model = ViewModelProviders.of(activity).get(ListModel.class);
        Date date = new Date();

        DateTimePicker.setDateTimePickerText(binding.addDrinkDateInput, date);
        binding.addDrinkDateInput.setOnClickListener(view ->
                DateTimePicker.openDateTimePickerDialog(getContext(), date, dateTime -> {
                            date.setTime(dateTime.getTime());
                            DateTimePicker.setDateTimePickerText(binding.addDrinkDateInput, date);
                        }
                )
        );

        binding.addDrinkAddButton.setOnClickListener(view -> {
            dismiss();
            Editable amountInput = binding.addDrinkAmountInput.getText();
            Editable percentageInput = binding.addDrinkPercentageInput.getText();
            String amountString = amountInput == null ? "" : amountInput.toString();
            String percentageString = percentageInput == null ? "" : percentageInput.toString();
            model.addDrink(date,
                    amountString.isEmpty() ? 0 : Float.parseFloat(amountString),
                    percentageString.isEmpty() ? 0 : Float.parseFloat(percentageString)
            );
        });

        binding.addDrinkCancelButton.setOnClickListener(view -> dismiss());
    }
}