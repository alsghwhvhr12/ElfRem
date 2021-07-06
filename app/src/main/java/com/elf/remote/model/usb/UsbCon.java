package com.elf.remote.model.usb;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.DialogFragment;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.arthenica.mobileffmpeg.FFprobe;
import com.arthenica.mobileffmpeg.MediaInformation;
import com.elf.remote.Application;
import com.elf.remote.model.data.DataAdapter;
import com.elf.remote.view.my_recorded.CustomDialog;
import com.elf.remote.view.my_recorded.MyRecorded;
import com.elf.remote.view.my_recorded.UsbPerFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

public class UsbCon {
    private final Context mContext;
    DialogFragment df;
    String mov, title;
    public IntentFilter filter;
    ProgressDialog progressBar2;
    MyRecorded videoFragment;
    AlertDialog.Builder builder;
    UsbDataBase usbDataBase;
    ArrayList<usbFile> usbFiles;
    public UsbDevice device;
    public AlertDialog.Builder usbBuilder;
    public AlertDialog alertDialog;

    SharedPreferences preferences;
    String kindPre = "KindTip";
    String nameSPre = "singer";
    String jaePre = "title", ounPre = "songBy";
    String colorPre = "color";
    String fontPre = "font";
    String strPre = "0";
    String str2, str3, size1, size2, color, font, audio, over, test2, recL;

    private final byte[] bytes = new byte[1024];

    public UsbCon(Context mContext, IntentFilter filter, DialogFragment df, String mov, MyRecorded videoFragment) {
        this.mContext = mContext;
        this.filter = filter;
        this.filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        this.filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        this.df = df;
        this.mov = mov;
        this.videoFragment = videoFragment;
        this.builder = new AlertDialog.Builder(mContext);
        this.usbBuilder = new AlertDialog.Builder(mContext);
        usbDataBase = UsbDataBase.getInstance(mContext);
    }

    public BroadcastReceiver mUsbDeviceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (df.getShowsDialog()) {
                String action = intent.getAction();

                switch (action) {
                    case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                        device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                        dbDownLoad();
                        break;
                    case UsbManager.ACTION_USB_DEVICE_DETACHED:
                        df.dismiss();
                        mContext.unregisterReceiver(mUsbDeviceReceiver);
                        break;
                }
            }
        }
    };

    public void UsbAttached() {
        usbBuilder.setMessage("USB 연결을 확인중입니다.");
        usbBuilder.setCancelable(false);
        alertDialog = usbBuilder.create();
        alertDialog.show();
        if (device != null) df.dismiss();
    }

    protected void AttachedAction() {
        builder.setMessage("USB가 연결되었습니다.\n영상을 생성하시겠습니까?");
        builder.setNegativeButton("아니오",
                (dialog, which) -> {
                    //mContext.unregisterReceiver(mUsbDeviceReceiver);
                    videoFragment.startPreview();
                });
        builder.setPositiveButton("예",
                (dialog, which) -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        makeMovieAfterR();
                    } else {
                        makeMovieBeforeR();
                    }
                });
        builder.show();
    }

    public void AttachedCType() {
        builder.setMessage("USB가 연결되었습니다.\n영상을 생성하시겠습니까?");
        builder.setNegativeButton("아니오",
                (dialog, which) -> videoFragment.startPreview());
        builder.setPositiveButton("예",
                (dialog, which) -> ffmpegCType());
        builder.show();
    }

    protected void makeMovieBeforeR() {
        int i = 1;

        getPreference();

        audio = "";
        over = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ElfData/logo/mainlogo.webp";
        test2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ElfData/mm.mp4";
        recL = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ElfData/MyRec-" + i + ".mp4";
        font = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ElfData/logo/" + font;

        File file = new File("/storage");
        File[] list = file.listFiles();

        if (list != null) {
            for (File ll : list) {
                File f = new File(ll + "/TEMP/TEMP_REC.TMP");
                File s = new File(ll + "/TEMP/TEMP_REC_INFO.TMP");

                if (f.exists() && s.exists()) {
                    audio = f.getAbsolutePath();
                    String read = ReadTextFile(s.getAbsolutePath());
                    if (initLoadDB(read)) {
                        AlertDialog.Builder builder;
                        builder = new AlertDialog.Builder(mContext);
                        builder.setCancelable(false);
                        builder.setMessage("곡을 선곡하여 녹음 후 진행해주세요.");
                        builder.setNegativeButton("확인",
                                (dialog, which) -> videoFragment.startPreview());
                        builder.show();
                        return;
                    }
                } else {
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(mContext);
                    builder.setCancelable(false);
                    builder.setMessage("USB 장치 인식 실패 혹은 파일이 없습니다. 다시 시도해주세요! \n(계속 영상합성이 실패할 경우 녹화 종료 후 다시 시도 해주세요.)");
                    builder.setPositiveButton("취소",
                            (dialog, which) -> videoFragment.finish());
                    builder.setNegativeButton("다시시도",
                            (dialog, which) -> makeMovieBeforeR());
                    builder.show();
                    return;
                }
            }
        }
        ffmpegEx();
    }

    protected void makeMovieAfterR() {
        int i = 1;

        getPreference();

        audio = "";
        test2 = Application.applicationContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES) + "/ElfData/mm.mp4";
        recL = Application.applicationContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES) + "/ElfData/MyRec-" + i + ".mp4";
        over = Application.applicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/ElfData/logo/mainlogo.webp";
        font = Application.applicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/ElfData/logo/" + font;

        usbFiles = getUsbData();
        if (usbFiles != null) {
            for (usbFile aa : usbFiles) {
                if (aa.usbName == device.getProductId()) {
                    DocumentFile file = DocumentFile.fromTreeUri(mContext, Uri.parse(aa.usbUri));
                    DocumentFile[] file2 = new DocumentFile[0];
                    if (file != null) {
                        file2 = file.listFiles();
                    }

                    for (DocumentFile ff : file2) {
                        try {
                            File files = new File(Application.applicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/ElfData/" + ff.getName());
                            InputStream is = Application.applicationContext().getContentResolver().openInputStream(ff.getUri());
                            FileOutputStream outStream = new FileOutputStream(files);
                            int read;
                            while ((read = is.read(bytes)) != -1) {
                                outStream.write(bytes, 0, read);
                            }
                            is.close();
                            outStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } else {
            Toast.makeText(mContext, "USB 장치 인식 실패! 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
        }

        File f = new File(Application.applicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/ElfData/TEMP_REC.TMP");
        File s = new File(Application.applicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/ElfData/TEMP_REC_INFO.TMP");
        if (f.exists() && s.exists()) {
            audio = f.getAbsolutePath();
            String read = ReadTextFile(s.getAbsolutePath());
            if (initLoadDB(read)) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(mContext);
                builder.setCancelable(false);
                builder.setMessage("곡을 선곡하여 녹음 후 진행해주세요.");
                builder.setNegativeButton("확인",
                        (dialog, which) -> videoFragment.startPreview());
                builder.show();
                return;
            }
        } else {
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(mContext);
            builder.setCancelable(false);
            builder.setMessage("USB 장치 인식 실패 혹은 파일이 없습니다. 다시 시도해주세요! \n(계속 영상합성이 실패할 경우 녹화 종료 후 다시 시도 해주세요.)");
            builder.setPositiveButton("취소",
                    (dialog, which) -> videoFragment.finish());
            builder.setNegativeButton("다시시도",
                    (dialog, which) -> makeMovieAfterR());
            builder.show();
            return;
        }

        ffmpegEx();
    }

    protected void getPreference() {
        preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        str2 = preferences.getString(kindPre, "0");
        str3 = preferences.getString(nameSPre, " ");
        size1 = preferences.getString(jaePre, "50");
        size2 = preferences.getString(ounPre, "50");
        color = preferences.getString(colorPre, "black");
        font = preferences.getString(fontPre, "nanum.ttf");
    }

    protected void ffmpegEx() {
        File mm = new File(test2);
        File movie = new File(mov);
        showProgressDialog();

        String command = "-y -i " + mov + " -i " + audio + " -c:v copy -c:a aac -strict experimental -map 0:v:0 -map 1:a:0 " + test2;

        FFmpeg.executeAsync(command, (executionId, returnCode) -> {
            String base = null, width = null, height = null, rotate = "0", trans1 = "", trans2 = "", singer;

            movie.delete();

            if (returnCode == RETURN_CODE_SUCCESS) {

                String seg = "-i " + test2 + " -c copy -segment_time 5 -f segment " + mContext.getFilesDir().getAbsolutePath() + "/deg%02d.mp4";
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

                if (!rotate.equals("0")) {
                    if (MyRecorded.mCamId.equals("0")) trans1 = "transpose=1,";
                    else trans1 = "transpose=0, transpose=3, transpose=1,";
                    if (MyRecorded.mCamId.equals("0")) trans2 = "transpose=2,";
                    else trans2 = "transpose=1,";
                    size1 = String.valueOf(Integer.parseInt(size1) / 1.8);
                    size2 = String.valueOf(Integer.parseInt(size2) / 1.8);
                }

                if (str2.equals(strPre)) {
                    singer = " ";
                } else {
                    singer = String.format("drawtext=fontfile=%s:text='Song by %s.':fontcolor=%s:fontsize=%s:x=(w-tw)/1.2:y=(h/1.4)+th:enable='between(t,0,5)', ", font, str3, color, size2);
                }

                String overlay = String.format("-y -noautorotate -i %s -i %s " +
                        "-filter_complex \"%s overlay=10:10:enable='between(t,0,5)', " +
                        "drawtext=fontfile=%s:text='%s':fontcolor=%s:fontsize=%s:x=(w-tw)/2:y=(h/2)-th:enable='between(t,0,5)', " +
                        "%s " +
                        "%s scale=%sx%s, setsar=sar=1/1, setdar=dar=16/9\" -video_track_timescale %s " +
                        "-pix_fmt yuv420p -c:v libx264 -c:a copy -preset ultrafast -crf 28 %s", mContext.getFilesDir().getAbsolutePath() + "/deg00.mp4", over, trans1, font, title, color, size1, singer, trans2, width, height, base, getTFile().getAbsolutePath());

                String finalRotate = rotate;
                FFmpeg.executeAsync(overlay, (executionId1, returnCode1) -> {
                    String aa = String.format("-y -i %s -c copy -bsf:v h264_mp4toannexb -f mpegts %s", getTFile().getAbsolutePath(), mContext.getFilesDir() + "/list2.ts");
                    FFmpeg.execute(aa);

                    mkTxtFile("file " + mContext.getFilesDir() + "/list2.ts" + "\n");
                    listFile();

                    String bb;
                    if (MyRecorded.mCamId.equals("0"))
                        bb = "-f concat -safe 0 -i " + getTxtFile().getAbsolutePath() + " -c copy -bsf:a aac_adtstoasc -metadata:s:v rotate=-" + finalRotate + " " + recL;
                    else
                        bb = "-f concat -safe 0 -i " + getTxtFile().getAbsolutePath() + " -c copy -bsf:a aac_adtstoasc -metadata:s:v rotate=" + finalRotate + " " + recL;
                    FFmpeg.execute(bb);

                    listDel();

                    getTxtFile().delete();
                    getTFile().delete();
                    mm.delete();
                    hideProgressDialog();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        File f = new File(Application.applicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/ElfData/TEMP_REC.TMP");
                        File s = new File(Application.applicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/ElfData/TEMP_REC_INFO.TMP");
                        f.delete();
                        s.delete();
                    }
                    CustomDialog customDialog = new CustomDialog(mContext);
                    customDialog.callFunction(recL, title, videoFragment);
                    device = null;
                });
            }
        });
    }

    protected void ffmpegCType() {
        getPreference();
        String base = null, width = null, height = null, rotate = "0", trans1 = "", trans2 = "", singer;
        File movie = new File(mov);
        showProgressDialog();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            over = Application.applicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/ElfData/logo/mainlogo.webp";
            recL = Application.applicationContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES) + "/ElfData/MyRec-1.mp4";
            font = Application.applicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/ElfData/logo/" + font;
        } else {
            over = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ElfData/logo/mainlogo.webp";
            recL = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ElfData/MyRec-1.mp4";
            font = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ElfData/logo/" + font;
        }

        String seg = "-i " + mov + " -c copy -segment_time 5 -f segment " + mContext.getFilesDir().getAbsolutePath() + "/deg%02d.mp4";
        FFmpeg.execute(seg);

        MediaInformation info = FFprobe.getMediaInformation(mov);
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

        if (!rotate.equals("0")) {
            if (MyRecorded.mCamId.equals("0")) trans1 = "transpose=1,";
            else trans1 = "transpose=0, transpose=3, transpose=1,";
            if (MyRecorded.mCamId.equals("0")) trans2 = "transpose=2,";
            else trans2 = "transpose=1,";
            size1 = String.valueOf(Integer.parseInt(size1) / 1.8);
            size2 = String.valueOf(Integer.parseInt(size2) / 1.8);
        }

        if (str2.equals(strPre)) {
            singer = " ";
        } else {
            singer = String.format("drawtext=fontfile=%s:text='Song by %s.':fontcolor=%s:fontsize=%s:x=(w-tw)/1.2:y=(h/1.4)+th:enable='between(t,0,5)', ", font, str3, color, size2);
        }

        String overlay = String.format("-y -noautorotate -i %s -i %s " +
                "-filter_complex \"%s overlay=10:10:enable='between(t,0,5)', " +
                "%s " +
                "%s scale=%sx%s, setsar=sar=1/1, setdar=dar=16/9\" -video_track_timescale %s " +
                "-pix_fmt yuv420p -c:v libx264 -c:a copy -preset ultrafast -crf 28 %s", mContext.getFilesDir().getAbsolutePath() + "/deg00.mp4", over, trans1, singer, trans2, width, height, base, getTFile().getAbsolutePath());

        String finalRotate = rotate;

        FFmpeg.executeAsync(overlay, (executionId1, returnCode1) -> {
            String aa = String.format("-y -i %s -c copy -bsf:v h264_mp4toannexb -f mpegts %s", getTFile().getAbsolutePath(), mContext.getFilesDir() + "/list2.ts");
            FFmpeg.execute(aa);

            mkTxtFile("file " + mContext.getFilesDir() + "/list2.ts" + "\n");
            listFile();

            String bb;
            if (MyRecorded.mCamId.equals("0"))
                bb = "-f concat -safe 0 -i " + getTxtFile().getAbsolutePath() + " -c copy -bsf:a aac_adtstoasc -metadata:s:v rotate=-" + finalRotate + " " + recL;
            else
                bb = "-f concat -safe 0 -i " + getTxtFile().getAbsolutePath() + " -c copy -bsf:a aac_adtstoasc -metadata:s:v rotate=" + finalRotate + " " + recL;
            FFmpeg.execute(bb);

            listDel();

            getTxtFile().delete();
            getTFile().delete();
            hideProgressDialog();
            movie.delete();

            CustomDialog customDialog = new CustomDialog(mContext);
            customDialog.callFunction(recL, "", videoFragment);
            device = null;
        });
    }

    protected void showProgressDialog() {

        progressBar2 = new ProgressDialog(mContext);
        progressBar2.setMessage("영상 생성중...");
        progressBar2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar2.setIndeterminate(true);
        progressBar2.setCancelable(false);

        Config.resetStatistics();

        progressBar2.show();
    }

    protected void hideProgressDialog() {
        progressBar2.dismiss();
    }

    public File getTxtFile() {
        return new File(mContext.getFilesDir(), "list3.txt");
    }

    public File getTFile() {
        return new File(mContext.getFilesDir(), "list2.mp4");
    }

    public void listFile() {
        File[] segf = mContext.getFilesDir().listFiles();
        String j = "01";
        int k = 1;
        if (segf != null) {
            for (File recList : segf) {
                String asd = mContext.getFilesDir().getAbsolutePath() + "/deg" + j + ".mp4";
                String asd2 = mContext.getFilesDir().getAbsolutePath() + "/deg" + j + ".ts";
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
        File[] segf = mContext.getFilesDir().listFiles();
        String j = "00";
        int k = 0;
        if (segf != null) {
            for (File recList : segf) {
                String asd = mContext.getFilesDir().getAbsolutePath() + "/deg" + j + ".mp4";
                String asd2 = mContext.getFilesDir().getAbsolutePath() + "/deg" + j + ".ts";
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

    public String ReadTextFile(String path) {
        StringBuilder strBuffer = new StringBuilder();
        try {
            InputStream is = new FileInputStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            line = reader.readLine();
            strBuffer.append(line).append("\n");

            reader.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return strBuffer.toString();
    }

    private boolean initLoadDB(String text) {
        DataAdapter mDbHelper = new DataAdapter(mContext.getApplicationContext());
        mDbHelper.open();
        // db에 있는 값들을 model을 적용해서 넣는다.
        title = mDbHelper.getSongTitle(text);
        if (title == null) {
            return true;
        }
        title = title.replace("\"", "");
        if (title.contains("(")) title = title.substring(0, title.indexOf("("));
        // db 닫기
        mDbHelper.close();
        return false;
    }

    public ArrayList<usbFile> getUsbData() {
        ArrayList<usbFile> arrayList = new ArrayList<>();

        String[] columns = new String[]{"usbUri", "usbName"};

        Cursor cursor = usbDataBase.query(columns, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                usbFile LoveData = new usbFile();

                LoveData.setUsbUri(cursor.getString(0));
                LoveData.setUsbName(cursor.getInt(1));

                arrayList.add(LoveData);
            }
        }
        return arrayList;
    }

    public void firstUsbDir() {
        UsbPerFragment exit = UsbPerFragment.getInstance();
        Bundle bundle = new Bundle();
        bundle.putParcelable("device", device);
        bundle.putString("name", asd);
        exit.setArguments(bundle);
        exit.show(videoFragment.getSupportFragmentManager(), UsbPerFragment.TAG_EVENT_DIALOG);

        exit.setDialogR(result -> {
            if (result) {
                makeMovieAfterR();
            }
        });
    }

    Disposable backgroundtask;
    String asd;

    public void dbDownLoad() {
        backgroundtask = Observable.fromCallable(() -> {
            for (; ; ) {
                asd = getStorageDirectories();
                if (asd != null) {
                    asd = asd.substring(asd.lastIndexOf("/") + 1);
                    break;
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            return asd;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                    backgroundtask.dispose();
                    df.dismiss();
                    usbFiles = getUsbData();
                    int asdf = 0;

                    if (usbFiles.size() != 0) {
                        for (usbFile aa : usbFiles) {
                            if (aa.usbName == device.getProductId()) {
                                AttachedAction();
                                asdf = 1;
                            }
                        }
                    }
                    if (asdf == 0) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            firstUsbDir();
                        } else {
                            AttachedAction();
                        }
                    }
                });
    }

    public static String getStorageDirectories() {
        String dirs = null;
        try (BufferedReader bufReader = new BufferedReader(new FileReader("/proc/mounts"))) {
            ArrayList<String> list = new ArrayList<>();
            String line;
            while ((line = bufReader.readLine()) != null) {
                if (line.contains("vfat") || line.contains("/mnt")) {
                    StringTokenizer tokens = new StringTokenizer(line, " ");
                    String s = tokens.nextToken(); // Take the second token, i.e. mount point
                    s = tokens.nextToken();
                    if (s.equals(Environment.getExternalStorageDirectory().getPath())) {
                        list.add(s);
                    } else if (line.contains("/dev/block/vold")) {
                        if (!line.contains("/mnt/secure") && !line.contains("/mnt/asec") && !line.contains("/mnt/obb") && !line.contains("/dev/mapper") && !line.contains("tmpfs")) {
                            list.add(s);
                        }
                    }
                }
            }
            for (int i = 0; i < list.size(); i++) {
                dirs = list.get(i);
            }
        } catch (IOException ignored) {
        }
        return dirs;
    }
}
