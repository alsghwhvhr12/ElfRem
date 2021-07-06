package com.elf.remote.viewmodel.remotecon;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.BaseObservable;

import com.elf.remote.ejoButton;
import com.elf.remote.model.bluetooth.BluetoothCon;
import com.elf.remote.model.bluetooth.BtDevice;
import com.elf.remote.viewmodel.BaseViewModel;

public class AkboViewModel extends BaseObservable implements BaseViewModel {
    private final BluetoothCon bluetoothCon;
    private final ejoButton navi;
    private final AlertDialog.Builder builder;

    public AkboViewModel(ejoButton navi, BluetoothCon bluetoothCon, AlertDialog.Builder builder) {
        this.navi = navi;
        this.bluetoothCon = bluetoothCon;
        this.builder = builder;
    }

    public void onExitClick() {
        navi.closeCall();
    }

    public void on1BUpClick() {
        if (BtDevice.getDevice() != null) {
            bluetoothCon.commandBluetooth("1BEEJOUP\n");
        } else {
            dialog();
        }
    }

    public void on1BDnClick() {
        if (BtDevice.getDevice() != null) {
            bluetoothCon.commandBluetooth("1BEEJODN\n");
        } else {
            dialog();
        }
    }

    public void on1BTenorClick() {
        if (BtDevice.getDevice() != null) {
            bluetoothCon.commandBluetooth("1BEEJOTENOR\n");
        } else {
            dialog();
        }
    }

    public void on1BAltoClick() {
        if (BtDevice.getDevice() != null) {
            bluetoothCon.commandBluetooth("1BEEJOALTO\n");
        } else {
            dialog();
        }
    }

    public void on1BOctUp() {
        if (BtDevice.getDevice() != null) {
            bluetoothCon.commandBluetooth("1BOCTAVUP\n");
        } else {
            dialog();
        }
    }

    public void on1BOctDn() {
        if (BtDevice.getDevice() != null) {
            bluetoothCon.commandBluetooth("1BOCTAVDN\n");
        } else {
            dialog();
        }
    }

    public void on2BUpClick() {
        if (BtDevice.getDevice() != null) {
            bluetoothCon.commandBluetooth("2BEEJOUP\n");
        } else {
            dialog();
        }
    }

    public void on2BDnClick() {
        if (BtDevice.getDevice() != null) {
            bluetoothCon.commandBluetooth("2BEEJODN\n");
        } else {
            dialog();
        }
    }

    public void on2BTenorClick() {
        if (BtDevice.getDevice() != null) {
            bluetoothCon.commandBluetooth("2BEEJOTENOR\n");
        } else {
            dialog();
        }
    }

    public void on2BAltoClick() {
        if (BtDevice.getDevice() != null) {
            bluetoothCon.commandBluetooth("2BEEJOALTO\n");
        } else {
            dialog();
        }
    }

    public void on2BOctUp() {
        if (BtDevice.getDevice() != null) {
            bluetoothCon.commandBluetooth("2BOCTAVUP\n");
        } else {
            dialog();
        }
    }

    public void on2BOctDn() {
        if (BtDevice.getDevice() != null) {
            bluetoothCon.commandBluetooth("2BOCTAVDN\n");
        } else {
            dialog();
        }
    }

    public void on3BUpClick() {
        if (BtDevice.getDevice() != null) {
            bluetoothCon.commandBluetooth("3BEEJOUP\n");
        } else {
            dialog();
        }
    }

    public void on3BDnClick() {
        if (BtDevice.getDevice() != null) {
            bluetoothCon.commandBluetooth("3BEEJODN\n");
        } else {
            dialog();
        }
    }

    public void on3BTenorClick() {
        if (BtDevice.getDevice() != null) {
            bluetoothCon.commandBluetooth("3BEEJOTENOR\n");
        } else {
            dialog();
        }
    }

    public void on3BAltoClick() {
        if (BtDevice.getDevice() != null) {
            bluetoothCon.commandBluetooth("3BEEJOALTO\n");
        } else {
            dialog();
        }
    }

    public void on3BOctUp() {
        if (BtDevice.getDevice() != null) {
            bluetoothCon.commandBluetooth("3BOCTAVUP\n");
        } else {
            dialog();
        }
    }

    public void on3BOctDn() {
        if (BtDevice.getDevice() != null) {
            bluetoothCon.commandBluetooth("3BOCTAVDN\n");
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
}