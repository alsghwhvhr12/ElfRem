package com.elf.remote.viewmodel.settings;

import android.view.View;
import android.widget.SeekBar;

import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableField;

import com.elf.remote.Application;
import com.elf.remote.CallActivity;
import com.elf.remote.model.bluetooth.BluetoothCon;
import com.elf.remote.model.data.VerSionMachin;
import com.elf.remote.viewmodel.BaseViewModel;

import java.util.Timer;
import java.util.TimerTask;

public class Step3ViewModel extends BaseObservable implements BaseViewModel {
    public final ObservableField<String> volume = new ObservableField<>();
    BluetoothCon bluetoothCon = new BluetoothCon(Application.applicationContext());
    public final ObservableField<Boolean> check = new ObservableField<>();
    public final ObservableField<Boolean> check2 = new ObservableField<>();
    public ObservableField<Integer> progress = new ObservableField<>();
    byte i = 0;

    CallActivity navi;

    public Step3ViewModel(CallActivity navi) {
        this.navi = navi;
    }

    public void onStartClick() {
        navi.callActivity();
    }

    @Override
    public void onCreate() {
        volume.set("0");
        VerSionMachin.setVolume(0);
        VerSionMachin.setSpeakerKind(0);
        check.set(true);
        check2.set(false);
        progress.set(0);

        //스피커 온
        bluetoothCon.commandBluetooth("SMEQSPKON");

        i = 0;
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
        bArr[8] = 0;
        bArr[9] = (byte) 0;

        bluetoothCon.commandBluetooth2(bArr);
    }

    @Override
    public void onResume() {
        if (VerSionMachin.getSpeakerKind() == 0) {
            check.set(true);
            check2.set(false);
        } else {
            VerSionMachin.setVolume(0);
            volume.set(String.valueOf(0));
            progress.set(0);
        }

        startTimerTask();
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        bluetoothCon.commandBluetooth("SMEQSPKOFF\n");
    }

    public SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            volume.set(String.valueOf(progress));
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

    public View.OnClickListener onClickListener = v -> {
        check.set(true);
        if (check.get()) {
            check2.set(false);
            i = 0;
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
            bArr[8] = i;
            bArr[9] = (byte) VerSionMachin.getVolume();

            bluetoothCon.commandBluetooth2(bArr);
        }
    };

    public View.OnClickListener onClickListener2 = v -> {
        check2.set(true);
        if (check2.get()) {
            check.set(false);
            i = 1;
            byte[] bArr = new byte[10];
            //SMEQVOLM [종류 0 = 인풋 1, 1 = 인풋 2, 2 = 볼륨, 3 = 높음, 4 = 기본, 5 = 낮음] [음량]
            bArr[0] = 69;
            bArr[1] = 88;
            bArr[2] = 84;
            bArr[3] = 83;
            bArr[4] = 79;
            bArr[5] = 85;
            bArr[6] = 78;
            bArr[7] = 68;
            bArr[8] = i;
            bArr[9] = (byte) VerSionMachin.getVolume();

            bluetoothCon.commandBluetooth2(bArr);
        }
    };

    TimerTask timerTask;
    Timer timer = new Timer();

    private synchronized void startTimerTask() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                //스피커 온
                bluetoothCon.commandBluetooth("SMEQSPKON");

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
                bArr[9] = (byte) 0;

                bluetoothCon.commandBluetooth2(bArr);

                progress.set(0);

                timerTask.cancel();
            }
        };

        timer.schedule(timerTask, 1000, 110);
    }
}
