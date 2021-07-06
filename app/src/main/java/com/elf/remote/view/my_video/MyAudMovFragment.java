package com.elf.remote.view.my_video;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableArrayList;
import androidx.fragment.app.DialogFragment;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.FragmentMyAudmovSelBinding;
import com.elf.remote.ListEdit;
import com.elf.remote.model.data.MovFile;
import com.elf.remote.view.adapter.MovAdapter;
import com.elf.remote.viewmodel.MovFileViewModel;
import com.elf.remote.viewmodel.my_video.MyAudMovViewModel;

import java.io.File;
import java.util.ArrayList;

@SuppressLint({"SetTextI18n", "DefaultLocale"})
public class MyAudMovFragment extends DialogFragment implements ListEdit {
    private static MovAdapter myAdapter2;
    MyAudMovViewModel model;
    ArrayList<MovFile> fileData;
    SelList selL;
    int jong;
    String filePath;
    String fileSel;

    private FragmentMyAudmovSelBinding binding;

    public MyAudMovFragment() {
    }

    public static MyAudMovFragment getInstance() {
        return new MyAudMovFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        model = new MyAudMovViewModel(this);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_audmov_sel, container, false);
        binding.setViewModel(model);
        binding.executePendingBindings();

        Bundle bundle = getArguments();
        if (bundle != null) {
            jong = bundle.getInt("jong");
        }

        if (jong == 1) {
            fileSel = ".mp4";
            filePath = "/DCIM";
            binding.seltop.setBackgroundResource(R.drawable.gmovlsttop2);
            binding.selpre.setBackgroundResource(R.drawable.click_my_clpre);
        } else if (jong == 2) {
            fileSel = ".mp4";
            filePath = "/ElfData/baseVideo";
            binding.seltop.setBackgroundResource(R.drawable.gmovlsttop3);
            binding.selpre.setBackgroundResource(R.drawable.click_my_clpre);
        } else if (jong == 3) {
            fileSel = ".mp3";
            filePath = "/ElfData/baseAudio";
            binding.seltop.setBackgroundResource(R.drawable.gmovlsttop4);
            binding.selpre.setBackgroundResource(R.drawable.click_my_audpre);
        }

        model.getFileList(fileSel, filePath, jong);

        binding.audMovList.setOnItemClickListener((adapterView, view, i, l) -> {
            fileData = model.fileData;
            String path = fileData.get(i).getPath();
            if (path.equals("..")) {
                model.prevPath(jong, fileSel);
                binding.audMovList.setSelector(R.drawable.list_sel2);
            } else {
                model.nextPath(path, jong, fileSel);
                binding.audMovList.setSelector(R.drawable.list_sel);
            }
        });

        return binding.getRoot();
    }

    @BindingAdapter("app:items2")
    public static void setAudMovFileList(ListView listView, ObservableArrayList<MovFileViewModel> movFiles) {
        if (listView.getAdapter() == null) {
            myAdapter2 = new MovAdapter();
            listView.setAdapter(myAdapter2);
        } else {
            myAdapter2 = (MovAdapter) listView.getAdapter();
        }

        myAdapter2.addAll(movFiles, 2);
    }


    @Override
    public void editList() {
    }

    @Override
    public void dleList() {
    }

    @Override
    public void exitList() {
        dismiss();
    }

    @Override
    public void chgFile() {
    }

    @Override
    public void selList() {
        int count, checked;
        count = myAdapter2.getCount();
        fileData = model.fileData;
        if (count > 0) {
            checked = binding.audMovList.getCheckedItemPosition();
            if (checked > -1 && checked < count) {
                File file = new File(fileData.get(checked).getPath());
                if (!file.isDirectory()) {
                    Intent intent = new Intent(getContext(), VideoViewFragment.class);
                    if (jong == 3) intent.putExtra("kind", 4);
                    intent.putExtra("path", fileData.get(checked).getPath());
                    requireContext().startActivity(intent);
                }
            }
        }
    }

    @Override
    public void shareList() {
        int count, checked;
        count = myAdapter2.getCount();
        fileData = model.fileData;
        if (count > 0) {
            checked = binding.audMovList.getCheckedItemPosition();
            if (checked > -1 && checked < count) {
                File file = new File(fileData.get(checked).getPath());
                if (!file.isDirectory()) {
                    selL.finish(fileData.get(checked).getPath(), fileData.get(checked).getFname());
                    dismiss();
                }
            }
        }
    }

    public void setDialogR(SelList dialogR) {
        selL = dialogR;
    }

    public interface SelList {
        void finish(String path, String Fname);
    }
}