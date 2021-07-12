package com.elf.remote.view.remotecon;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.FragmentBlank2Binding;
import com.elf.mvvmremote.databinding.FragmentBlankBinding;
import com.elf.remote.ejoButton;
import com.elf.remote.model.bluetooth.BluetoothCon;
import com.elf.remote.model.bluetooth.BtDevice;
import com.elf.remote.model.data.VerSionMachin;
import com.elf.remote.viewmodel.remotecon.AkboViewModel;

import java.util.Objects;

public class BlankFragment extends DialogFragment implements ejoButton {
    public static final String TAG_EVENT_DIALOG = "dialog_event";

    BluetoothCon bluetoothCon = new BluetoothCon(getContext());

    public BlankFragment() {
    }

    public static BlankFragment getInstance() {
        return new BlankFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        if (BtDevice.getDevice() == null) {
            FragmentBlankBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_blank, container, false);
            binding.setViewModel(new com.elf.remote.viewmodel.remotecon.AkboViewModel(this, bluetoothCon, builder));
            binding.executePendingBindings();
            view = binding.getRoot();
        } else {
            if (VerSionMachin.getName().equals("G10")) {
                FragmentBlank2Binding binding2 = DataBindingUtil.inflate(inflater, R.layout.fragment_blank2, container, false);
                binding2.setViewModel(new com.elf.remote.viewmodel.remotecon.AkboViewModel(this, bluetoothCon, builder));
                binding2.executePendingBindings();
                view = binding2.getRoot();
            } else {
                FragmentBlankBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_blank, container, false);
                binding.setViewModel(new AkboViewModel(this, bluetoothCon, builder));
                binding.executePendingBindings();
                view = binding.getRoot();
            }
        }

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setCancelable(false);
        return view;
    }

    @Override
    public void closeCall() {
        dismiss();
    }

    //region 안씀
    @Override
    public void ejoup() {
    }

    @Override
    public void ejodn() {
    }

    @Override
    public void ejoalto() {
    }

    @Override
    public void ejotenor() {
    }

    @Override
    public void eocup() {
    }

    @Override
    public void eocdn() {
    }

    @Override
    public void eejoup() {
    }

    @Override
    public void eejodn() {
    }

    @Override
    public void eejoalto() {
    }

    @Override
    public void eejotenor() {
    }

    @Override
    public void eeocup() {
    }

    @Override
    public void eeocdn() {
    }

    @Override
    public void eeejoup() {
    }

    @Override
    public void eeejodn() {
    }

    @Override
    public void eeejoalto() {
    }

    @Override
    public void eeejotenor() {
    }

    @Override
    public void eeeocup() {
    }

    @Override
    public void eeeocdn() {
    }
    //endregion

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