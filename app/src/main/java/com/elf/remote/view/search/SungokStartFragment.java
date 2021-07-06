package com.elf.remote.view.search;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.FragmentSungokStartBinding;
import com.elf.remote.SubCall;
import com.elf.remote.model.bluetooth.BluetoothCon;
import com.elf.remote.viewmodel.search.SungokStartViewModel;

public class SungokStartFragment extends DialogFragment implements SubCall {
    BluetoothCon bluetoothCon = new BluetoothCon(getContext());

    public SungokStartFragment() {
    }

    public static SungokStartFragment getInstance() {
        return new SungokStartFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentSungokStartBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sungok_start, container, false);
        binding.setViewModel(new SungokStartViewModel(this));
        binding.executePendingBindings();
        setCancelable(false);

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void closeCall() {
        dismiss();
    }

    @Override
    public void oneCall() {
        bluetoothCon.commandBluetooth("SONGSTART");
        dismiss();
    }

    @Override
    public void twoCall() {

    }

    @Override
    public void threeCall() {

    }

    @Override
    public void fourCall() {

    }

    @Override
    public void fiveCall() {

    }

    @Override
    public void sixCall() {

    }

    @Override
    public void sevenCall() {

    }

    @Override
    public void eightCall() {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
                dismiss();
            }
        };
    }
}