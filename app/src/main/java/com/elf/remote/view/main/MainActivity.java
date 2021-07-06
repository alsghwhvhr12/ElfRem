package com.elf.remote.view.main;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.ActivityMainBinding;
import com.elf.remote.model.bluetooth.BtDevice;
import com.elf.remote.model.data.VerSionMachin;
import com.elf.remote.utils.RecycleUtils;
import com.elf.remote.view.banjugi.BanConnect;
import com.elf.remote.view.camera.camera;
import com.elf.remote.view.my_recorded.MyRecorded;
import com.elf.remote.view.my_video.MyVideo;
import com.elf.remote.view.remotecon.RemoteController;
import com.elf.remote.view.search.SearchMusic;
import com.elf.remote.view.settings.Settings;
import com.elf.remote.view.settings_manage.SettingsManage;
import com.elf.remote.view.view_recorded.MovListFragment;
import com.elf.remote.viewmodel.main.MainViewModel;

@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private MainViewModel model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ProgressDialog progressBar = new ProgressDialog(this);
        String savePath = getApplicationInfo().dataDir + "/databases/";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        model = new MainViewModel(progressBar, savePath, builder);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setViewModel(model);
        binding.executePendingBindings();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(bluetoothReceiver, filter);

        model.onCreate();

        checkPer();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            PermissionActivity.makeDirAfterR();
        } else {
            PermissionActivity.makeDirBeforeR();
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void checkPer() {
        String[] reqPermissionArray = model.checkPermission();
        for (String check : reqPermissionArray) {
            if (ActivityCompat.checkSelfPermission(this, check) != 0) {
                Intent intent = new Intent(this, PermissionActivity.class);
                intent.putExtra("permission", reqPermissionArray);
                startActivity(intent);
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        ExitFragment exit = ExitFragment.getInstance();
        exit.show(getSupportFragmentManager(), ExitFragment.TAG_EVENT_DIALOG);
    }

    @BindingAdapter({"toastMessage"})
    public static void runMe(View view, String message) {
        if (message != null) {
            switch (message) {
                case "Search": {
                    Intent intent = new Intent(view.getContext(), SearchMusic.class);
                    intent.putExtra("record", 0);
                    view.getContext().startActivity(intent);
                    break;
                }
                case "Remote": {
                    Intent intent = new Intent(view.getContext(), RemoteController.class);
                    view.getContext().startActivity(intent);
                    break;
                }
                case "Ban":
                    view.getContext().startActivity(new Intent(view.getContext(), BanConnect.class));
                    break;
                case "ViewR":
                    view.getContext().startActivity(new Intent(view.getContext(), MovListFragment.class));
                    break;
                case "MyR":
                    if (BtDevice.getDevice() == null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setMessage("녹화는 반주기 연결이 필요합니다.\n반주기를 연결하시겠습니까?");
                        builder.setNegativeButton("취소",
                                (dialog, which) -> {
                                });
                        builder.setPositiveButton("연결",
                                (dialog, which) -> view.getContext().startActivity(new Intent(view.getContext(), BanConnect.class)));
                        builder.show();
                    } else {
                        Intent intent = new Intent(view.getContext(), MyRecorded.class);
                        view.getContext().startActivity(intent);
                    }
                    break;
                case "MyVideo":
                    view.getContext().startActivity(new Intent(view.getContext(), MyVideo.class));
                    break;
                case "Set":
                    view.getContext().startActivity(new Intent(view.getContext(), Settings.class));
                    break;
                case "SetMa":
                    view.getContext().startActivity(new Intent(view.getContext(), SettingsManage.class));
                    break;
                case "Cam":
                    view.getContext().startActivity(new Intent(view.getContext(), camera.class));
                    break;
                case "Cha": {
                    Uri webpage = Uri.parse("https://www.youtube.com/channel/UCleIClZIkMjXQZ7Px5bt49A");
                    Intent WebIntent = new Intent(Intent.ACTION_VIEW, webpage);
                    view.getContext().startActivity(WebIntent);
                    break;
                }
                case "Band": {
                    Uri webpage = Uri.parse("https://band.us/band/70928613");
                    Intent WebIntent = new Intent(Intent.ACTION_VIEW, webpage);
                    view.getContext().startActivity(WebIntent);
                    break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        RecycleUtils.recursiveRecycle(getWindow().getDecorView());
        unregisterReceiver(bluetoothReceiver);
        super.onDestroy();

        model.onDestroy();
    }

    @Override
    protected void onResume() {
        if (VerSionMachin.isCon()) {
            binding.banBtn.setBackgroundResource(R.drawable.click_dev_btn_con);

            if (VerSionMachin.getName().equals("G10")) {
                binding.conImage.setBackgroundResource(R.drawable.mmodelg10);
                binding.inputBtn.setBackgroundResource(R.drawable.blackgun);
                binding.bansetBtn.setBackgroundResource(R.drawable.blackgun);
                binding.inputBtn.setEnabled(false);
                binding.bansetBtn.setEnabled(false);
            } else if(VerSionMachin.getName().equals("919")) {
                binding.conImage.setBackgroundResource(R.drawable.mmodel919);
                binding.inputBtn.setBackgroundResource(R.drawable.click_minput_btn);
                binding.bansetBtn.setBackgroundResource(R.drawable.click_mbanset_btn);
                binding.inputBtn.setEnabled(true);
                binding.bansetBtn.setEnabled(true);
            } else {
                binding.conImage.setBackgroundResource(R.drawable.mmodel909);
                binding.inputBtn.setBackgroundResource(R.drawable.blackgun);
                binding.bansetBtn.setBackgroundResource(R.drawable.blackgun);
                binding.inputBtn.setEnabled(false);
                binding.bansetBtn.setEnabled(false);
            }
        }
        super.onResume();
    }

    int a = 0;
    BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BtDevice.setDevice(intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                binding.bluetooth.setText(BtDevice.getDevice().getName() + "에 연결되었습니다.");
                a++;
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                if (a == 1) {
                    binding.bluetooth.setText("");
                    binding.banBtn.setBackgroundResource(R.drawable.click_dev_btn);
                    binding.conImage.setBackground(null);
                    BtDevice.setDevice(null);
                    VerSionMachin.setCon(false);

                    binding.inputBtn.setBackgroundResource(R.drawable.blackgun);
                    binding.bansetBtn.setBackgroundResource(R.drawable.blackgun);
                    binding.inputBtn.setEnabled(false);
                    binding.bansetBtn.setEnabled(false);

                    if (MyRecorded.myRecorded != null) {
                        MyRecorded myRecorded = (MyRecorded) MyRecorded.myRecorded;
                        myRecorded.finish();

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("반주기 연결이 끊겼습니다.");
                        builder.setPositiveButton("확인",
                                (dialog, which) -> {
                                });
                        builder.show();
                    }
                    a--;
                } else if (a>0) {
                    a--;
                }
            }
        }
    };
}