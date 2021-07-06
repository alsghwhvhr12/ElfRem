package com.elf.remote.viewmodel.settings;

import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableField;

import com.elf.remote.Application;
import com.elf.remote.CallActivity;
import com.elf.remote.model.bluetooth.BluetoothCon;
import com.elf.remote.viewmodel.BaseViewModel;

public class SensViewModel extends BaseObservable implements BaseViewModel {
    private final CallActivity navi;

    BluetoothCon bluetoothCon = new BluetoothCon(Application.applicationContext());

    public final ObservableField<Boolean> check = new ObservableField<>();
    public final ObservableField<Boolean> check2 = new ObservableField<>();
    public final ObservableField<Boolean> check3 = new ObservableField<>();

    public SensViewModel(CallActivity navi) {
        this.navi = navi;
    }

    public void highClick() {
        check.set(false);
        check2.set(false);
        check3.set(true);
        navi.exitActivity();
    }

    public void midClick() {
        check.set(false);
        check2.set(true);
        check3.set(false);
        navi.callActivity();
    }

    public void lowClick() {
        check.set(true);
        check2.set(false);
        check3.set(false);
        navi.callDialog();
    }

    @Override
    public void onCreate() {
        check.set(false);
        check2.set(true);
        check3.set(false);

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
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }
}
