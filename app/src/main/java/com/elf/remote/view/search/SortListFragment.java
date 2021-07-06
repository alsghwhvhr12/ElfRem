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
import com.elf.mvvmremote.databinding.FragmentSortListBinding;
import com.elf.remote.SubCall;
import com.elf.remote.viewmodel.search.SortListViewModel;

import java.util.Objects;

public class SortListFragment extends DialogFragment implements SubCall {
    SortList ssl;

    public SortListFragment() {
    }

    public static SortListFragment getInstance() {
        return new SortListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentSortListBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sort_list, container, false);
        binding.setViewModel(new SortListViewModel(this));
        binding.executePendingBindings();
        setCancelable(false);

        Window window = Objects.requireNonNull(getDialog()).getWindow();
        window.setGravity(Gravity.CENTER | Gravity.BOTTOM);

        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 100;
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
        ssl.result(1);
        dismiss();
    }

    @Override
    public void twoCall() {
        ssl.result(2);
        dismiss();
    }

    @Override
    public void threeCall() {
        ssl.result(3);
        dismiss();
    }

    @Override
    public void fourCall() {
        ssl.result(4);
        dismiss();
    }

    @Override
    public void fiveCall() {
        ssl.result(5);
        dismiss();
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

    public void setSortList(SortList sl) {
        ssl = sl;
    }

    public interface SortList {
        void result(int kind);
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