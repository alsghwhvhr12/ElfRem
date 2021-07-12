package com.elf.remote.view.banjugi;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.FragmentBanListEditBinding;
import com.elf.remote.CallActivity;
import com.elf.remote.viewmodel.banjugi.BanListEditViewModel;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BanListEditFragment extends DialogFragment implements CallActivity {
    private FragmentBanListEditBinding binding;
    BluetoothAdapter mBluetoothAdapter;
    SimpleAdapter adapterPaired;
    List<Map<String, String>> dataPaired;
    List<BluetoothDevice> bluetoothDevices;
    BluetoothDevice pairDevice;
    Delcol delcol;

    public BanListEditFragment() {
    }

    public static BanListEditFragment getInstance() {
        return new BanListEditFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        BanListEditViewModel model = new BanListEditViewModel(this);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ban_list_edit, container, false);
        binding.setViewModel(model);
        binding.executePendingBindings();
        setCancelable(false);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        bluetoothDevices = new ArrayList<>();
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dataPaired = new ArrayList<>();
        adapterPaired = new SimpleAdapter(getContext(), dataPaired, android.R.layout.simple_list_item_1, new String[]{"name"}, new int[]{android.R.id.text1});
        binding.editDevice.setAdapter(adapterPaired);

        GetListPairedDevice();

        binding.editDevice.setOnItemClickListener((adapterView, view, i, l) -> binding.editDevice.setItemChecked(i, true));

        return binding.getRoot();
    }

    @Override
    public void callActivity() {
        try {
            pairDevice = bluetoothDevices.get(binding.editDevice.getCheckedItemPosition());
            Method m = pairDevice.getClass().getMethod("removeBond", (Class[]) null);
            m.invoke(pairDevice, (Object[]) null);
            dataPaired.remove(binding.editDevice.getCheckedItemPosition());
            adapterPaired.notifyDataSetChanged();
            delcol.result(binding.editDevice.getCheckedItemPosition());
            bluetoothDevices.remove(binding.editDevice.getCheckedItemPosition());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exitActivity() {
        dismiss();
    }

    @Override
    public void callDialog() {

    }

    public void DelPair(Delcol dc) {
        delcol = dc;
    }

    public interface Delcol {
        void result(int position);
    }

    public void GetListPairedDevice() {
        Set<BluetoothDevice> pairedDevice = mBluetoothAdapter.getBondedDevices();

        if (pairedDevice.size() > 0) {
            for (BluetoothDevice device : pairedDevice) {
                //데이터 저장
                if (device.getName().substring(0,3).equals("ELF")) {
                    Map map = new HashMap();
                    map.put("name", device.getName()); //device.getName() : 블루투스 디바이스의 이름
                    map.put("address", device.getAddress()); //device.getAddress() : 블루투스 디바이스의 MAC 주소
                    dataPaired.add(map);
                    bluetoothDevices.add(device);
                }
            }
            adapterPaired.notifyDataSetChanged();
        }
        //리스트 목록갱신
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
                dismiss();
            }
        };
    }
}