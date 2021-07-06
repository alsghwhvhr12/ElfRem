package com.elf.remote.viewmodel.my_video;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;

import com.elf.remote.Application;
import com.elf.remote.ListEdit;
import com.elf.remote.model.data.MovFile;
import com.elf.remote.utils.RecycleUtils;
import com.elf.remote.viewmodel.BaseViewModel;
import com.elf.remote.viewmodel.MovFileViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class MyMovListViewModel extends BaseObservable implements BaseViewModel {
    public final ObservableArrayList<MovFileViewModel> movFiles = new ObservableArrayList<>();
    public final ObservableField<String> location = new ObservableField<>();
    private final ListEdit navi;
    public ArrayList<MovFile> fileData;

    public MyMovListViewModel(ListEdit navi) {
        this.navi = navi;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onDestroy() {
    }

    public void onExitClick() {
        navi.exitList();
    }

    public void onDelClick() {
        navi.dleList();
    }

    public void onEditClick() {
        navi.editList();
    }

    public void onShareClick() {
        navi.shareList();
    }

    public void onPrvClick() {
        navi.selList();
    }

    public void getFileList(String fileSel) {
        fileData = new ArrayList<>();
        String rootSD = "", what, loc;
        File file = null;
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            what = "ElfClip";
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

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

            if (rootSD != null) {
                file = new File(rootSD);
            }
            loc = "/Movies/Elf/ElfClip";
        } else {
            rootSD = Environment.getExternalStorageDirectory().getAbsolutePath();
            file = new File(rootSD + "/ElfData");
            loc = "/ElfData";
        }

        location.set("Location : " + loc);


        File[] list = new File[0];
        if (file != null) {
            list = file.listFiles((file1, s) -> s.endsWith(fileSel));
        }
        fileData = new ArrayList<>();

        if (list == null) {
            Log.d("null", "null");
        } else {
            for (File value : list) {
                if (value.getName().contains("pending") || value.getName().contains("'") || value.getName().contains("/") || value.getName().contains("\"")) continue;

                MovFile myList = new MovFile();

                String s = value.getPath();
                myList.setPath(s);
                MediaMetadataRetriever r = new MediaMetadataRetriever();
                r.setDataSource(s);
                s = r.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                long mils = Long.parseLong(s);
                long dur = mils / 1000;
                long hour = dur / 3600;
                long min = (dur - hour * 3600) / 60;
                long sec = dur - (hour * 3600 + min * 60);

                String sMin = String.valueOf(min);
                String sSec = String.valueOf(sec);
                if (sec < 10) sSec = "0" + sec;
                if (min < 10) sMin = "0" + min;

                String time = sMin + ":" + sSec + " | ";

                double a = value.length() / 1024.0 / 1024.0;
                Bitmap bitmap = RecycleUtils.getThumbnail(value.getPath(), Application.applicationContext());

                String size = time + String.format(Locale.getDefault(), "%.2f", a) + "MB";

                myList.setFname(value.getName());
                myList.setSize(size);
                myList.setPoster(bitmap);

                fileData.add(myList);
                movFiles.add(new MovFileViewModel(myList));
            }
            Collections.reverse(fileData);
            Collections.reverse(movFiles);
        }
    }
}
