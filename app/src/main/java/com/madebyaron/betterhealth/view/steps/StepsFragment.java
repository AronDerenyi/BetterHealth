package com.madebyaron.betterhealth.view.steps;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.madebyaron.betterhealth.databinding.FragmentStepsBinding;
import com.madebyaron.betterhealth.viewmodel.StepsModel;

public class StepsFragment extends Fragment {

    private FragmentStepsBinding binding;
    private StepsModel model;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStepsBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            model = ViewModelProviders.of(getActivity()).get(StepsModel.class);
            binding.setModel(model);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        model.startRefreshing();
    }

    @Override
    public void onPause() {
        super.onPause();
        model.stopRefreshing();
    }
}