package com.elf.remote.view.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.elf.mvvmremote.R;


public class ExitFragment extends DialogFragment {
    public static final String TAG_EVENT_DIALOG = "dialog_event";

    public ExitFragment() {
    }

    public static ExitFragment getInstance() {
        return new ExitFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_exit, container);
        Button con = v.findViewById(R.id.btn_no);
        Button ex = v.findViewById(R.id.btn_confirm);
        ex.setOnClickListener(view -> {
            System.exit(0);
            dismiss();
        });

        con.setOnClickListener(view -> dismiss());
        setCancelable(false);



        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}