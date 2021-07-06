package com.elf.remote.view.my_recorded;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.FragmentRecOkBinding;
import com.elf.remote.SubCall;
import com.elf.remote.model.data.VerSionMachin;
import com.elf.remote.model.usb.UsbCon;
import com.elf.remote.viewmodel.my_recorded.RecOkViewModel;

import java.util.HashMap;


public class RecOkFragment extends DialogFragment implements SubCall {

    public RecOkFragment() {
    }

    public static RecOkFragment getInstance() {
        return new RecOkFragment();
    }

    public void readVideo(MyRecorded video) {
        videoFragment = video;
    }

    UsbCon usbCon;

    MyRecorded videoFragment;
    FragmentRecOkBinding binding;
    myT mm;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_rec_ok, container, false);
        binding.setViewModel(new RecOkViewModel(this));
        binding.executePendingBindings();

        if (VerSionMachin.getName().equals("G10"))
            binding.bakcBase.setBackgroundResource(R.drawable.usblinkchkg);
        else binding.bakcBase.setBackgroundResource(R.drawable.usblinkchk);

        Bundle bundle = getArguments();
        String mov = null;
        if (bundle != null) {
            mov = bundle.getString("mov");
        }

        setCancelable(false);

        usbCon = new UsbCon(getContext(), new IntentFilter(), this, mov, videoFragment);
        UsbManager manager = (UsbManager) requireContext().getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        for (UsbDevice usbDevice : deviceList.values()) {
            usbCon.device = usbDevice;
        }

        usbCon.UsbAttached();

        binding.frm1.setVisibility(View.GONE);

        mm = new myT(6000, 1000);
        mm.start();

        return binding.getRoot();
    }

    @Override
    public void closeCall() {
        if (!VerSionMachin.isRdy()) dialog();
    }

    @Override
    public void oneCall() {

    }

    @Override
    public void twoCall() {

    }

    @Override
    public void threeCall() {

    }

    @Override
    public void fourCall() {

    }

    @Override
    public void fiveCall() {

    }

    @Override
    public void sixCall() {

    }

    @Override
    public void sevenCall() {

    }

    @Override
    public void eightCall() {

    }

    class myT extends CountDownTimer {

        public myT(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
        }

        @Override
        public void onFinish() {
            if (usbCon.device == null) {
                binding.frm1.setVisibility(View.VISIBLE);
                requireContext().registerReceiver(usbCon.mUsbDeviceReceiver, usbCon.filter);

                UsbManager manager = (UsbManager) requireContext().getSystemService(Context.USB_SERVICE);
                HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
                for (UsbDevice usbDevice : deviceList.values()) {
                    usbCon.device = usbDevice;
                }

                if (usbCon.device != null) {
                    dismiss();
                    usbCon.dbDownLoad();
                }
            } else {
                usbCon.dbDownLoad();
            }
            usbCon.alertDialog.dismiss();
        }
    }

    public void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("USB연결을 종료하면 동영상 생성을\n완료할 수 없습니다.\n그래도 종료하시겠습니까?");
        builder.setNegativeButton("취소",
                (dialog, which) -> {
                });
        builder.setPositiveButton("확인",
                (dialog, which) -> {
                    videoFragment.startPreview();
                    requireContext().unregisterReceiver(usbCon.mUsbDeviceReceiver);
                    dismiss();
                });
        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}