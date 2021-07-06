package com.elf.remote.view.search;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.FragmentSungokBinding;
import com.elf.remote.SungokCall;
import com.elf.remote.model.bluetooth.BluetoothCon;
import com.elf.remote.model.bluetooth.BtDevice;
import com.elf.remote.viewmodel.search.SungokViewModel;

@SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
public class SungokFragment extends DialogFragment implements SungokCall {
    private FragmentSungokBinding binding;

    BluetoothCon bluetoothCon = new BluetoothCon(getContext());

    SungokViewModel model;

    public SungokFragment() {
    }

    public static SungokFragment getInstance() {
        return new SungokFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        model = new SungokViewModel(this, bluetoothCon, bundle);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sungok, container, false);
        binding.setViewModel(model);
        binding.executePendingBindings();

        binding.interval.setSelected(true);

        setCancelable(false);

        model.onCreate();

        if (BtDevice.getDevice() == null) {
            binding.tempo.setEnabled(false);
            binding.interval.setEnabled(false);
            binding.upBtn.setEnabled(false);
            binding.dnBtn.setEnabled(false);
        }

        threeCall();
        fourCall();

        return binding.getRoot();
    }

    @Override
    public void closeCall() {
        dismiss();
    }

    @Override
    public void oneCall() {
        binding.tempo.setSelected(true);
        binding.interval.setSelected(false);
        model.tmpSec = true;
        model.intSec = false;
    }

    @Override
    public void twoCall() {
        binding.interval.setSelected(true);
        binding.tempo.setSelected(false);
        model.tmpSec = false;
        model.intSec = true;
    }

    @Override
    public void threeCall() {
    }

    @Override
    public void fourCall() {
    }

    @Override
    public void fiveCall() {
        dismiss();
    }

    @Override
    public void sixCall() {
        SLoveAddFragment sAdd = SLoveAddFragment.getInstance();
        Bundle bundle = new Bundle();
        bundle.putString("LoveKind", "myLove");
        bundle.putSerializable("song", model.song);
        bundle.putInt("tempo2", model.Tempo);
        bundle.putInt("count", model.c - model.ftemp);
        sAdd.setArguments(bundle);
        sAdd.show(getParentFragmentManager(), "sAdd");
        dismiss();
    }

    @Override
    public void sevenCall() {
        SLoveAddFragment sAdd = SLoveAddFragment.getInstance();
        Bundle bundle = new Bundle();
        bundle.putString("LoveKind", "youLove");
        bundle.putSerializable("song", model.song);
        bundle.putInt("tempo2", model.Tempo);
        bundle.putInt("count", model.c - model.ftemp);
        sAdd.setArguments(bundle);
        sAdd.show(getParentFragmentManager(), "sAdd");
        dismiss();
    }

    @Override
    public void eightCall() {
        if (!(BtDevice.getDevice() == null)) {
            if (model.record == 0) {
                SungokStartFragment sstart = SungokStartFragment.getInstance();
                sstart.show(getParentFragmentManager(), "sstart");
            } else {
                requireActivity().finish();
            }
            dismiss();
        } else {
            dialog();
        }
    }

    @Override
    public void nineCall() {
        if (!(BtDevice.getDevice() == null)) {
            dismiss();
        } else {
            dialog();
        }
    }

    @Override
    public void tenCall() {
        if (!(BtDevice.getDevice() == null)) {
            dismiss();
        } else {
            dialog();
        }
    }

    public void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("반주기를 연결해주세요.");
        builder.setNegativeButton("확인",
                (dialog, which) -> {
                });
        builder.show();
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