package com.elf.remote.view.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.FragmentStep2Binding;
import com.elf.remote.viewmodel.settings.Step2ViewModel;


public class Step2Fragment extends Fragment {
    public Step2Fragment() {
    }

    public static Step2Fragment getInstance() {
        return new Step2Fragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Step2ViewModel model = new Step2ViewModel();

        FragmentStep2Binding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_step2, container, false);
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