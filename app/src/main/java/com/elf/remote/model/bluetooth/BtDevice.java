package com.elf.remote.model.bluetooth;

import android.bluetooth.BluetoothDevice;

public class BtDevice {
    private static BluetoothDevice Device;

    public static BluetoothDevice getDevice() {
        return Device;
    }

    public static void setDevice(BluetoothDevice device) {
        Device = device;
    }
}
