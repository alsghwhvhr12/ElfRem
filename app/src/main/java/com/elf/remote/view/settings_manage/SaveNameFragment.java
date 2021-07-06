package com.elf.remote.view.settings_manage;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.FragmentSavenameBinding;
import com.elf.remote.CallActivity;
import com.elf.remote.viewmodel.settings_manage.SaveNameViewModel;

public class SaveNameFragment extends DialogFragment implements CallActivity {
    private FragmentSavenameBinding binding;
    ChgName chg;

    public SaveNameFragment() {
    }

    public static SaveNameFragment getInstance() {
        return new SaveNameFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_savename, container, false);
        binding.setViewModel(new SaveNameViewModel(this));
        binding.executePendingBindings();

        Bundle bundle = getArguments();

        assert bundle != null;
        String text = bundle.getString("text");

        binding.chgtxt.setText(text);
        setCancelable(false);

        return binding.getRoot();
    }

    @Override
    public void callActivity() {
        String asd = String.valueOf(binding.chgtxt.getText());
        if (asd.equals("")) {
            Toast.makeText(getContext(), "제목을 입력해 주세요.", Toast.LENGTH_SHORT).show();
        } else {
            chg.finish(binding.chgtxt.getText() + ".db");
            dismiss();
        }
    }

    @Override
    public void exitActivity() {
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
