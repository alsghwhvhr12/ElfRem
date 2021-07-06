package com.elf.remote.view.search;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.FragmentSLoveAddBinding;
import com.elf.remote.SubCall;
import com.elf.remote.viewmodel.search.SLoveAddViewModel;

public class SLoveAddFragment extends DialogFragment implements SubCall {
    private FragmentSLoveAddBinding binding;
    SLoveAddViewModel model;

    public SLoveAddFragment() {
    }

    public static SLoveAddFragment getInstance() {
        return new SLoveAddFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();

        model = new SLoveAddViewModel(this, bundle);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_s_love_add, container, false);
        binding.setViewModel(model);
        binding.executePendingBindings();

        setCancelable(false);

        model.onCreate();

        if (model.loveKind.equals("myLove")) {
            binding.titleLove.setBackgroundResource(R.drawable.glmylv_topbar);
            binding.sloveList.setAdapter(model.loveGroupAdapter);
        } else {
            binding.titleLove.setBackgroundResource(R.drawable.glcuslv_topbar);
            binding.sloveList.setAdapter(model.customerGroupAdapter);
        }

        return binding.getRoot();
    }

    @Override
    public void closeCall() {
        dismiss();
    }

    @Override
    public void oneCall() {
        SloAddFragment add = SloAddFragment.getInstance();
        Bundle bundle = new Bundle();
        bundle.putString("title", "");
        bundle.putString("memo", "");
        add.setArguments(bundle);
        add.show(getChildFragmentManager(), "SloAdd");

        add.addDialog((title, memo) -> {
            model.SloAdd(title, memo);

            if (model.loveKind.equals("myLove")) {
                binding.sloveList.setAdapter(model.loveGroupAdapter);
            } else {
                binding.sloveList.setAdapter(model.customerGroupAdapter);
            }
        });
    }

    @Override
    public void twoCall() {
        Toast.makeText(getContext(), "애창곡 목록이 저장되었습니다.", Toast.LENGTH_SHORT).show();
        dismiss();
    }

    @Override
    public void threeCall() {

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