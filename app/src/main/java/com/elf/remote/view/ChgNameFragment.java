package com.elf.remote.view;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.FragmentChgnameBinding;
import com.elf.remote.CallActivity;
import com.elf.remote.viewmodel.ChgNameViewModel;

public class ChgNameFragment extends DialogFragment implements CallActivity {
    private FragmentChgnameBinding binding;
    ChgName chg;

    public ChgNameFragment() {
    }

    public static ChgNameFragment getInstance() {
        return new ChgNameFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chgname, container, false);
        binding.setViewModel(new ChgNameViewModel(this));
        binding.executePendingBindings();

        Bundle bundle = getArguments();

        String text = null;
        if (bundle != null) {
            text = bundle.getString("text");
        }

        binding.chgtxt.setText(text);
        setCancelable(false);

        return binding.getRoot();
    }

    @Override
    public void callActivity() {
        chg.finish(binding.chgtxt.getText() + ".mp4");
        String asd = String.valueOf(binding.chgtxt.getText());
        if (!asd.equals("")) dismiss();
    }

    @Override
    public void exitActivity() {
        chg.finish(" ");
        dismiss();
    }

    @Override
    public void callDialog() {

    }

    public void setDialogR(ChgName dialogR) {
        chg = dialogR;
    }

    public interface ChgName {
        void finish(String result);
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
