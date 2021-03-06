package com.elf.remote.viewmodel.search;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableField;

import com.elf.remote.Application;
import com.elf.remote.CallActivity;
import com.elf.remote.model.bluetooth.BluetoothCon;
import com.elf.remote.model.bluetooth.BtDevice;
import com.elf.remote.viewmodel.BaseViewModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TimeSetViewModel extends BaseObservable implements BaseViewModel {
    private final CallActivity navi;

    public int i;
    public String loveKind;
    public int GroupId;
    public ArrayList<Integer> GroupIds = new ArrayList<>();
    String title, nae, path, rPath;

    BluetoothCon bluetoothCon;
    Bundle bundle;
    ProgressDialog progressBar2;
    AlertDialog.Builder builder;

    public final ObservableField<String> bindJae = new ObservableField<>();
    public final ObservableField<String> bindNae = new ObservableField<>();

    public TimeSetViewModel(CallActivity navi, BluetoothCon bluetoothCon, Bundle bundle, ProgressDialog progressBar2, AlertDialog.Builder builder) {
        this.navi = navi;
        this.bluetoothCon = bluetoothCon;
        this.bundle = bundle;
        this.progressBar2 = progressBar2;
        this.builder = builder;
    }

    public void onLoad(String path) {
        bluetoothCon.getDataDir(path);
        bluetoothCon.commandBluetooth("MACHINEREQ");
        showProgressDialog();
    }

    public void onSubmitClick() {
        navi.exitActivity();
    }

    public void onDelClick() {
        if (i == 99) {
            if (!(BtDevice.getDevice() == null)) {
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
                String formatDate = sdfNow.format(date);
                bluetoothCon.commandBluetooth("REQTIME" + formatDate + "\n");
            } else dialog();
        } else if (i == 2 || i == 3) {
            if (!(BtDevice.getDevice() == null)) {
                File saveDb = new File(Application.applicationContext().getApplicationInfo().dataDir + "/databases/lovecount.db");
                if (saveDb.exists()) saveDb.delete();
                bluetoothCon.getDataDir(saveDb.getAbsolutePath());
                bluetoothCon.commandBluetooth("SENDRDY");
                showProgressDialog();
            } else {
                dialog();
            }
        }
        navi.callActivity();
    }

    @Override
    public void onCreate() {
        i = bundle.getInt("kind");
        loveKind = bundle.getString("loveKind");
        GroupId = bundle.getInt("GroupId");
        GroupIds = bundle.getIntegerArrayList("GroupIds");

        path = Application.applicationContext().getApplicationInfo().dataDir + "/databases/temp.db";
        rPath = Application.applicationContext().getApplicationInfo().dataDir + "/databases/mylove.db";

        if (i == 1) { // ?????? ??????????????? db?????? ????????????
            title = "????????? ????????????";
            nae = "?????????, ????????? ????????? ????????????????????????? ?????? ???????????? ???????????????.";
        } else if (i == 2 || i == 3) {
            title = "????????? ??????";
            nae = "????????? ????????? ?????? ???????????????????";
        } else if (i == 4) { // ????????? ?????? ?????? ??????
            title = "?????? ???????????????????";
            nae = "";
        } else if (i == 5) { // ????????? ??????, ????????? ?????? ?????? ?????? ?????? ?????? ??????
            title = "?????? ???????????????????";
            nae = "";
        } else if (i == 6) {
            title = "????????? ??????";
            nae = "????????? ????????? ????????? ?????? ???????????????????";
        } else { // ?????? ??????
            title = "?????? ????????? ???????????????????";
            nae = "";
        }

        bindJae.set(title);
        bindNae.set(nae);
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

    public void copyFileList(String copyFileName1, String copyFileName2) {
        File newFile = new File(copyFileName1);
        File copyFile = new File(copyFileName2);

        try {
            copy(copyFile, newFile);
        } catch (IOException e) {
            e.printStackTrace();
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

        if (!src.getAbsolutePath().contains("myResv")) dialog2();
    }

    protected void showProgressDialog() {
        bluetoothCon.getTimeSet(this);
        if (i == 2 || i == 3) {
            progressBar2.setMessage("???????????? ????????? ????????? ????????? ?????????.");
        } else {
            progressBar2.setMessage("????????? ?????????...");
        }
        progressBar2.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar2.setIndeterminate(true);
        progressBar2.setCancelable(false);

        progressBar2.show();
    }

    public void hideProgressDialog() {
        progressBar2.dismiss();
        bluetoothCon.getTimeSet(null);

        if (i == 2 || i == 3) {
            navi.callDialog();
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
        builder.setMessage("???????????? ??????????????????.");
        builder.setNegativeButton("??????",
                (dialog, which) -> {
                });
        builder.show();
    }

    public void dialog2() {
        builder.setMessage("?????? ???????????? ??????");
        builder.setNegativeButton("??????",
                (dialog, which) -> {
                });
        builder.show();
    }
}
