package com.elf.remote.view.search;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.FragmentSubSungokBinding;
import com.elf.remote.SubCall;
import com.elf.remote.model.bluetooth.BluetoothCon;
import com.elf.remote.model.bluetooth.BtDevice;
import com.elf.remote.viewmodel.search.SubSungokViewModel;

public class SubSungokFragment extends DialogFragment implements SubCall {
    FragmentSubSungokBinding binding;
    SelList selL;

    BluetoothCon bluetoothCon = new BluetoothCon(getContext());

    SubSungokViewModel model;

    public SubSungokFragment() {
    }

    public static SubSungokFragment getInstance() {
        return new SubSungokFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        model = new SubSungokViewModel(this, bluetoothCon, bundle);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sub_sungok, container, false);
        binding.setViewModel(model);
        binding.executePendingBindings();

        model.onCreate();
        setCancelable(false);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if (model.kind == 2) {
            binding.editBtn.setBackgroundResource(R.drawable.click_sub_rsedit);
        } else {
            binding.editBtn.setBackgroundResource(R.drawable.click_sub_lvedit);
        }

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void closeCall() {
        dismiss();
    }

    @Override
    public void oneCall() {
        SubsunEditFragment edit = SubsunEditFragment.getInstance();
        Bundle bundle = new Bundle();
        bundle.putInt("kind", model.kind);
        if (model.kind == 1) bundle.putSerializable("song", model.lSong);
        else if (model.kind == 2) bundle.putSerializable("song", model.song);
        else bundle.putSerializable("song", model.cSong);
        bundle.putInt("touch", model.touch);
        edit.setArguments(bundle);
        edit.show(getParentFragmentManager(), "edit");
        dismiss();
    }

    @Override
    public void twoCall() {
        if (model.kind == 2) {
            selL.finish("_id = " + model.song.id, 4);
        } else if (model.kind == 1) {
            selL.finish("rowid = " + model.lSong.Id, 2);
        } else {
            selL.finish("rowid = " + model.cSong.Id, 3);
        }
        dismiss();
    }

    @Override
    public void threeCall() {
        if (!(BtDevice.getDevice() == null)) {
            if (model.record==0) {
                SungokStartFragment sstart = SungokStartFragment.getInstance();
                sstart.show(getParentFragmentManager(), "sstart");
            } else {
                requireActivity().finish();
            }
            dismiss();
        } else {
            dialog();
        }
    }

    @Override
    public void fourCall() {
        if (!(BtDevice.getDevice() == null)) {
            dismiss();
        } else {
            dialog();
        }
    }

    @Override
    public void fiveCall() {
        if (!(BtDevice.getDevice() == null)) {
            dismiss();
        } else {
            dialog();
        }
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

    public void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("반주기를 연결해주세요.");
        builder.setNegativeButton("확인",
                (dialog, which) -> {
                });
        builder.show();
    }

    public void setDialogR(SelList dialogR) {
        selL = dialogR;
    }

    public interface SelList {
        void finish(String where, int id);
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