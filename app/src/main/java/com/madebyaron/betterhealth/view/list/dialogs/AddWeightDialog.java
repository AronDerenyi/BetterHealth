package com.madebyaron.betterhealth.view.list.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.madebyaron.betterhealth.databinding.DialogAddWeightBinding;
import com.madebyaron.betterhealth.view.components.DateTimePicker;
import com.madebyaron.betterhealth.viewmodel.ListModel;

import java.util.*;

public class AddWeightDialog extends Dialog {

    private final FragmentActivity activity;

    public AddWeightDialog(FragmentActivity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DialogAddWeightBinding binding = DialogAddWeightBinding.inflate(LayoutInflater.from(getContext()));
        setContentView(binding.getRoot());

        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        ListModel model = ViewModelProviders.of(activity).get(ListModel.class);
        Date date = new Date();

        DateTimePicker.setDateTimePickerText(binding.addWeightDateInput, date);
        binding.addWeightDateInput.setOnClickListener(view ->
                DateTimePicker.openDateTimePickerDialog(getContext(), date, dateTime -> {
                            date.setTime(dateTime.getTime());
                            DateTimePicker.setDateTimePickerText(binding.addWeightDateInput, date);
                        }
                )
        );

        binding.addWeightAddButton.setOnClickListener(view -> {
            dismiss();
            Editable weightInput = binding.addWeightWeightInput.getText();
            String weightString = weightInput == null ? "" : weightInput.toString();
            model.addWeight(date, weightString.isEmpty() ? 0 : Float.parseFloat(weightString));
        });

        binding.addWeightCancelButton.setOnClickListener(view -> dismiss());
    }
}