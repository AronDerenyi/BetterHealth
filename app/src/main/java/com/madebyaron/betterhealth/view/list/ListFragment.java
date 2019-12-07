package com.madebyaron.betterhealth.view.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.madebyaron.betterhealth.R;
import com.madebyaron.betterhealth.databinding.FragmentListBinding;
import com.madebyaron.betterhealth.view.list.dialogs.AddCaloriesDialog;
import com.madebyaron.betterhealth.view.list.dialogs.AddCigarettesDialog;
import com.madebyaron.betterhealth.view.list.dialogs.AddDrinkDialog;
import com.madebyaron.betterhealth.view.list.dialogs.AddWeightDialog;
import com.madebyaron.betterhealth.viewmodel.ListModel;

public class ListFragment extends Fragment {

    private FragmentListBinding binding;
    private ListAdapter adapter;
    private boolean addButtonExpanded = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getActivity() != null) {
            adapter = new ListAdapter(getActivity());
            adapter.attachToRecyclerView(binding.listRecyclerView);
            binding.listRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            binding.listSwipeRefresh.setColorSchemeColors(
                    ContextCompat.getColor(getActivity(), R.color.color_control)
            );
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            ListModel model = ViewModelProviders.of(getActivity()).get(ListModel.class);

            binding.setFragment(this);
            binding.setModel(model);

            model.dataList.observe(getViewLifecycleOwner(), adapter::setData);
            model.loading.observe(getViewLifecycleOwner(), binding.listSwipeRefresh::setRefreshing);
            binding.listSwipeRefresh.setOnRefreshListener(model::reload);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        compressAddButton();
    }

    public void add() {
        if (addButtonExpanded) {
            compressAddButton();
        } else {
            expandAddButton();
        }
    }

    public void addWeight() {
        compressAddButton();
        if (getActivity() != null) new AddWeightDialog(getActivity()).show();
    }

    public void addCalories() {
        compressAddButton();
        if (getActivity() != null) new AddCaloriesDialog(getActivity()).show();
    }

    public void addDrink() {
        compressAddButton();
        if (getActivity() != null) new AddDrinkDialog(getActivity()).show();
    }

    public void addCigarettes() {
        compressAddButton();
        if (getActivity() != null) new AddCigarettesDialog(getActivity()).show();
    }

    private void compressAddButton() {
        addButtonExpanded = false;

        binding.listAddWeightButton.hide();
        binding.listAddCaloriesButton.hide();
        binding.listAddDrinkButton.hide();
        binding.listAddCigarettesButton.hide();

        binding.listAddButton.startAnimation(
                AnimationUtils.loadAnimation(
                        getContext(),
                        R.anim.compress_floating_action_button
                )
        );
    }

    private void expandAddButton() {
        addButtonExpanded = true;

        binding.listAddWeightButton.show();
        binding.listAddCaloriesButton.show();
        binding.listAddDrinkButton.show();
        binding.listAddCigarettesButton.show();

        binding.listAddButton.startAnimation(
                AnimationUtils.loadAnimation(
                        getContext(),
                        R.anim.expand_floating_action_button
                )
        );
    }
}