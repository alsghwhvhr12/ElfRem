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
import com.elf.mvvmremote.databinding.FragmentDelokBinding;
import com.elf.remote.CallActivity;
import com.elf.remote.viewmodel.DelOkViewModel;

public class DelOkFragment extends DialogFragment implements CallActivity {
    DelOk del;

    public DelOkFragment() {
    }

    public static DelOkFragment getInstance() {
        return new DelOkFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentDelokBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_delok, container, false);
        binding.setViewModel(new DelOkViewModel(this));
        binding.executePendingBindings();
        String text = "";
        Bundle bundle = getArguments();

        boolean tt;
        if (bundle != null) {
            tt = bundle.getBoolean("true");
            if (tt) {
                text = "선택한 파일 " + bundle.getString("text") + "개를 삭제하시겠습니까?";
            } else {
                text = bundle.getString("text") + "를 삭제하시겠습니까?";
            }
        }

        binding.deloktxt.setText(text);
        setCancelable(false);

        return binding.getRoot();
    }

    @Override
    public void callActivity() {
        del.finish(true);
        dismiss();
    }

    @Override
    public void exitActivity() {
        del.finish(false);
        dismiss();
    }

    @Override
    public void callDialog() {

    }

    public void setDialogR(DelOk dialogR) {
        del = dialogR;
    }

    public interface DelOk {
        void finish(boolean result);
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
