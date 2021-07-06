package com.elf.remote.view.banjugi;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.ActivityBanConnectBinding;
import com.elf.remote.CallActivity;
import com.elf.remote.model.bluetooth.BluetoothCon;
import com.elf.remote.model.data.VerSionMachin;
import com.elf.remote.utils.RecycleUtils;
import com.elf.remote.viewmodel.banjugi.BanConnectViewModel;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BanConnect extends AppCompatActivity implements CallActivity {
    private int i = 0;
    int selectDevice;
    ActivityBanConnectBinding binding;
    BluetoothCon bluetoothCon;

    BluetoothAdapter mBluetoothAdapter;
    SimpleAdapter adapterDevice;
    SimpleAdapter adapterPaired;

    List<Map<String, String>> dataDevice;
    List<Map<String, String>> dataPaired;
    List<BluetoothDevice> bluetoothDevices;
    List<BluetoothDevice> mPairedDevices;

    Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ban_connect);
        binding.setViewModel(new BanConnectViewModel(this));
        binding.executePendingBindings();

        bluetoothCon = new BluetoothCon(this);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBluetoothSearchReceiver, filter);

        dataDevice = new ArrayList<>();
        adapterDevice = new SimpleAdapter(this, dataDevice, android.R.layout.simple_list_item_1, new String[]{"name"}, new int[]{android.R.id.text1});
        binding.findDevice.setAdapter(adapterDevice);

        dataPaired = new ArrayList<>();
        adapterPaired = new SimpleAdapter(this, dataPaired, android.R.layout.simple_list_item_1, new String[]{"name"}, new int[]{android.R.id.text1});
        binding.addDevice.setAdapter(adapterPaired);

        bluetoothDevices = new ArrayList<>();
        mPairedDevices = new ArrayList<>();

        selectDevice = -1;

        binding.findDevice.setOnItemClickListener((adapterView, view, i, l) -> {
            BluetoothDevice device = bluetoothDevices.get(i);
            try {
                //선택한 디바이스 페어링 요청
                Method method = device.getClass().getMethod("createBond", (Class[]) null);
                method.invoke(device, (Object[]) null);
                selectDevice = i;
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread = new Thread(new Runnable() {
            boolean what = true;

            @Override
            public void run() {
                while (what) {
                    if (VerSionMachin.isCon()) {
                        finish();
                        what = false;
                    }
                }
            }
        });

        binding.addDevice.setOnItemClickListener((adapterView, view, i, l) -> {
            if (!thread.isAlive()) {
                try {
                    bluetoothCon.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (bluetoothCon.isThat) {
                    bluetoothCon.isThat = false;
                    bluetoothCon.connectSelectedDevice(mPairedDevices.get(i));
                    thread.start();
                }
            }
        });

        bluetoothOn();
    }

    @Override
    public void callActivity() {
        BanListEditFragment edit = BanListEditFragment.getInstance();
        edit.show(getSupportFragmentManager(), "DIALOG");
        edit.DelPair(position -> {
            dataPaired.remove(position);
            adapterPaired.notifyDataSetChanged();
        });
    }

    @Override
    public void exitActivity() {
        finish();
    }

    @Override
    public void callDialog() {
        if (i == 0) {
            mBluetoothAdapter.startDiscovery();
            i = 1;
        } else {
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
            i = 0;
        }
    }

    @Override
    protected void onDestroy() {
        RecycleUtils.recursiveRecycle(getWindow().getDecorView());
        unregisterReceiver(mBluetoothSearchReceiver);
        super.onDestroy();
    }

    BroadcastReceiver mBluetoothSearchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            assert action != null;
            switch (action) {
                //블루투스 디바이스 검색 종료
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    dataDevice.clear();
                    binding.findBtn.setBackgroundResource(R.drawable.click_bl_cancle);
                    binding.probar.setVisibility(View.VISIBLE);
                    i = 1;
                    break;
                //블루투스 디바이스 찾음
                case BluetoothDevice.ACTION_FOUND:
                    //검색한 블루투스 디바이스의 객체를 구한다
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    //데이터 저장
                    Map map = new HashMap();
                    assert device != null;
                    if (device.getName() != null) {
                        if (device.getName().startsWith("ELF")) {
                            for (Map asd : dataPaired) {
                                if (device.getName().equals(asd.get("name"))) return;
                            }
                            map.put("name", device.getName()); //device.getName() : 블루투스 디바이스의 이름
                            map.put("address", device.getAddress()); //device.getAddress() : 블루투스 디바이스의 MAC 주소
                            if (dataDevice.size() > 0) {
                                for (int i = 0; i < dataDevice.size(); i++) {
                                    if (!dataDevice.get(i).get("name").equals(map.get("name"))) {
                                        dataDevice.add(map);
                                        bluetoothDevices.add(device);
                                    }
                                }
                            } else {
                                dataDevice.add(map);
                                bluetoothDevices.add(device);
                            }
                        }
                    }
                    //리스트 목록갱신
                    adapterDevice.notifyDataSetChanged();
                    break;
                //블루투스 디바이스 검색 종료
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    binding.findBtn.setBackgroundResource(R.drawable.click_bl_search);
                    binding.probar.setVisibility(View.INVISIBLE);
                    i = 0;
                    break;
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    BluetoothDevice paired = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    assert paired != null;
                    if (paired.getBondState() == BluetoothDevice.BOND_BONDED) {
                        //데이터 저장
                        Map map2 = new HashMap();
                        map2.put("name", paired.getName()); //device.getName() : 블루투스 디바이스의 이름
                        map2.put("address", paired.getAddress()); //device.getAddress() : 블루투스 디바이스의 MAC 주소
                        dataPaired.add(map2);
                        mPairedDevices.add(paired);
                        //리스트 목록갱신
                        adapterPaired.notifyDataSetChanged();
                        //검색된 목록
                        if (selectDevice != -1) {
                            bluetoothDevices.remove(selectDevice);

                            dataDevice.remove(selectDevice);
                            adapterDevice.notifyDataSetChanged();
                            selectDevice = -1;
                        }
                    }
                    break;
            }
        }
    };

    public void GetListPairedDevice() {
        Set<BluetoothDevice> pairedDevice = mBluetoothAdapter.getBondedDevices();

        if (pairedDevice.size() > 0) {
            for (BluetoothDevice device : pairedDevice) {
                //데이터 저장
                if (device.getName().startsWith("ELF")) {
                    Map map = new HashMap();
                    map.put("name", device.getName()); //device.getName() : 블루투스 디바이스의 이름
                    map.put("address", device.getAddress()); //device.getAddress() : 블루투스 디바이스의 MAC 주소
                    dataPaired.add(map);
                    mPairedDevices.add(device);
                }
            }
            adapterPaired.notifyDataSetChanged();
        }
        //리스트 목록갱신
    }

    void bluetoothOn() {
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "블루투스를 지원하지 않는 기기입니다.", Toast.LENGTH_LONG).show();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent intentBluetoothEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                BluetoothChk.launch(intentBluetoothEnable);
            } else GetListPairedDevice();
        }
    }

    ActivityResultLauncher<Intent> BluetoothChk = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            GetListPairedDevice();
        }
    });
}