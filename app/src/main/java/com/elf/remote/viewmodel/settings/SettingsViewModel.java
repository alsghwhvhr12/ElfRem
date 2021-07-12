package com.elf.remote.viewmodel.settings;

import android.app.ProgressDialog;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.BaseObservable;

import com.elf.remote.Application;
import com.elf.remote.ListEdit;
import com.elf.remote.model.bluetooth.BluetoothCon;
import com.elf.remote.model.bluetooth.BtDevice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class SettingsViewModel extends BaseObservable {
    private final ListEdit navi;
    BluetoothCon bluetoothCon;
    ProgressDialog progressBar2;
    AlertDialog.Builder builder;

    public String path;

    public SettingsViewModel(ListEdit navi , BluetoothCon bluetoothCon, ProgressDialog progressBar2, AlertDialog.Builder builder) {
        this.navi = navi;
        this.bluetoothCon = bluetoothCon;
        this.progressBar2 = progressBar2;
        this.builder = builder;
    }

    public void onSycClick() {
        navi.editList();
    }

    public void onSpeakerClick() {
        navi.exitList();
    }

    public void onTimeSetClick() {
        navi.dleList();
    }

    public void onKokClick() {
        navi.selList();
    }

    public void onSaveClick() {
        if (!(BtDevice.getDevice() == null)) {
            path = Application.applicationContext().getApplicationInfo().dataDir + "/databases/temp.db";
            bluetoothCon.getDataDir(path);
            bluetoothCon.commandBluetooth("DBFILEREQ");
            showProgressDialog();
        } else dialog();
    }

    public void onLoadClick() {
        if (!(BtDevice.getDevice() == null)) {
            navi.chgFile();
        } else dialog();
    }

    public void dialog() {
        builder.setMessage("반주기를 연결해주세요.");
        builder.setNegativeButton("확인",
                (dialog, which) -> {
                });
        builder.show();
    }

    protected void showProgressDialog() {
        bluetoothCon.getSettingsViewModel(this);
        String what;
        what = "애창곡 받는중...";

        progressBar2.setMessage(what);
        progressBar2.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar2.setIndeterminate(true);
        progressBar2.setCancelable(false);

        progressBar2.show();
    }

    public void hideProgressDialog() {
        progressBar2.dismiss();
        bluetoothCon.getSettingsViewModel(null);

        navi.shareList();
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

    public void copyFileList(String copyFileName1, String copyFileName2) {
        File newFile = new File(copyFileName1);
        File copyFile = new File(copyFileName2);

        if (newFile.exists()) {
            builder.setMessage("파일이 이미 존재합니다.\n덮어씌우시겠습니까?");
            builder.setNegativeButton("취소",
                    (dialog, which) -> {
                        navi.shareList();
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
}
