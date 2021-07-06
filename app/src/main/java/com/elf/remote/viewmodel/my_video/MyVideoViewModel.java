package com.elf.remote.viewmodel.my_video;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableField;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.arthenica.mobileffmpeg.FFprobe;
import com.arthenica.mobileffmpeg.MediaInformation;
import com.arthenica.mobileffmpeg.Statistics;
import com.elf.remote.Application;
import com.elf.remote.MyMovCall;
import com.elf.remote.viewmodel.BaseViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

public class MyVideoViewModel extends BaseObservable implements BaseViewModel {
    private final MyMovCall navi;

    private final ProgressDialog progressBar2;
    private final ProgressDialog progressBar;
    private final AlertDialog.Builder builder;
    SharedPreferences preferences;

    private Statistics statistics;

    String[] audName = {"samplesaxo1.mp3", "samplesaxo2.mp3", "samplesaxo3.mp3", "samplesaxo4.mp3"};
    String[] clipName = {"elf_nature1.mp4", "elf_nature2.mp4", "elf_nature3.mp4", "elf_nature4.mp4"};
    String movPath, audPath, singer;
    int duration = 0;
    String strPre;

    public final ObservableField<String> mov = new ObservableField<>();
    public final ObservableField<String> aud = new ObservableField<>();

    File file;

    protected static final Queue<Callable<Object>> actionQueue = new ConcurrentLinkedQueue<>();

    protected static final Handler handler = new Handler();

    public MyVideoViewModel(MyMovCall navi, ProgressDialog progressBar, ProgressDialog progressBar2, AlertDialog.Builder builder) {
        this.navi = navi;
        this.progressBar = progressBar;
        this.progressBar2 = progressBar2;
        this.builder = builder;
    }

    public void onMovListClick() {
        navi.callList();
    }

    public void onMovSelClick() {
        navi.callMovsel();
    }

    public void onAudSelClick() {
        navi.callAudsel();
    }

    public void onBaseDownload() {
        String url;
        String url2;
        url = "https://www.elf.co.kr/pds/choice/baseVideo/";
        url2 = "https://www.elf.co.kr/pds/choice/baseAudio/";

        String path;
        String path2;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            path = Application.applicationContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES).getAbsolutePath() + "/ElfData/baseVideo/elf_nature1.mp4";
            path2 = Application.applicationContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/ElfData/baseAudio/samplesaxo1.mp3";
        } else {
            path = Environment.getExternalStorageDirectory() + "/ElfData/baseVideo/elf_nature1.mp4";
            path2 = Environment.getExternalStorageDirectory() + "/ElfData/baseAudio/samplesaxo1.mp3";
        }

        File file = new File(path);
        File file2 = new File(path2);

        if (file.isFile() && file2.isFile()) {
            builder.setTitle("기본 영상 오디오 확인");
            builder.setMessage("기본 영상 오디오가 존재 합니다.\n그래도 다운로드 하시겠습니까?.");
        } else {
            builder.setTitle("기본 영상 오디오 다운로드");
            builder.setMessage("영상 사이즈가 큽니다.\n와이파이를 연결해 주세요.\n다운로드 하시겠습니까?");
        }
        builder.setNegativeButton("아니오",
                (dialog, which) -> {
                });
        builder.setPositiveButton("예",
                (dialog, which) -> dbDownLoad(url, url2));
        builder.setNeutralButton("",
                (dialog, which) -> {
                });
        builder.show();
    }

    public void onClipClick() {
        if (movPath != null && audPath != null) {
            String command, path, test, test2, img, mRoot, fRoot, name = null, saveName = null;
            int i = 1;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                test = Application.applicationContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES) + "/ElfData/MyVod (" + i + ").mp4";
                test2 = Application.applicationContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES) + "/ElfData/MyVod.mp4";
                img = Application.applicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/ElfData/logo/mainlogo.webp";
                mRoot = Application.applicationContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES).getAbsolutePath();
                fRoot = Application.applicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                name = "MyVod (" + i + ").mp4";
                saveName = "MyVod (" + i + ").mp4";

                Cursor cursor = Application.applicationContext().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
                cursor.moveToFirst();
                do {
                    String asd = cursor.getString(cursor.getColumnIndex("_data"));
                    if (asd.contains("MyVod")) {
                        i++;
                        test = mRoot + "/ElfData/MyVod (" + i + ").mp4";
                        name = "MyVod (" + i + ").mp4";
                        saveName = "MyVod (" + i + ").mp4";

                        File rf = new File(asd.substring(0, asd.indexOf("MyVod")) + name);
                        if (!rf.exists()) {
                            break;
                        }
                    }
                } while (cursor.moveToNext());

                cursor.close();
            } else {
                path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ElfData/";
                test = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ElfData/MyVod (" + i + ").mp4";
                test2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ElfData/MyVod.mp4";
                img = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ElfData/logo/mainlogo.webp";
                mRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
                fRoot = mRoot;

                File vodFile = new File(path);
                File[] list2 = vodFile.listFiles();
                while (true) {
                    if (list2 != null) {
                        for (File recList : list2) {
                            if (test.equals(recList.getAbsolutePath())) {
                                i++;
                                test = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ElfData/MyVod (" + i + ").mp4";
                            }
                        }
                    }
                    File rf = new File(test);
                    if (!rf.exists()) {
                        break;
                    }
                }
            }

            File test3 = getTFile();

            File file = new File(test2);

            long audMil = milsTime(audPath);
            long movMil = milsTime(movPath);

            if (audMil < movMil)
                command = "-y -i '" + movPath + "' -i '" + audPath + "' -c:v copy -c:a copy -strict experimental -map 0:v:0 -map 1:a:0 -shortest " + test2;
            else
                command = "-y -stream_loop -1 -i '" + movPath + "' -i '" + audPath + "' -c:v copy -c:a copy -strict experimental -map 0:v:0 -map 1:a:0 -shortest " + test2;

            String finalPath = test;
            String finalName = name;
            String finalSaveName = saveName;

            showProgressDialog();
            FFmpeg.execute(command);

            String base = null, width = null, height = null, rotate = "0", trans1 = "", trans2 = "";
            String kindPre = "KindTip";
            String nameSPre = "singer";
            String ounPre = "songBy";
            String colorPre = "color";
            String fontPre = "font";
            String str2 = preferences.getString(kindPre, "0");
            String str3 = preferences.getString(nameSPre, " ");
            String size = preferences.getString(ounPre, "50");
            String color = preferences.getString(colorPre, "black");
            String font = preferences.getString(fontPre, "nanum.ttf");

            String seg = "-i " + test2 + " -c copy -segment_time 5 -f segment " + Application.applicationContext().getFilesDir().getAbsolutePath() + "/deg%02d.mp4";
            FFmpeg.execute(seg);

            MediaInformation info = FFprobe.getMediaInformation(test2);
            JSONObject jsonObject = info.getAllProperties();
            try {
                JSONArray streams = jsonObject.getJSONArray("streams");
                JSONObject obj = streams.getJSONObject(0);
                base = obj.getString("time_base");
                base = base.substring(base.lastIndexOf("/") + 1);
                width = obj.getString("width");
                height = obj.getString("height");
                rotate = obj.getJSONObject("tags").getString("rotate");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            MediaInformation info2 = FFprobe.getMediaInformation(Application.applicationContext().getFilesDir().getAbsolutePath() + "/deg00.mp4");
            JSONObject jsonObject2 = info2.getAllProperties();
            try {
                String asd;
                JSONArray streams = jsonObject2.getJSONArray("streams");
                JSONObject obj = streams.getJSONObject(0);
                asd = obj.getString("duration");
                duration = Integer.parseInt(asd.substring(0, asd.length()-3).replace(".", ""));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (!rotate.equals("0")) {
                trans1 = "transpose=1,";
                trans2 = "transpose=2,";
                size = String.valueOf(Integer.parseInt(size) / 1.8);
            }

            if (str2.equals(strPre)) {
                singer = " ";
            } else {
                font = fRoot + "/ElfData/logo/" + font;
                singer = String.format("drawtext=fontfile=%s:text='Song by %s.':fontcolor=%s:fontsize=%s:x=(w-tw)/1.2:y=(h/1.4)+th:enable='between(t,0,5)' , ", font, str3, color, size);
            }

            String overlay = String.format("-y -noautorotate -i %s -i %s " +
                    "-filter_complex \"%s overlay=10:10:enable='between(t,0,5)', " +
                    "%s" +
                    "%s scale=%sx%s, setsar=sar=1/1, setdar=dar=16/9\" -video_track_timescale %s " +
                    "-pix_fmt yuv420p -c:v libx264 -c:a copy -preset ultrafast -crf 28 %s", Application.applicationContext().getFilesDir().getAbsolutePath() + "/deg00.mp4", img, trans1, singer, trans2, width, height, base, test3.getAbsolutePath());

            String finalRotate = rotate;
            FFmpeg.executeAsync(overlay, (executionId1, returnCode1) -> {
                progressBar2.setProgress(0);

                if (returnCode1 == RETURN_CODE_SUCCESS) {

                    String aa = String.format("-y -i %s -c copy -bsf:v h264_mp4toannexb -f mpegts %s", test3.getAbsolutePath(), Application.applicationContext().getFilesDir() + "/list2.ts");
                    FFmpeg.execute(aa);

                    mkTxtFile("file " + Application.applicationContext().getFilesDir() + "/list2.ts" + "\n");
                    listFile();

                    String bb = "-f concat -safe 0 -i " + getTxtFile().getAbsolutePath() + " -c copy -metadata:s:v rotate=-" + finalRotate + " " + "'" + finalPath + "'";
                    FFmpeg.execute(bb);

                    hideProgressDialog();

                    listDel();

                    file.delete();
                    getTFile().delete();
                    getTxtFile().delete();

                    File file1 = new File(finalPath);
                    this.file = file1;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        saveMakeVideoFile(finalName, finalSaveName);
                    } else {
                        Application.applicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file1)));
                    }

                    duration = 0;
                    shareDialog();
                }
            });
        }
    }

    @Override
    public void onCreate() {
        progressBar.setMessage("다운로드중...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setCancelable(false);
        progressBar.setIndeterminate(false);
        progressBar.setMax(100);

        waitForUIAction();
    }

    @Override
    public void onResume() {
        enableStatisticsCallback();
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(runnable);
    }

    public boolean preChk() {
        preferences = PreferenceManager.getDefaultSharedPreferences(Application.applicationContext());
        String namePre = "VideoTip";
        String str = preferences.getString(namePre, "0");

        strPre = "0";
        return str.equals(strPre);
    }

    Disposable backgroundtask;

    public void dbDownLoad(String... params) {
        progressBar.show();
        backgroundtask = Observable.fromCallable(() -> {
            String savePath2, savePath3;
            long len = -1;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                savePath2 = Application.applicationContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES).getPath();
                savePath3 = Application.applicationContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC).getPath();
            } else {
                savePath2 = Environment.getExternalStorageDirectory() + "/ElfData/baseVideo/";
                savePath3 = Environment.getExternalStorageDirectory() + "/ElfData/baseAudio/";
            }

            String fileUrl;
            String localPath;
            try {
                for (int i = 0; i < 8; i++) {
                    if (i < 4) {
                        localPath = savePath2 + "/" + clipName[i];
                        fileUrl = params[0] + clipName[i];
                    } else {
                        localPath = savePath3 + "/" + audName[i - 4];
                        fileUrl = params[1] + audName[i - 4];
                    }
                    URL imgUrl = new URL(fileUrl);
                    //서버와 접속하는 클라이언트 객체 생성
                    HttpURLConnection conn = (HttpURLConnection) imgUrl.openConnection();
                    len = conn.getContentLength();
                    byte[] tmpByte = new byte[(int) len];
                    //입력 스트림을 구한다
                    InputStream is = conn.getInputStream();
                    File file = new File(localPath);
                    //파일 저장 스트림 생성
                    FileOutputStream fos = new FileOutputStream(file);
                    int read;
                    //입력 스트림을 파일로 저장
                    long downloadedSize = 0;
                    for (; ; ) {
                        read = is.read(tmpByte);
                        if (read <= 0) {
                            break;
                        }
                        downloadedSize += read;
                        if (len > 0) {
                            progressBar.setProgress((int) ((downloadedSize * 100) / len));
                        }
                        fos.write(tmpByte, 0, read); //file 생성
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        if (i < 4) {
                            saveVideoFile(clipName[i]);
                        } else {
                            saveAudioFile(audName[i - 4]);
                        }
                        file.delete();
                    } else {
                        Application.applicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                    }

                    is.close();
                    fos.close();
                    conn.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return len;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                    progressBar.dismiss();
                    backgroundtask.dispose();
                });
    }

    private void saveMakeVideoFile(String name, String saveName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Video.Media.TITLE, name);
            values.put(MediaStore.Video.Media.DISPLAY_NAME, name);
            values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            values.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
            values.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/Elf/ElfClip");
            values.put(MediaStore.Video.Media.IS_PENDING, 1);

            ContentResolver contentResolver = Application.applicationContext().getContentResolver();
            Uri collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            Uri item = contentResolver.insert(collection, values);

            try {
                ParcelFileDescriptor pdf = contentResolver.openFileDescriptor(item, "w", null);

                if (pdf == null) {
                    Log.d("asdf", "null");
                } else {
                    FileOutputStream fos = new FileOutputStream(pdf.getFileDescriptor());

                    String aa = Application.applicationContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES) + "/ElfData";
                    File imageFile = new File(aa + "/" + saveName);

                    FileInputStream in = new FileInputStream(imageFile);

                    byte[] buf = new byte[8192];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        fos.write(buf, 0, len);
                    }

                    in.close();
                    fos.close();
                    pdf.close();

                    values.clear();
                    values.put(MediaStore.Video.Media.IS_PENDING, 0);
                    contentResolver.update(item, values, null, null);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveVideoFile(String name) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Video.Media.TITLE, name);
            values.put(MediaStore.Video.Media.DISPLAY_NAME, name);
            values.put(MediaStore.Video.Media.MIME_TYPE, "video/*");
            values.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
            values.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/baseVideo");
            values.put(MediaStore.Video.Media.IS_PENDING, 1);

            ContentResolver contentResolver = Application.applicationContext().getContentResolver();
            Uri collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            Uri item = contentResolver.insert(collection, values);

            Cursor video_c = Application.applicationContext().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
            int vidid_index = video_c.getColumnIndex(MediaStore.Video.Media._ID);
            int viddata_index = video_c.getColumnIndex(MediaStore.Video.Media.TITLE);

            if (video_c.moveToFirst()) {
                do {
                    long rowId = video_c.getLong(vidid_index);
                    String fullPathString = video_c.getString(viddata_index);
                    if (fullPathString.equals(name.substring(0, name.indexOf('.')))) {
                        Uri uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, rowId);
                        Application.applicationContext().getContentResolver().delete(uri, null, null);
                    }
                } while (video_c.moveToNext());
            }
            video_c.close();

            try {
                ParcelFileDescriptor pdf = contentResolver.openFileDescriptor(item, "w", null);

                if (pdf == null) {
                    Log.d("asdf", "null");
                } else {
                    FileOutputStream fos = new FileOutputStream(pdf.getFileDescriptor());

                    File storageDir = new File(String.valueOf(Application.applicationContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES)));
                    File imageFile = new File(storageDir, name);

                    FileInputStream in = new FileInputStream(imageFile);

                    byte[] buf = new byte[8192];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        fos.write(buf, 0, len);
                    }

                    in.close();
                    fos.close();
                    pdf.close();

                    values.clear();
                    values.put(MediaStore.Video.Media.IS_PENDING, 0);
                    contentResolver.update(item, values, null, null);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveAudioFile(String name) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Audio.Media.TITLE, name);
            values.put(MediaStore.Audio.Media.DISPLAY_NAME, name);
            values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/*");
            values.put(MediaStore.Audio.Media.DATE_ADDED, System.currentTimeMillis() / 1000);

            values.put(MediaStore.Audio.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Audio.Media.RELATIVE_PATH, "Music/baseAudio");
            values.put(MediaStore.Audio.Media.IS_PENDING, 1);


            ContentResolver contentResolver = Application.applicationContext().getContentResolver();
            Uri collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            Uri item = contentResolver.insert(collection, values);

            Cursor Audio_c = Application.applicationContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
            int aud_index = Audio_c.getColumnIndex(MediaStore.Audio.Media._ID);
            int audd_index = Audio_c.getColumnIndex(MediaStore.Audio.Media.TITLE);

            if (Audio_c.moveToFirst()) {
                do {
                    long rowId = Audio_c.getLong(aud_index);
                    String fullPathString = Audio_c.getString(audd_index);
                    if (fullPathString.equals(name.substring(0, name.indexOf('.')))) {
                        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, rowId);
                        Application.applicationContext().getContentResolver().delete(uri, null, null);
                    }
                } while (Audio_c.moveToNext());
                Audio_c.close();
            }


            try {
                ParcelFileDescriptor pdf = contentResolver.openFileDescriptor(item, "w", null);

                if (pdf == null) {
                    Log.d("asdf", "null");
                } else {
                    FileOutputStream fos = new FileOutputStream(pdf.getFileDescriptor());

                    File storageDir = new File(String.valueOf(Application.applicationContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC)));
                    File imageFile = new File(storageDir, name);

                    FileInputStream in = new FileInputStream(imageFile);

                    byte[] buf = new byte[8192];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        fos.write(buf, 0, len);
                    }

                    in.close();
                    fos.close();
                    pdf.close();

                    values.clear();
                    values.put(MediaStore.Audio.Media.IS_PENDING, 0);
                    contentResolver.update(item, values, null, null);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    long milsTime(String path) {
        MediaMetadataRetriever m = new MediaMetadataRetriever();

        m.setDataSource(path);

        String s = m.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        return Long.parseLong(s);
    }

    public File getTxtFile() {
        return new File(Application.applicationContext().getFilesDir(), "list2.txt");
    }

    public File getTFile() {
        return new File(Application.applicationContext().getFilesDir(), "list2.mp4");
    }

    public void mkTxtFile(String contents) {
        try {
            File dir = getTxtFile();

            FileOutputStream fos = new FileOutputStream(dir.getAbsolutePath(), true);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write(contents);
            writer.flush();

            writer.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listFile() {
        File[] segf = Application.applicationContext().getFilesDir().listFiles();
        String j = "01";
        int k = 1;
        if (segf != null) {
            for (File recList : segf) {
                String asd = Application.applicationContext().getFilesDir().getAbsolutePath() + "/deg" + j + ".mp4";
                String asd2 = Application.applicationContext().getFilesDir().getAbsolutePath() + "/deg" + j + ".ts";
                if (asd.equals(recList.getAbsolutePath())) {
                    String aa = String.format("-y -i %s -c copy -bsf:v h264_mp4toannexb -f mpegts %s", asd, asd2);
                    FFmpeg.execute(aa);

                    mkTxtFile("file " + asd2 + "\n");
                    k++;
                    j = String.format(Locale.getDefault(), "%02d", k);
                }
            }
        }
    }

    public void listDel() {
        File[] segf = Application.applicationContext().getFilesDir().listFiles();
        String j = "00";
        int k = 0;
        if (segf != null) {
            for (File recList : segf) {
                String asd = Application.applicationContext().getFilesDir().getAbsolutePath() + "/deg" + j + ".mp4";
                String asd2 = Application.applicationContext().getFilesDir().getAbsolutePath() + "/deg" + j + ".ts";
                if (asd.equals(recList.getAbsolutePath())) {
                    recList.delete();
                    k++;
                    j = String.format(Locale.getDefault(), "%02d", k);

                    File file = new File(asd2);
                    file.delete();
                }
            }
        }
    }

    void shareDialog() {
        builder.setTitle("제작 완료");
        builder.setMessage("ElfData폴더에 저장되었습니다.\n영상을 바로 확인하시겠습니까?.");
        builder.setNegativeButton("취소",
                (dialog, which) -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) file.delete();
                });
        builder.setPositiveButton("영상확인",
                (dialog, which) -> navi.callBase());
        builder.setNeutralButton("공유하기",
                (dialog, which) -> navi.callClipmk());
        builder.show();
    }

    public String VideoPath() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            File file = null;
            Cursor cursor = Application.applicationContext().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
            cursor.moveToFirst();
            do {
                String path2 = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                if (path2.equals(this.file.getName().substring(0, this.file.getName().indexOf(".")))) {
                    String path = cursor.getString(cursor.getColumnIndex("_data"));
                    file = new File(path);
                }
            } while (cursor.moveToNext());

            cursor.close();

            this.file.delete();

            if (file != null) {
                return file.getAbsolutePath();
            } else {
                return null;
            }
        } else {
            return file.getAbsolutePath();
        }
    }

    public Intent shareMyVideo() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        Uri screenshotUri = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            File file = null;
            Cursor cursor = Application.applicationContext().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
            cursor.moveToFirst();
            do {
                String path2 = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                if (path2.equals(this.file.getName().substring(0, this.file.getName().indexOf(".")))) {
                    String path = cursor.getString(cursor.getColumnIndex("_data"));
                    file = new File(path);
                }
            } while (cursor.moveToNext());

            cursor.close();

            this.file.delete();
            if (file != null) {
                screenshotUri = FileProvider.getUriForFile(Application.applicationContext(), Application.applicationContext().getPackageName() + ".fileprovider", file);
            }
        } else {
            screenshotUri = FileProvider.getUriForFile(Application.applicationContext(), Application.applicationContext().getPackageName() + ".fileprovider", file);
        }

        sharingIntent.setType("video/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
        return sharingIntent;
    }

    protected static final Runnable runnable = new Runnable() {

        @Override
        public void run() {
            Callable<Object> callable;

            do {
                callable = actionQueue.poll();
                if (callable != null) {
                    try {
                        callable.call();
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                }
            } while (callable != null);

            handler.postDelayed(this, 10);
        }
    };

    public static void waitForUIAction() {
        handler.postDelayed(runnable, 10);
    }

    public static void addUIAction(final Callable<Object> callable) {
        actionQueue.add(callable);
    }

    public void enableStatisticsCallback() {
        Config.enableStatisticsCallback(newStatistics -> addUIAction(() -> {
            statistics = newStatistics;
            updateProgressDialog();
            return null;
        }));
    }

    protected void showProgressDialog() {
        progressBar2.setMessage("영상 생성중...");
        progressBar2.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar2.setIndeterminate(true);
        progressBar2.setCancelable(false);

        Config.resetStatistics();

        progressBar2.show();
    }

    protected void hideProgressDialog() {
        progressBar2.dismiss();
    }

    protected void updateProgressDialog() {
        if (statistics == null) {
            Log.d("progress", "statistics");
            return;
        }

        if (duration != 0) {
            int timeInMilliseconds = this.statistics.getTime();
            int totalVideoDuration = duration;

            if (timeInMilliseconds < duration) {
                String completePercentage = new BigDecimal(timeInMilliseconds).multiply(new BigDecimal(100)).divide(new BigDecimal(totalVideoDuration), 0, BigDecimal.ROUND_HALF_UP).toString();
                progressBar2.setMax(100);
                progressBar2.setProgress(Integer.parseInt(completePercentage));
                progressBar2.setCancelable(false);
                progressBar2.setIndeterminate(false);
            } else {
                progressBar2.setProgress(100);
            }
        }
    }

    public void setVideoName(String name, String path) {
        movPath = path;
        mov.set(name);
    }

    public void setAudioName(String name, String path) {
        audPath = path;
        aud.set(name);
    }
}
