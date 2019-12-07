package com.madebyaron.betterhealth.view.list.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.madebyaron.betterhealth.databinding.DialogAddCigarettesBinding;
import com.madebyaron.betterhealth.view.components.DateTimePicker;
import com.madebyaron.betterhealth.viewmodel.ListModel;

import java.util.Date;

public class AddCigarettesDialog extends Dialog {

    private final FragmentActivity activity;

    public AddCigarettesDialog(FragmentActivity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DialogAddCigarettesBinding binding = DialogAddCigarettesBinding.inflate(LayoutInflater.from(getContext()));
        setContentView(binding.getRoot());

        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        ListModel model = ViewModelProviders.of(activity).get(ListModel.class);
        Date date = new Date();

        DateTimePicker.setDateTimePickerText(binding.addCigarettesDateInput, date);
        binding.addCigarettesDateInput.setOnClickListener(view ->
                DateTimePicker.openDateTimePickerDialog(getContext(), date, dateTime -> {
                            date.setTime(dateTime.getTime());
                            DateTimePicker.setDateTimePickerText(binding.addCigarettesDateInput, date);
                        }
                )
        );

        binding.addCigarettesAddButton.setOnClickListener(view -> {
            dismiss();
            Editable countInput = binding.addCigarettesCountInput.getText();
            String countString = countInput == null ? "" : countInput.toString();
            model.addCigarettes(date, countString.isEmpty() ? 0 : Integer.parseInt(countString));
        });

        binding.addCigarettesCancelButton.setOnClickListener(view -> dismiss());
    }
}