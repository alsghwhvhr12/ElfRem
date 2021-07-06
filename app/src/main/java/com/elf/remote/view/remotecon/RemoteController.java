package com.elf.remote.view.remotecon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.ActivityRemoteController2Binding;
import com.elf.mvvmremote.databinding.ActivityRemoteControllerBinding;
import com.elf.remote.RemoteButton;
import com.elf.remote.model.bluetooth.BluetoothCon;
import com.elf.remote.model.bluetooth.BtDevice;
import com.elf.remote.model.data.VerSionMachin;
import com.elf.remote.utils.RecycleUtils;
import com.elf.remote.viewmodel.remotecon.RemoteControllerViewModel;

public class RemoteController extends AppCompatActivity implements RemoteButton {
    ActivityRemoteControllerBinding activityRemoteControllerBinding;
    ActivityRemoteController2Binding activityRemoteController2Binding;
    BluetoothCon bluetoothCon = new BluetoothCon(this);
    public static Activity remoteController;
    int no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (BtDevice.getDevice() == null) {
            activityRemoteControllerBinding = DataBindingUtil.setContentView(this, R.layout.activity_remote_controller);
            activityRemoteControllerBinding.setViewModel(new RemoteControllerViewModel(this, bluetoothCon, builder));
            activityRemoteControllerBinding.executePendingBindings();
            Intent intent = getIntent();
            int no = intent.getIntExtra("btnOn", 0);

            if (no == 1) {
                activityRemoteControllerBinding.xbtn.setVisibility(View.VISIBLE);
            }
        } else {
            if (VerSionMachin.getName().equals("G10")) {
                activityRemoteController2Binding = DataBindingUtil.setContentView(this, R.layout.activity_remote_controller2);
                activityRemoteController2Binding.setViewModel(new RemoteControllerViewModel(this, bluetoothCon, builder));
                activityRemoteController2Binding.executePendingBindings();

                Intent intent = getIntent();
                no = intent.getIntExtra("btnOn", 0);

                if (no == 1) {
                    activityRemoteController2Binding.xbtn.setVisibility(View.VISIBLE);
                }
            } else {
                activityRemoteControllerBinding = DataBindingUtil.setContentView(this, R.layout.activity_remote_controller);
                activityRemoteControllerBinding.setViewModel(new RemoteControllerViewModel(this, bluetoothCon, builder));
                activityRemoteControllerBinding.executePendingBindings();

                activityRemoteControllerBinding.endBtn.setEnabled(true);
                activityRemoteControllerBinding.secEndBtn.setEnabled(true);
                activityRemoteControllerBinding.secStartBtn.setEnabled(true);
                activityRemoteControllerBinding.roundBtn.setEnabled(true);
                activityRemoteControllerBinding.stopBtn.setEnabled(true);
                activityRemoteControllerBinding.nextBtn.setEnabled(true);

                Intent intent = getIntent();
                no = intent.getIntExtra("btnOn", 0);

                if (no == 1) {
                    activityRemoteControllerBinding.xbtn.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (no == 1) {
            if (VerSionMachin.getName().equals("G10")) {
                activityRemoteController2Binding.xbtn.setVisibility(View.INVISIBLE);
            } else {
                activityRemoteControllerBinding.xbtn.setVisibility(View.INVISIBLE);
            }
        }
        RecycleUtils.recursiveRecycle(getWindow().getDecorView());
        remoteController = null;
        super.onDestroy();
    }

    @Override
    public void closeCall() {
        finish();
    }

    @Override
    public void akboCall() {
        BlankFragment exit = BlankFragment.getInstance();
        Bundle bundle = new Bundle();
        exit.setArguments(bundle);
        exit.show(getSupportFragmentManager(), BlankFragment.TAG_EVENT_DIALOG);
    }

    //region 안씀
    @Override
    public void start() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void restart() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void wkey() {
    }

    @Override
    public void mkey() {
    }

    @Override
    public void orgkey() {
    }

    @Override
    public void orgtempo() {
    }

    @Override
    public void keyup() {
    }

    @Override
    public void keydn() {
    }

    @Override
    public void tempoup() {
    }

    @Override
    public void tempodn() {
    }

    @Override
    public void ending() {
    }

    @Override
    public void onerepeat() {
    }

    @Override
    public void sect() {
    }

    @Override
    public void nextSong() {
    }

    @Override
    public void sectEnd() {
    }

    @Override
    public void sectStop() {
    }
    //endregion
}