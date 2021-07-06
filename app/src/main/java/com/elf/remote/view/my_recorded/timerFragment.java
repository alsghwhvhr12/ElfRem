package com.elf.remote.view.my_recorded;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.FragmentTimerBinding;
import com.elf.remote.CallActivity;
import com.elf.remote.viewmodel.my_recorded.TimerViewModel;

public class timerFragment extends DialogFragment implements CallActivity {
    OnMyDialogResult mDr;
    SharedPreferences preferences;

    public timerFragment() {
    }

    public static timerFragment getInstance() {
        return new timerFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentTimerBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_timer, container, false);
        binding.setViewModel(new TimerViewModel(this));
        binding.executePendingBindings();
        setCancelable(false);

        String timePre = "timerTip";

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        int time = preferences.getInt(timePre, 0);

        if (time == 3000) {
            binding.Rg.check(R.id.two);
        } else if (time == 6000) {
            binding.Rg.check(R.id.five);
        } else if (time == 11000) {
            binding.Rg.check(R.id.ten);
        } else {
            binding.Rg.check(R.id.nop);
        }

        binding.Rg.setOnCheckedChangeListener((radioGroup, i) -> {
            SharedPreferences.Editor editor = preferences.edit();
            if (i == R.id.nop) {
                if (mDr != null) {
                    mDr.finish(0);
                    editor.putInt(timePre, 0);
                }
            } else if (i == R.id.two) {
                if (mDr != null) {
                    mDr.finish(3000);
                    editor.putInt(timePre, 3000);
                }
            } else if (i == R.id.five) {
                if (mDr != null) {
                    mDr.finish(6000);
                    editor.putInt(timePre, 6000);
                }
            } else if (i == R.id.ten) {
                if (mDr != null) {
                    mDr.finish(11000);
                    editor.putInt(timePre, 11000);
                }
            }
            editor.apply();
        });

        return binding.getRoot();
    }

    @Override
    public void callActivity() {

    }

    @Override
    public void exitActivity() {
        dismiss();
    }

    @Override
    public void callDialog() {

    }

    public void setDialogR(OnMyDialogResult dialogR) {
        mDr = dialogR;
    }

    public interface OnMyDialogResult {
        void finish(int result);
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