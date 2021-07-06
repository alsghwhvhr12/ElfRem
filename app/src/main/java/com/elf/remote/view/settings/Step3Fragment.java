package com.elf.remote.view.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.FragmentStep3Binding;
import com.elf.remote.CallActivity;
import com.elf.remote.model.data.VerSionMachin;
import com.elf.remote.viewmodel.settings.Step3ViewModel;


public class Step3Fragment extends Fragment implements CallActivity {
    Step3ViewModel model;

    public Step3Fragment() {
    }

    public static Step3Fragment getInstance() {
        return new Step3Fragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        model = new Step3ViewModel(this);

        FragmentStep3Binding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_step3, container, false);
        binding.setViewModel(model);
        binding.executePendingBindings();

        model.onCreate();

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        model.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        model.onDestroy();
        super.onDestroy();
    }

    @Override
    public void callActivity() {
        Intent intent = new Intent(getContext(), Step4Fragment.class);
        startActivity(intent);
        VerSionMachin.setSpeakerKind(1);
    }

    @Override
    public void exitActivity() {

    }

    @Override
    public void callDialog() {

    }
}