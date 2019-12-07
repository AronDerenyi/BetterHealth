package com.madebyaron.betterhealth.view.list;

import android.view.View;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.madebyaron.betterhealth.R;
import com.madebyaron.betterhealth.databinding.ItemListBinding;
import com.madebyaron.betterhealth.model.Data;
import com.madebyaron.betterhealth.viewmodel.ListModel;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ListItemHolder extends RecyclerView.ViewHolder {

    private static DecimalFormat amountFormat = new DecimalFormat("0.##");
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy   hh:mm a", Locale.getDefault());

    private final FragmentActivity activity;
    private final ItemListBinding binding;
    private final ListModel model;

    private Data data;

    public ListItemHolder(FragmentActivity activity, ItemListBinding binding) {
        super(binding.getRoot());
        this.activity = activity;
        this.binding = binding;
        this.model = ViewModelProviders.of(activity).get(ListModel.class);
    }

    public void bind(Data data) {
        this.data = data;

        binding.listItemIconWeight.setVisibility(data.getType() == Data.TYPE_WEIGHT ? View.VISIBLE : View.GONE);
        binding.listItemIconCalories.setVisibility(data.getType() == Data.TYPE_CALORIES ? View.VISIBLE : View.GONE);
        binding.listItemIconDrink.setVisibility(data.getType() == Data.TYPE_DRINK ? View.VISIBLE : View.GONE);
        binding.listItemIconCigarettes.setVisibility(data.getType() == Data.TYPE_CIGARETTES ? View.VISIBLE : View.GONE);

        binding.listItemAmount.setText(amountFormat.format(data.getAmount()));

        switch (data.getType()) {
            case Data.TYPE_WEIGHT:
                binding.listItemUnit.setText(activity.getString(R.string.unit_kilograms));
                break;
            case Data.TYPE_CALORIES:
                binding.listItemUnit.setText(activity.getString(R.string.unit_calories));
                break;
            case Data.TYPE_DRINK:
                binding.listItemUnit.setText(activity.getString(R.string.unit_units));
                break;
            case Data.TYPE_CIGARETTES:
                binding.listItemUnit.setText(activity.getString(R.string.unit_cigarettes));
                break;
            default:
                binding.listItemUnit.setText(null);
                break;
        }

        binding.listItemDate.setText(dateFormat.format(data.getDate()));
    }

    public void remove() {
        model.deleteData(data);
    }
}