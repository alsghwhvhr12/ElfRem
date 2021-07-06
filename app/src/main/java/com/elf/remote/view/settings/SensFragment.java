package com.elf.remote.view.settings;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.FragmentSensBinding;
import com.elf.remote.CallActivity;
import com.elf.remote.model.bluetooth.BluetoothCon;
import com.elf.remote.viewmodel.settings.SensViewModel;

public class SensFragment extends DialogFragment implements CallActivity {
    BluetoothCon bluetoothCon;

    public SensFragment() {
    }

    public static SensFragment getInstance() {
        return new SensFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SensViewModel model = new SensViewModel(this);
        FragmentSensBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sens, container, false);
        binding.setViewModel(model);
        binding.executePendingBindings();

        setCancelable(false);

        model.onCreate();
        bluetoothCon = new BluetoothCon(getContext());

        return binding.getRoot();
    }

    @Override
    public void callActivity() {
        byte[] bArr = new byte[10];
        //SMEQVOLM [종류 0 = 인풋 1, 1 = 인풋 2, 2 = 볼륨, 3 = 높음, 4 = 기본, 5 = 낮음] [음량]
        bArr[0] = 83;
        bArr[1] = 77;
        bArr[2] = 69;
        bArr[3] = 81;
        bArr[4] = 86;
        bArr[5] = 79;
        bArr[6] = 76;
        bArr[7] = 77;
        bArr[8] = 4;
        bArr[9] = (byte) 0;

        bluetoothCon.commandBluetooth2(bArr);
    }

    @Override
    public void exitActivity() {
        byte[] bArr = new byte[10];
        //SMEQVOLM [종류 0 = 인풋 1, 1 = 인풋 2, 2 = 볼륨, 3 = 높음, 4 = 기본, 5 = 낮음] [음량]
        bArr[0] = 83;
        bArr[1] = 77;
        bArr[2] = 69;
        bArr[3] = 81;
        bArr[4] = 86;
        bArr[5] = 79;
        bArr[6] = 76;
        bArr[7] = 77;
        bArr[8] = 3;
        bArr[9] = (byte) 0;

        bluetoothCon.commandBluetooth2(bArr);
    }

    @Override
    public void callDialog() {
        byte[] bArr = new byte[10];
        //SMEQVOLM [종류 0 = 인풋 1, 1 = 인풋 2, 2 = 볼륨, 3 = 높음, 4 = 기본, 5 = 낮음] [음량]
        bArr[0] = 83;
        bArr[1] = 77;
        bArr[2] = 69;
        bArr[3] = 81;
        bArr[4] = 86;
        bArr[5] = 79;
        bArr[6] = 76;
        bArr[7] = 77;
        bArr[8] = 5;
        bArr[9] = (byte) 0;

        bluetoothCon.commandBluetooth2(bArr);
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
