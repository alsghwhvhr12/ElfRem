package com.elf.remote.viewmodel.settings_manage;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableArrayList;

import com.elf.remote.Application;
import com.elf.remote.CallActivity;
import com.elf.remote.model.data.MovFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Locale;

public class FavoriteSongViewModel extends BaseObservable {
    public final ObservableArrayList<DBFileViewModel> movFiles = new ObservableArrayList<>();

    private final CallActivity navi;

    public FavoriteSongViewModel(CallActivity navi) {
        this.navi = navi;
    }

    public void setChoiceType() {
        navi.callActivity();
    }

    public void getFileList(String fileSel) {
        String rootSD = "";
        File file;

        setChoiceType();
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

                String s = value.getPath();
                myList.setPath(s);
                myList.setFname(value.getName().substring(0, value.getName().indexOf(".db")));

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                myList.setSize(simpleDateFormat.format(value.lastModified()));
                movFiles.add(new DBFileViewModel(myList));
            }
            Collections.reverse(movFiles);
        }
    }

    public void onCommitClick() {
        navi.callDialog();
    }
}
