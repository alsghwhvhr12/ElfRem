package com.elf.remote.viewmodel.remotecon;

import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableField;

import com.elf.remote.RemoteButton;
import com.elf.remote.model.bluetooth.BluetoothCon;
import com.elf.remote.model.bluetooth.BtDevice;
import com.elf.remote.model.data.VerSionMachin;
import com.elf.remote.utils.LongPressRepeatListener;
import com.elf.remote.viewmodel.BaseViewModel;

public class RemoteControllerViewModel extends BaseObservable implements BaseViewModel {
    private final RemoteButton navi;
    private BluetoothCon bluetoothCon;
    private final AlertDialog.Builder builder;
    public final ObservableField<Boolean> check = new ObservableField<>();

    public RemoteControllerViewModel(RemoteButton navi, BluetoothCon bluetoothCon, AlertDialog.Builder builder) {
        this.navi = navi;
        this.bluetoothCon = bluetoothCon;
        this.builder = builder;
        check.set(false);
    }

    public void onAkboClicked() {
        navi.akboCall();
    }

    public void onCloseClicked() {
        navi.closeCall();
    }

    public void onStartClick() {
        if (BtDevice.getDevice() != null && VerSionMachin.getStop() == 1) {
            if (check.get()) {
                bluetoothCon.commandBluetooth("SNGRESTART\n");
                check.set(false);
            }
            else bluetoothCon.commandBluetooth("SONGSTART");

        } else if (VerSionMachin.getStop() == 0) {
            dialog2();
        } else {
            dialog();
        }
    }

    public void onPauseClick() {
        if (BtDevice.getDevice() != null  && VerSionMachin.getStop() == 1) {
            if (check.get()) bluetoothCon.commandBluetooth("SNGPAUSE\n");
            else bluetoothCon.commandBluetooth("SNGRESTART\n");
        } else if (VerSionMachin.getStop() == 0) {
            check.set(false);
            dialog2();
        } else {
            check.set(false);
            dialog();
        }
    }

    /*public void onRestartClick() {
        if (BtDevice.getDevice() != null  && VerSionMachin.getStop() == 1) {
            bluetoothCon.commandBluetooth("SNGRESTART\n");
        } else if (VerSionMachin.getStop() == 0) {
            dialog2();
        } else {
            dialog();
        }
    }*/

    public void onStopClick() {
        if (BtDevice.getDevice() != null  && VerSionMachin.getStop() == 1) {
            if (check.get()) {
                check.set(false);
            }
            bluetoothCon.commandBluetooth("SONGSTOP\n");
        } else if (VerSionMachin.getStop() == 0) {
            dialog2();
        } else {
            dialog();
        }
    }

    public void onWkeyClick() {
        if (BtDevice.getDevice() != null  && VerSionMachin.getStop() == 1) {
            bluetoothCon.commandBluetooth("SNGWOMKEY\n");
        } else if (VerSionMachin.getStop() == 0) {
            dialog2();
        } else {
            dialog();
        }
    }

    public void onMkeyClick() {
        if (BtDevice.getDevice() != null  && VerSionMachin.getStop() == 1) {
            bluetoothCon.commandBluetooth("SNGMANKEY\n");
        } else if (VerSionMachin.getStop() == 0) {
            dialog2();
        } else {
            dialog();
        }
    }

    public void onOrgketClick() {
        if (BtDevice.getDevice() != null) {
            bluetoothCon.commandBluetooth("SNGORGKEY\n");
        } else {
            dialog();
        }
    }

    public void onOrgtempoClick() {
        if (BtDevice.getDevice() != null) {
            bluetoothCon.commandBluetooth("SNGORGTEMPO\n");
        } else {
            dialog();
        }
    }

    public void onKeyupClick() {
        if (BtDevice.getDevice() != null) {
            bluetoothCon.commandBluetooth("SNGKEYUP\n");
        } else {
            dialog();
        }
    }

    public void onKeydnClick() {
        if (BtDevice.getDevice() != null) {
            bluetoothCon.commandBluetooth("SNGKEYDN\n");
        } else {
            dialog();
        }
    }

    /*public void onTempoUpClick() {
        if (BtDevice.getDevice() != null) {
            bluetoothCon.commandBluetooth("SNGTEMPOUP\n");
        } else {
            dialog();
        }
    }

    public void onTempoDnClick() {
        if (BtDevice.getDevice() != null) {
            bluetoothCon.commandBluetooth("SNGTEMPODN\n");
        } else {
            dialog();
        }
    }*/

    public void onEndingClick() {
        if (BtDevice.getDevice() != null) {
            bluetoothCon.commandBluetooth("SNGENDING\n");
        } else {
            dialog();
        }
    }

    public void onOneRepeatClick() {
        if (BtDevice.getDevice() != null  && VerSionMachin.getStop() == 1) {
            bluetoothCon.commandBluetooth("ONEREPEAT\n");
        } else if (VerSionMachin.getStop() == 0) {
            dialog2();
        } else {
            dialog();
        }
    }

    public void onSectClick() {
        if (BtDevice.getDevice() != null  && VerSionMachin.getStop() == 1) {
            bluetoothCon.commandBluetooth("SECTSTART\n");
        } else if (VerSionMachin.getStop() == 0) {
            dialog2();
        } else {
            dialog();
        }
    }

    public void onNextClick() {
        if (BtDevice.getDevice() != null) {
            bluetoothCon.commandBluetooth("NEXTSONG\n");
        } else {
            dialog();
        }
    }

    public void onSectEnd() {
        if (BtDevice.getDevice() != null) {
            bluetoothCon.commandBluetooth("SECTEND\n");
        } else if (VerSionMachin.getStop() == 0) {
            dialog2();
        } else {
            dialog();
        }
    }

    public void onSectStop() {
        if (BtDevice.getDevice() != null) {
            bluetoothCon.commandBluetooth("SECTSTOP\n");
        } else if (VerSionMachin.getStop() == 0) {
            dialog2();
        } else {
            dialog();
        }
    }

    @Override
    public void onCreate() {

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

    public void dialog() {
        builder.setMessage("반주기를 연결해주세요.");
        builder.setNegativeButton("확인",
                (dialog, which) -> {
                });
        builder.show();
    }

    public void dialog2() {
        builder.setMessage("녹화중에는 기능이 제한됩니다.");
        builder.setNegativeButton("확인",
                (dialog, which) -> {
                });
        builder.show();
    }

    public View.OnTouchListener tempUp = new LongPressRepeatListener(100, 100, v -> {
        if (BtDevice.getDevice() != null) {
            bluetoothCon.commandBluetooth("SNGTEMPOUP\n");
        } else {
            dialog();
        }
    });

    public View.OnTouchListener tempDn = new LongPressRepeatListener(100, 100, v -> {
        if (BtDevice.getDevice() != null) {
            bluetoothCon.commandBluetooth("SNGTEMPODN\n");
        } else {
            dialog();
        }
    });
}
