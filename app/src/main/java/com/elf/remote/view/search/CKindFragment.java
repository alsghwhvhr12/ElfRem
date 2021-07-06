package com.elf.remote.view.search;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.FragmentCkindBinding;
import com.elf.remote.SubCall;
import com.elf.remote.viewmodel.search.CKindViewModel;

import java.util.Objects;

public class CKindFragment extends DialogFragment implements SubCall {
    Ckind cki;

    public CKindFragment() {
    }

    public static CKindFragment getInstance() {
        return new CKindFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentCkindBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ckind, container, false);
        binding.setViewModel(new CKindViewModel(this));
        binding.executePendingBindings();
        setCancelable(false);
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        window.setGravity(Gravity.START | Gravity.TOP);


        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 30;
        params.y = 200;
        window.setAttributes(params);

        setCancelable(false);

        return binding.getRoot();
    }

    @Override
    public void closeCall() {
        dismiss();
    }

    @Override
    public void oneCall() {
        cki.result(1);
        dismiss();
    }

    @Override
    public void twoCall() {
        cki.result(2);
        dismiss();
    }

    @Override
    public void threeCall() {
        cki.result(3);
        dismiss();
    }

    @Override
    public void fourCall() {
        cki.result(4);
        dismiss();
    }

    @Override
    public void fiveCall() {
        cki.result(5);
        dismiss();
    }

    @Override
    public void sixCall() {
        cki.result(6);
        dismiss();
    }

    @Override
    public void sevenCall() {

    }

    @Override
    public void eightCall() {

    }

    public void setCkind(Ckind ck) {
        cki = ck;
    }

    public interface Ckind {
        void result(int ckind);
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