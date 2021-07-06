package com.elf.remote.view.search;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.FragmentSubsunEditBinding;
import com.elf.remote.SubCall;
import com.elf.remote.viewmodel.search.SubsunEditViewModel;

@SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
public class SubsunEditFragment extends DialogFragment implements SubCall {
    FragmentSubsunEditBinding binding;
    int kind;

    SubsunEditViewModel model;

    public SubsunEditFragment() {
    }

    public static SubsunEditFragment getInstance() {
        return new SubsunEditFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        SearchMusic searchMusic = (SearchMusic) getContext();
        model = new SubsunEditViewModel(this, bundle, searchMusic);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_subsun_edit, container, false);
        binding.setViewModel(model);
        binding.executePendingBindings();

        if (bundle != null) {
            kind = bundle.getInt("kind");
        }

        binding.interval.setSelected(true);

        model.onCreate();

        setCancelable(false);

        if (kind == 2) {
            binding.toptop.setBackgroundResource(R.drawable.lrsveditbar);
        } else {
            binding.toptop.setBackgroundResource(R.drawable.lveditbar);
        }

        twoCall();
        threeCall();

        return binding.getRoot();
    }

    @Override
    public void closeCall() {
        dismiss();
    }

    @Override
    public void oneCall() {
        dismiss();
    }


    @Override
    public void twoCall() {
    }

    @Override
    public void threeCall() {
    }

    @Override
    public void fourCall() {
        binding.tempo.setSelected(true);
        binding.interval.setSelected(false);
        model.tmpSec = true;
        model.intSec = false;
    }

    @Override
    public void fiveCall() {
        binding.interval.setSelected(true);
        binding.tempo.setSelected(false);
        model.tmpSec = false;
        model.intSec = true;
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