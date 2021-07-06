package com.elf.remote.viewmodel.my_video;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;

import com.elf.mvvmremote.R;
import com.elf.remote.Application;
import com.elf.remote.ListEdit;
import com.elf.remote.model.data.MovFile;
import com.elf.remote.utils.RecycleUtils;
import com.elf.remote.viewmodel.BaseViewModel;
import com.elf.remote.viewmodel.MovFileViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class MyAudMovViewModel extends BaseObservable implements BaseViewModel {
    public final ObservableArrayList<MovFileViewModel> AudMovFiles = new ObservableArrayList<>();
    public ArrayList<MovFile> fileData;
    public final ObservableField<String> location = new ObservableField<>();
    private final ListEdit navi;
    private String nextPath = "";
    private String prevPath = "";
    private String currentPath = "";
    String rootSD = "";

    public MyAudMovViewModel(ListEdit navi) {
        this.navi = navi;
    }

    public void onExitClick() {
        navi.exitList();
    }

    public void onSelClick() {
        navi.shareList();
    }

    public void onEditClick() {
        navi.selList();
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

    public void getFileList(String fileSel, String filePath, int jong) {
        fileData = new ArrayList<>();
        Bitmap bit;
        String what, loc;
        File file;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Uri uri;
            if (jong == 2) {
                what = "baseVideo";
                uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            } else if (jong == 3) {
                what = "baseAudio";
                uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            } else {
                what = "Movies";
                uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            }
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
            if (file.getAbsolutePath().equals(" /")) {
                String root = Environment.getExternalStorageDirectory().getAbsolutePath();
                String asd = file.getAbsolutePath();
                loc = file.getAbsolutePath();
                loc = loc.substring(root.length(), asd.length());
            } else {
                if (jong == 2) loc = "Movies/baseVideo";
                else if (jong == 3) loc = "Movies/basAudio";
                else loc = "Movies/Elf/ElfClip";
            }
        } else {
            rootSD = Environment.getExternalStorageDirectory().getAbsolutePath();
            file = new File(rootSD + filePath);
            rootSD = file.getAbsolutePath();
            loc = filePath;
        }

        File[] list = file.listFiles();

        location.set("Location : " + loc);

        if (jong == 1 || jong == 3 && !rootSD.equals("")) {
            MovFile sample = new MovFile();
            bit = BitmapFactory.decodeResource(Application.applicationContext().getResources(), R.drawable.va_folder);
            sample.setPath("..");
            sample.setFname("../");
            sample.setSize("");
            sample.setPoster(bit);

            fileData.add(sample);
            AudMovFiles.add(new MovFileViewModel(sample));
        }

        if (list == null) {
            Log.d("null", "null");
        } else {
            for (File value : list) {
                if (value.getName().contains("pending") || value.getName().contains("'") || value.getName().contains("/") || value.getName().contains("\"")) continue;
                MovFile myList = new MovFile();
                if ((jong == 3 || jong == 1) && value.isDirectory()) {
                    String s = value.getPath();
                    bit = BitmapFactory.decodeResource(Application.applicationContext().getResources(), R.drawable.va_folder);
                    myList.setPath(s);
                    myList.setFname(value.getName());
                    myList.setSize("");
                    myList.setPoster(bit);

                    fileData.add(myList);
                    AudMovFiles.add(new MovFileViewModel(myList));
                }
                if (value.getName().endsWith(fileSel)) {
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
                    String size = time + String.format(Locale.getDefault(), "%.2f", a) + "MB";

                    if (jong == 3)
                        bit = BitmapFactory.decodeResource(Application.applicationContext().getResources(), R.drawable.va_music);
                    else
                        bit = RecycleUtils.getThumbnail(value.getPath(), Application.applicationContext());

                    myList.setFname(value.getName());
                    myList.setSize(size);
                    myList.setPoster(bit);

                    fileData.add(myList);
                    AudMovFiles.add(new MovFileViewModel(myList));
                }
            }
        }
    }

    public void nextPath(String str, int jong, String fileSel) {
        currentPath = rootSD;
        Bitmap bit;
        prevPath = currentPath;

        // 현재 경로에서 / 와 다음 경로 붙이기
        nextPath = str;
        File file = new File(nextPath);
        if (!file.isDirectory()) {
            return;
        } else {
            rootSD = nextPath;
        }
        File[] list = file.listFiles();

        fileData.clear();
        AudMovFiles.clear();
        MovFile sample = new MovFile();
        bit = BitmapFactory.decodeResource(Application.applicationContext().getResources(), R.drawable.va_folder);
        sample.setPath("..");
        sample.setFname("../");
        sample.setSize("");
        sample.setPoster(bit);

        fileData.add(sample);
        AudMovFiles.add(new MovFileViewModel(sample));

        if (list != null) {
            for (File value : list) {
                if (value.getName().contains("pending") || value.getName().contains("'") || value.getName().contains("/") || value.getName().contains("\"")) continue;
                MovFile myList = new MovFile();
                if ((jong == 3 || jong == 1) && value.isDirectory()) {
                    String s = value.getPath();
                    bit = BitmapFactory.decodeResource(Application.applicationContext().getResources(), R.drawable.va_folder);
                    myList.setPath(s);
                    myList.setFname(value.getName());
                    myList.setSize("");
                    myList.setPoster(bit);

                    fileData.add(myList);
                    AudMovFiles.add(new MovFileViewModel(myList));
                }
                if (value.getName().endsWith(fileSel)) {
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
                    String size = time + String.format(Locale.getDefault(), "%.2f", a) + "MB";

                    if (jong == 3)
                        bit = BitmapFactory.decodeResource(Application.applicationContext().getResources(), R.drawable.va_music);
                    else
                        bit = RecycleUtils.getThumbnail(value.getPath(), Application.applicationContext());

                    myList.setFname(value.getName());
                    myList.setSize(size);
                    myList.setPoster(bit);

                    fileData.add(myList);
                    AudMovFiles.add(new MovFileViewModel(myList));
                }
            }
        }

        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        location.set("Location : " + nextPath.substring(root.length(), rootSD.length()));
    }

    public void prevPath(int jong, String fileSel) {
        currentPath = rootSD;
        Bitmap bit;
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        nextPath = currentPath;
        prevPath = currentPath;

        // 마지막 / 의 위치 찾기
        int lastSlashPosition = prevPath.lastIndexOf("/");

        // 처음부터 마지막 / 까지의 문자열 가져오기
        prevPath = prevPath.substring(0, lastSlashPosition);
        File file = new File(prevPath);

        if (!file.isDirectory()) {
            return;
        } else {
            rootSD = prevPath;
        }

        File[] list = file.listFiles();
        fileData.clear();
        AudMovFiles.clear();
        if (!prevPath.equals(rootPath)) {
            MovFile sample = new MovFile();
            bit = BitmapFactory.decodeResource(Application.applicationContext().getResources(), R.drawable.va_folder);
            sample.setPath("..");
            sample.setFname("../");
            sample.setSize("");
            sample.setPoster(bit);

            fileData.add(sample);
            AudMovFiles.add(new MovFileViewModel(sample));
        }

        if (list != null) {
            for (File value : list) {
                if (value.getName().contains("pending") || value.getName().contains("'") || value.getName().contains("/") || value.getName().contains("\"")) continue;
                MovFile myList = new MovFile();
                if ((jong == 3 || jong == 1) && value.isDirectory()) {
                    String s = value.getPath();
                    bit = BitmapFactory.decodeResource(Application.applicationContext().getResources(), R.drawable.va_folder);
                    myList.setPath(s);
                    myList.setFname(value.getName());
                    myList.setSize("");
                    myList.setPoster(bit);

                    fileData.add(myList);
                    AudMovFiles.add(new MovFileViewModel(myList));
                }
                if (value.getName().endsWith(fileSel)) {
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
                    String size = time + String.format(Locale.getDefault(), "%.2f", a) + "MB";

                    if (jong == 3)
                        bit = BitmapFactory.decodeResource(Application.applicationContext().getResources(), R.drawable.va_music);
                    else
                        bit = RecycleUtils.getThumbnail(value.getPath(), Application.applicationContext());

                    myList.setFname(value.getName());
                    myList.setSize(size);
                    myList.setPoster(bit);

                    fileData.add(myList);
                    AudMovFiles.add(new MovFileViewModel(myList));
                }
            }
        }
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        location.set("Location : " + prevPath.substring(root.length(), rootSD.length()));
    }
}
