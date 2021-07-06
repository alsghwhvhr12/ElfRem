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
import com.elf.mvvmremote.databinding.FragmentJkindBinding;
import com.elf.remote.SubCall;
import com.elf.remote.viewmodel.search.JKindViewModel;

import java.util.Objects;

public class JKindFragment extends DialogFragment implements SubCall {
    Jkind jki;

    public JKindFragment() {
    }

    public static JKindFragment getInstance() {
        return new JKindFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentJkindBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_jkind, container, false);
        binding.setViewModel(new JKindViewModel(this));
        binding.executePendingBindings();
        setCancelable(false);
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        window.setGravity(Gravity.START | Gravity.TOP);


        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 30;
        params.y = 300;
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
        jki.result(1);
        dismiss();
    }

    @Override
    public void twoCall() {
        jki.result(2);
        dismiss();
    }

    @Override
    public void threeCall() {
        jki.result(3);
        dismiss();
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

    public void setJkind(Jkind jk) {
        jki = jk;
    }

    public interface Jkind {
        void result(int jkind);
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