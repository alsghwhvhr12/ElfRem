package com.elf.remote.view.settings;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.FragmentStep4Binding;
import com.elf.remote.CallActivity;
import com.elf.remote.model.bluetooth.BluetoothCon;
import com.elf.remote.viewmodel.settings.Step4ViewModel;

import java.util.Timer;
import java.util.TimerTask;


public class Step4Fragment extends AppCompatActivity implements CallActivity {
    FragmentStep4Binding binding;
    BluetoothCon bluetoothCon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Step4ViewModel model = new Step4ViewModel(this);

        binding = DataBindingUtil.setContentView(this, R.layout.fragment_step4);
        binding.setViewModel(model);
        binding.executePendingBindings();

        bluetoothCon = new BluetoothCon(this);

        model.onCreate();

        bluetoothCon.commandBluetooth("SMEQSTART\n");

        binding.pro1.setMax(120);
        binding.pro2.setMax(120);
        binding.pro3.setMax(120);
        binding.pro4.setMax(120);
        binding.pro5.setMax(120);
        binding.pro6.setMax(120);
        binding.pro7.setMax(120);
        binding.pro8.setMax(120);
        binding.pro9.setMax(120);
        binding.pro10.setMax(120);
        binding.pro11.setMax(120);
        binding.pro12.setMax(120);
        binding.pro13.setMax(120);
        binding.pro14.setMax(120);
        binding.pro15.setMax(120);
        binding.pro16.setMax(120);
        binding.pro17.setMax(120);
        binding.pro18.setMax(120);
        binding.pro19.setMax(120);
        binding.pro20.setMax(120);
        binding.pro21.setMax(120);
        binding.pro22.setMax(120);
        binding.pro23.setMax(120);
        binding.pro24.setMax(120);
        binding.pro25.setMax(120);
        binding.pro26.setMax(120);
        binding.pro27.setMax(120);
        binding.pro28.setMax(120);
        binding.pro29.setMax(120);
        binding.pro30.setMax(120);
        binding.pro31.setMax(120);
        binding.pro32.setMax(120);

        startTimerTask();
    }

    TimerTask timerTask;
    Timer timer = new Timer();

    private synchronized void startTimerTask() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                BluetoothCon.readData();
                binding.pro1.setProgress(BluetoothCon.aa[0]);
                binding.pro2.setProgress(BluetoothCon.aa[1]);
                binding.pro3.setProgress(BluetoothCon.aa[2]);
                binding.pro4.setProgress(BluetoothCon.aa[3]);
                binding.pro5.setProgress(BluetoothCon.aa[4]);
                binding.pro6.setProgress(BluetoothCon.aa[5]);
                binding.pro7.setProgress(BluetoothCon.aa[6]);
                binding.pro8.setProgress(BluetoothCon.aa[7]);
                binding.pro9.setProgress(BluetoothCon.aa[8]);
                binding.pro10.setProgress(BluetoothCon.aa[9]);
                binding.pro11.setProgress(BluetoothCon.aa[10]);
                binding.pro12.setProgress(BluetoothCon.aa[11]);
                binding.pro13.setProgress(BluetoothCon.aa[12]);
                binding.pro14.setProgress(BluetoothCon.aa[13]);
                binding.pro15.setProgress(BluetoothCon.aa[14]);
                binding.pro16.setProgress(BluetoothCon.aa[15]);
                binding.pro17.setProgress(BluetoothCon.aa[16]);
                binding.pro18.setProgress(BluetoothCon.aa[17]);
                binding.pro19.setProgress(BluetoothCon.aa[18]);
                binding.pro20.setProgress(BluetoothCon.aa[19]);
                binding.pro21.setProgress(BluetoothCon.aa[20]);
                binding.pro22.setProgress(BluetoothCon.aa[21]);
                binding.pro23.setProgress(BluetoothCon.aa[22]);
                binding.pro24.setProgress(BluetoothCon.aa[23]);
                binding.pro25.setProgress(BluetoothCon.aa[24]);
                binding.pro26.setProgress(BluetoothCon.aa[25]);
                binding.pro27.setProgress(BluetoothCon.aa[26]);
                binding.pro28.setProgress(BluetoothCon.aa[27]);
                binding.pro29.setProgress(BluetoothCon.aa[28]);
                binding.pro30.setProgress(BluetoothCon.aa[29]);
                binding.pro31.setProgress(BluetoothCon.aa[30]);
                binding.pro32.setProgress(BluetoothCon.aa[31]);
            }
        };

        timer.schedule(timerTask, 0, 110);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        timerTask.cancel();
        //스마트EQ 중단
        bluetoothCon.commandBluetooth("SMEQSTOP\n");
        //스피커 오프
        bluetoothCon.commandBluetooth("SMEQSPKOFF\n");
        BluetoothCon.aa = new byte[32];
        BluetoothCon.chk = 0;
        BluetoothCon.Read = 0;
        BluetoothCon.Write = 0;
        BluetoothCon.asd = new byte[1024][64];
        super.onDestroy();
    }

    @Override
    public void callActivity() {
        timerTask.cancel();
        //스마트EQ 중단
        bluetoothCon.commandBluetooth("SMEQSTOP\n");
        //스피커 오프
        bluetoothCon.commandBluetooth("SMEQSPKOFF\n");
        BluetoothCon.aa = new byte[32];
        BluetoothCon.chk = 0;
        BluetoothCon.Read = 0;
        BluetoothCon.Write = 0;
        BluetoothCon.asd = new byte[1024][64];
    }

    @Override
    public void exitActivity() {
        finish();
        SpeakerFragment.speakerFragment.dismissAllowingStateLoss();
    }

    @Override
    public void callDialog() {
        SensFragment ts = SensFragment.getInstance();
        ts.show(getSupportFragmentManager(), "sens");
    }
}