package com.elf.remote.viewmodel.settings;

import android.widget.SeekBar;

import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableField;

import com.elf.remote.Application;
import com.elf.remote.CallActivity;
import com.elf.remote.model.bluetooth.BluetoothCon;
import com.elf.remote.model.data.VerSionMachin;
import com.elf.remote.viewmodel.BaseViewModel;

public class Step4ViewModel extends BaseObservable implements BaseViewModel {
    private final CallActivity navi;
    public final ObservableField<String> volume = new ObservableField<>();
    public final ObservableField<Integer> volumePro = new ObservableField<>();
    BluetoothCon bluetoothCon = new BluetoothCon(Application.applicationContext());

    public Step4ViewModel (CallActivity navi) {
        this.navi = navi;
    }

    public void exitClick() {
        navi.exitActivity();
    }

    public void finishClick() {
        navi.callActivity();
    }

    public void sensClick() {
        navi.callDialog();
    }

    @Override
    public void onCreate() {
        volume.set("볼륨 : " + VerSionMachin.getVolume());
        volumePro.set(VerSionMachin.getVolume());
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

    public SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            volume.set("볼륨 : " + progress);
            VerSionMachin.setVolume(progress);

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
            bArr[8] = 2;
            bArr[9] = (byte) progress;

            bluetoothCon.commandBluetooth2(bArr);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
}
