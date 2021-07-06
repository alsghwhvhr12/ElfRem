package com.elf.remote.view.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.FragmentStep1Binding;
import com.elf.remote.viewmodel.settings.Step1ViewModel;


public class Step1Fragment extends Fragment {
    public Step1Fragment() {
    }

    public static Step1Fragment getInstance() {
        return new Step1Fragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Step1ViewModel model = new Step1ViewModel();

        FragmentStep1Binding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_step1, container, false);
        binding.setViewModel(model);
        binding.executePendingBindings();

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}