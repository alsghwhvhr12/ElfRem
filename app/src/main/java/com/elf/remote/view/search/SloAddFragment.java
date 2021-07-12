package com.elf.remote.view.search;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.FragmentSloAddBinding;
import com.elf.remote.CallActivity;
import com.elf.remote.viewmodel.search.SloAddViewModel;

public class SloAddFragment extends DialogFragment implements CallActivity {
    private FragmentSloAddBinding binding;
    addCol ac;

    public SloAddFragment() {
    }

    public static SloAddFragment getInstance() {
        return new SloAddFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_slo_add, container, false);
        binding.setViewModel(new SloAddViewModel(this));
        binding.executePendingBindings();
        setCancelable(false);

        Bundle bundle = getArguments();

        String title = null;
        String memo = null;
        if (bundle != null) {
            title = bundle.getString("title");
            memo = bundle.getString("memo");
        }

        if ("".equals(title)) {
            binding.grpnm.setBackgroundResource(R.drawable.grpaddback);
        } else {
            binding.grpnm.setBackgroundResource(R.drawable.grpnmchgbk);
        }

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        binding.editTitle.setText(title);
        binding.editMemo.setText(memo);

        setCancelable(false);

        return binding.getRoot();
    }

    @Override
    public void callActivity() {
        ac.result(binding.editTitle.getText().toString(), binding.editMemo.getText().toString());
        dismiss();
    }

    @Override
    public void exitActivity() {
        dismiss();
    }

    @Override
    public void callDialog() {
    }

    public void addDialog(addCol col) {
        ac = col;
    }

    public interface addCol {
        void result(String title, String memo);
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