package com.elf.remote.view.search;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.FragmentTimeSetBinding;
import com.elf.remote.Application;
import com.elf.remote.CallActivity;
import com.elf.remote.model.bluetooth.BluetoothCon;
import com.elf.remote.model.bluetooth.BtDevice;
import com.elf.remote.viewmodel.search.TimeSetViewModel;

public class TimeSetFragment extends DialogFragment implements CallActivity {
    SelList selL;

    TimeSetViewModel model;

    public TimeSetFragment() {
    }

    public static TimeSetFragment getInstance() {
        return new TimeSetFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        BluetoothCon bluetoothCon = new BluetoothCon(getContext());
        Bundle bundle = getArguments();
        ProgressDialog progressBar2 = new ProgressDialog(getContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        model = new TimeSetViewModel(this, bluetoothCon, bundle, progressBar2, builder);

        FragmentTimeSetBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_time_set, container, false);
        binding.setViewModel(model);
        binding.executePendingBindings();
        setCancelable(false);

        model.onCreate();

        return binding.getRoot();
    }

    @Override
    public void callActivity() {
        if (model.i == 4) { // 애창곡 목록 삭제
            selL.finish("rowid = ");
            dismiss();
        } else if (model.i == 5) { // 편집 목록 저장
            selL.finish("1");
            dismiss();
        } else if (model.i == 1) {
            FavoriteListFragment ts = FavoriteListFragment.getInstance();
            ts.show(getParentFragmentManager(), "favorite");

            ts.SingerIDcol(id -> {
                String savePath, copyPath = null, savePath2, copyPath2 = null;
                savePath = Application.applicationContext().getApplicationInfo().dataDir + "/databases/mylove.db";
                savePath2 = Application.applicationContext().getApplicationInfo().dataDir + "/databases/myResv.db";
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    Cursor cursor = Application.applicationContext().getContentResolver().query(MediaStore.Downloads.EXTERNAL_CONTENT_URI, null, null, null, null);
                    cursor.moveToFirst();
                    do {
                        String asd = cursor.getString(cursor.getColumnIndex("_data"));
                        if (asd.contains(id)) {
                            copyPath = asd;
                        } else if (asd.contains("myResv")) {
                            copyPath2 = asd;
                        }
                    } while (cursor.moveToNext());
                    cursor.close();
                } else {
                    copyPath = Environment.getExternalStorageDirectory() + "/ElfData/favorite/" + id;
                    copyPath2 = Environment.getExternalStorageDirectory() + "/ElfData/favorite/myResv.db";
                }


                model.copyFileList(savePath, copyPath);
                model.copyFileList(savePath2, copyPath2);

                dismiss();
            });
        } else if (model.i == 99) {
            dismiss();
        } else if (model.i == 6) {
            FavoriteListFragment ts = FavoriteListFragment.getInstance();
            ts.show(getParentFragmentManager(), "favorite");
            ts.SingerIDcol(id -> {
                String midPath;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    midPath = "/Download/favorite/";
                } else {
                    midPath = "/ElfData/favorite/";
                }
                model.onLoad(Environment.getExternalStorageDirectory() + midPath + id);
                dismiss();
            });
        }
    }

    @Override
    public void exitActivity() {
        if (model.i == 5) { // 편집 목록 저장 안함
            selL.finish("2");
        }
        dismiss();
    }

    @Override
    public void callDialog() {
        if (model.i == 2 || model.i == 3) {
            if (!(BtDevice.getDevice() == null)) {
                SendLoveFragment ts = SendLoveFragment.getInstance();
                Bundle bundle = new Bundle();
                bundle.putInt("kind", model.i);
                bundle.putString("loveKind", model.loveKind);
                bundle.putInt("GroupId", model.GroupId);
                bundle.putIntegerArrayList("GroupIds", model.GroupIds);
                ts.setArguments(bundle);
                ts.show(getParentFragmentManager(), "send");
            }
        }
        dismiss();
    }

    public void setDialogR(SelList dialogR) {
        selL = dialogR;
    }

    public interface SelList {
        void finish(String where);
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
