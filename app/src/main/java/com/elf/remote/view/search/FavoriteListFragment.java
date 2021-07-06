package com.elf.remote.view.search;

import android.app.Dialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.FragmentFavoriteListBinding;
import com.elf.remote.Application;
import com.elf.remote.CallActivity;
import com.elf.remote.model.data.MovFile;
import com.elf.remote.viewmodel.search.FavoriteListViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class FavoriteListFragment extends DialogFragment implements CallActivity {
    List<MovFile> mySingerList = new ArrayList<>();
    SingerID singerID;

    public FavoriteListFragment() {
    }

    public static FavoriteListFragment getInstance() {
        return new FavoriteListFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentFavoriteListBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorite_list, container, false);
        binding.setViewModel(new FavoriteListViewModel(this));
        binding.executePendingBindings();

        getFileList(".db");

        int size = 0;

        String[] arr = new String[mySingerList.size()];
        for (MovFile temp : mySingerList) {
            arr[size++] = temp.getFname();
        }

        setCancelable(false);

        ArrayAdapter adapter = new ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, arr);

        binding.singerList.setAdapter(adapter);

        binding.singerList.setOnItemClickListener((adapterView, view, i, l) -> {
            singerID.result(mySingerList.get(i).getFname() + ".db");
            dismiss();
        });
        return binding.getRoot();
    }

    @Override
    public void callActivity() {

    }

    @Override
    public void exitActivity() {
        dismiss();
    }

    @Override
    public void callDialog() {

    }

    public void SingerIDcol(SingerID id) {
        singerID = id;
    }

    public interface SingerID {
        void result(String id);
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

    public void getFileList(String fileSel) {
        String rootSD = "";
        File file;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Uri uri;
            String what = "favorite";
            uri = MediaStore.Downloads.EXTERNAL_CONTENT_URI;

            Cursor video_c = Application.applicationContext().getContentResolver().query(uri, null, null, null, null);

            if (video_c != null && video_c.moveToFirst()) {
                do {
                    String path = video_c.getString(video_c.getColumnIndex("_data"));
                    if (path.contains(what)) {
                        rootSD = new File(path).getParent();
                    }
                } while (video_c.moveToNext());
            }
            if (video_c != null) {
                video_c.close();
            }

            file = new File(rootSD);
        } else {
            rootSD = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ElfData/";
            file = new File(rootSD + "favorite");
        }

        File[] list = file.listFiles((file1, s) -> s.endsWith(fileSel));

        if (list == null) {
            Log.d("없어", "아무것도 없다구");
        } else {
            for (File value : list) {
                if (value.getName().contains("pending")) continue;
                if (value.getName().contains("myResv")) continue;

                MovFile myList = new MovFile();

                myList.setFname(value.getName().substring(0, value.getName().indexOf(".db")));

                mySingerList.add(myList);
            }
        }
    }
}