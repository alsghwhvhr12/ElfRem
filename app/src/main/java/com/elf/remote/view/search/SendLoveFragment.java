package com.elf.remote.view.search;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.FragmentSendLoveBinding;
import com.elf.remote.CallActivity;
import com.elf.remote.model.bluetooth.BluetoothCon;
import com.elf.remote.viewmodel.search.SendLoveViewModel;

public class SendLoveFragment extends DialogFragment implements CallActivity {

    SendLoveViewModel model;

    public SendLoveFragment() {
    }

    public static SendLoveFragment getInstance() {
        return new SendLoveFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        BluetoothCon bluetoothCon = new BluetoothCon(getContext());
        Bundle bundle = getArguments();
        ProgressDialog progressBar2 = new ProgressDialog(getContext());

        model = new SendLoveViewModel(this, bundle, bluetoothCon, progressBar2);

        FragmentSendLoveBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_send_love, container, false);
        binding.setViewModel(model);
        binding.executePendingBindings();
        setCancelable(false);

        model.onCreate();

        binding.loveList.setAdapter(model.loveCountAdapter);

        if (model.loveCounts.size() == 0) {
            dismiss();

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("애창곡 목록이 없습니다. 기기에서 애창곡 목록을 만들어 주세요");
            builder.setNegativeButton("확인",
                    (dialog, which) -> {
                    });
            builder.show();
        }

        return binding.getRoot();
    }

    @Override
    public void callActivity() {
        dismiss();
    }

    @Override
    public void exitActivity() {
        dismiss();
    }

    @Override
    public void callDialog() {

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
