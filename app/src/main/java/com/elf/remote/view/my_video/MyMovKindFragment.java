package com.elf.remote.view.my_video;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.FragmentMyMovKindBinding;
import com.elf.remote.CallActivity;
import com.elf.remote.viewmodel.my_video.MyMovKindViewModel;


public class MyMovKindFragment extends DialogFragment implements CallActivity {
    public MyMovKindFragment() {
    }

    int kind;
    SelList selL;

    public static MyMovKindFragment getInstance() {
        return new MyMovKindFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentMyMovKindBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_mov_kind, container, false);
        binding.setViewModel(new MyMovKindViewModel(this));
        binding.executePendingBindings();

        setCancelable(false);

        binding.SaveKind.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.ts) kind = 1;
            else if (i == R.id.at) kind = 2;
        });

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void callActivity() {
        MyAudMovFragment mov = MyAudMovFragment.getInstance();
        Bundle bundle = new Bundle();
        if (kind == 1) {
            bundle.putInt("jong", 1);
            mov.setArguments(bundle);
            mov.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_NoTitleBar);
            mov.show(getParentFragmentManager(), "tag");
            dismiss();
        } else if (kind == 2) {
            bundle.putInt("jong", 2);
            mov.setArguments(bundle);
            mov.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_NoTitleBar);
            mov.show(getParentFragmentManager(), "tag");
            dismiss();
        }

        mov.setDialogR((path, Fname) -> selL.finish(path, Fname));
    }

    @Override
    public void exitActivity() {
        dismiss();
    }

    @Override
    public void callDialog() {

    }

    public void setDialogR(SelList dialogR) {
        selL = dialogR;
    }

    public interface SelList {
        void finish(String path, String Fname);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}