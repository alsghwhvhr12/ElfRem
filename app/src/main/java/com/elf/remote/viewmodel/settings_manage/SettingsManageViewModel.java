package com.elf.remote.viewmodel.settings_manage;

import android.app.ProgressDialog;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.BaseObservable;

import com.elf.remote.Application;
import com.elf.remote.CallActivity;
import com.elf.remote.model.bluetooth.BluetoothCon;
import com.elf.remote.model.bluetooth.BtDevice;
import com.elf.remote.model.data.VerSionMachin;
import com.elf.remote.viewmodel.BaseViewModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class SettingsManageViewModel extends BaseObservable implements BaseViewModel {
    private final CallActivity navi;
    BluetoothCon bluetoothCon;
    ProgressDialog progressBar2;
    AlertDialog.Builder builder;

    public String path, rPath;

    int i;

    public SettingsManageViewModel(CallActivity navi, BluetoothCon bluetoothCon, ProgressDialog progressBar2, AlertDialog.Builder builder) {
        this.navi = navi;
        this.bluetoothCon = bluetoothCon;
        this.progressBar2 = progressBar2;
        this.builder = builder;
    }

    public void onDelClick() {
        navi.callActivity();
    }

    public void onChgClick() {
        navi.callDialog();
    }

    public void onSaveClick() {
        if (!(BtDevice.getDevice() == null)) {
            i = 2;
            bluetoothCon.getDataDir(path);
            byte[] bArr = new byte[10];
            bArr[0] = 80;
            bArr[1] = 82;
            bArr[2] = 69;
            bArr[3] = 83;
            bArr[4] = 69;
            bArr[5] = 84;
            bArr[6] = 82;
            bArr[7] = 69;
            bArr[8] = 81;

            if (VerSionMachin.getSetKind() == 0) {
                bArr[9] = (byte) 2;
                bluetoothCon.commandBluetooth2(bArr);
            } else if (VerSionMachin.getSetKind() == 1) {
                bArr[9] = (byte) 3;
                bluetoothCon.commandBluetooth2(bArr);
            } else if (VerSionMachin.getSetKind() == 2) {
                bArr[9] = (byte) 5;
                bluetoothCon.commandBluetooth2(bArr);
            } else {
                bluetoothCon.commandBluetooth("DBFILEREQ");
            }
            showProgressDialog();
        } else dialog();
    }

    public void onLoadClick() {
        if (!(BtDevice.getDevice() == null)) {
            if (VerSionMachin.getFilePath() != null) {
                dialog2();
            }
        } else dialog();
    }

    public void copyFileList(String copyFileName1, String copyFileName2) {
        File newFile = new File(copyFileName1);
        File copyFile = new File(copyFileName2);

        if (newFile.exists()) {
            builder.setMessage("파일이 이미 존재합니다.\n덮어씌우시겠습니까?");
            builder.setNegativeButton("취소",
                    (dialog, which) -> {
                    });
            builder.setPositiveButton("확인",
                    (dialog, which) -> {
                        try {
                            copy(copyFile, newFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
            builder.show();
        } else {
            try {
                copy(copyFile, newFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void copy(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();

        File file = new File(path);
        file.delete();
    }

    protected void showProgressDialog() {
        bluetoothCon.getFragment(this);
        String what;
        if (i == 2) {
            if (VerSionMachin.getSetKind() == 0) {
                what = "스마트EQ 받는중...";
            } else if (VerSionMachin.getSetKind() == 1) {
                what = "마이크 이펙터 받는중...";
            } else if (VerSionMachin.getSetKind() == 2) {
                what = "기타 이펙터 받는중...";
            } else {
                what = "애창곡 받는중...";
            }
        } else {
            if (VerSionMachin.getSetKind() == 0) {
                what = "스마트EQ 전송중...";
            } else if (VerSionMachin.getSetKind() == 1) {
                what = "마이크 이펙터 전송중...";
            } else if (VerSionMachin.getSetKind() == 2) {
                what = "기타 이펙터 전송중...";
            } else {
                what = "애창곡 전송중...";
            }
        }
        progressBar2.setMessage(what);
        progressBar2.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar2.setIndeterminate(true);
        progressBar2.setCancelable(false);

        progressBar2.show();
    }

    public void hideProgressDialog() {
        progressBar2.dismiss();
        bluetoothCon.getFragment(null);
        if (i == 2) {
            navi.exitActivity();
        }
    }

    public void updateProgressDialog(int i) {
        progressBar2.setIndeterminate(false);
        progressBar2.setMax(100);
        progressBar2.setProgress(i);
        progressBar2.setCancelable(false);

        if (i == 100) {
            hideProgressDialog();
        }
    }

    public void dialog() {
        builder.setMessage("반주기를 연결해주세요.");
        builder.setNegativeButton("확인",
                (dialog, which) -> {
                });
        builder.show();
    }

    public void dialog2() {
        builder.setMessage("설정을 내보내시겠습니까? 기존 반주기 설정은 사라집니다.");
        builder.setPositiveButton("예",
                (dialog, which) -> {
                    i = 3;
                    bluetoothCon.getDataDir(VerSionMachin.getFilePath());
                    byte[] bArr = new byte[10];
                    bArr[0] = 80;
                    bArr[1] = 82;
                    bArr[2] = 69;
                    bArr[3] = 83;
                    bArr[4] = 69;
                    bArr[5] = 84;
                    bArr[6] = 83;
                    bArr[7] = 84;
                    bArr[8] = 82;

                    if (VerSionMachin.getSetKind() == 0) {
                        bArr[9] = (byte) 2;
                        bluetoothCon.commandBluetooth2(bArr);
                    } else if (VerSionMachin.getSetKind() == 1) {
                        bArr[9] = (byte) 3;
                        bluetoothCon.commandBluetooth2(bArr);
                    } else if (VerSionMachin.getSetKind() == 2) {
                        bArr[9] = (byte) 5;
                        bluetoothCon.commandBluetooth2(bArr);
                    } else {
                        bluetoothCon.commandBluetooth("MACHINEREQ");
                    }
                    showProgressDialog();
                });
        builder.setNegativeButton("아니요",
                (dialog, which) -> {
                });
        builder.show();
    }

    @Override
    public void onCreate() {
        path = Application.applicationContext().getApplicationInfo().dataDir + "/databases/temp.db";
        rPath = Application.applicationContext().getApplicationInfo().dataDir + "/databases/temp2.db";
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
