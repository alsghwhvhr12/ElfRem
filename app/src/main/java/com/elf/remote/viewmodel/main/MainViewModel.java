package com.elf.remote.viewmodel.main;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.elf.mvvmremote.BR;
import com.elf.remote.Application;
import com.elf.remote.model.data.VerSionMachin;
import com.elf.remote.viewmodel.BaseViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends BaseObservable implements BaseViewModel {
    private final ProgressDialog progressBar;
    private final String savePath;
    private final AlertDialog.Builder builder;
    private long len;
    private String date;
    private String update;
    SharedPreferences preferences;

    String namePre = "singer";
    String kindPre = "KindTip";
    String lastUpdaate = "LastUpdate";

    public MainViewModel(ProgressDialog progressBar, String savePath, AlertDialog.Builder builder) {
        this.progressBar = progressBar;
        this.savePath = savePath;
        this.builder = builder;
    }

    @Override
    public void onCreate() {
        VerSionMachin.setName("G10");
        VerSionMachin.setStop(1);

        progressBar.setTitle("DB파일 다운로드");
        progressBar.setMessage("다운로드중...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        preferences = PreferenceManager.getDefaultSharedPreferences(Application.applicationContext());
        String str2 = preferences.getString(namePre, "");
        update = preferences.getString(lastUpdaate, "");
        if (str2.equals("")) {
            setName();
        }

        dbChecked("https://www.elf.co.kr/pds/choice/sqlitedb.db");
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

    @Bindable
    private String toastMessage = null;

    public String getToastMessage() {
        return toastMessage;
    }

    private void setToastMessage(String toastMessage) {
        this.toastMessage = toastMessage;
        notifyPropertyChanged(BR.toastMessage);
    }

    public void onSearchMClicked() {
        setToastMessage("Search");
    }

    public void onRemoteCClicked() {
        setToastMessage("Remote");
    }

    public void onBanCClicked() {
        setToastMessage("Ban");
    }

    public void onMyRClicked() {
        setToastMessage("MyR");
    }

    public void onViewRClicked() {
        setToastMessage("ViewR");
    }

    public void onMyVClicked() {
        setToastMessage("MyVideo");
    }

    public void onChanClicked() {
        setToastMessage("Cha");
    }

    public void onBandClicked() {
        setToastMessage("Band");
    }

    public void onSettings() {
        setToastMessage("Set");
    }

    public void onSettingsManage() {
        setToastMessage("SetMa");
    }

    public void onCamera() {
        setToastMessage("Cam");
    }

    Disposable backgroundtask;

    public void dbDownLoad(String param) {
        progressBar.show();

        String fileName = "sqlitedb.db";
        String localPath = savePath + fileName;
        SharedPreferences.Editor editor = preferences.edit();


        backgroundtask = Observable.fromCallable(() -> {
            //다운로드 경로를 지정
            File dir = new File(savePath);

            if (!dir.exists()) {
                if (dir.mkdirs()) {
                    Log.d("dbFile", "make");
                }
            }

            try {
                URL imgUrl = new URL(param);
                //서버와 접속하는 클라이언트 객체 생성
                HttpURLConnection conn = (HttpURLConnection) imgUrl.openConnection();
                len = conn.getContentLength();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
                editor.putString(lastUpdaate, simpleDateFormat.format(conn.getLastModified()));
                editor.apply();

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
                        progressBar.setIndeterminate(false);
                        progressBar.setMax(100);
                        progressBar.setCancelable(false);
                        progressBar.setProgress((int) ((downloadedSize * 100) / len));
                    }
                    fos.write(tmpByte, 0, read); //file 생성
                }
                is.close();
                fos.close();
                conn.disconnect();
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

    public void dbChecked(String param) {
        String fileName = "sqlitedb.db";
        String localPath = savePath + fileName;
        File file2 = new File(localPath);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        backgroundtask = Observable.fromCallable(() -> {
            try {
                URL imgUrl = new URL(param);
                HttpURLConnection conn = (HttpURLConnection) imgUrl.openConnection();
                date = simpleDateFormat.format(conn.getLastModified());
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return len;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                    backgroundtask.dispose();

                    if (file2.exists()) {
                        if (!date.equals(update)) {
                            builder.setTitle("DB업데이트");
                            builder.setMessage("새로운 DB가 업데이트 되었습니다. 다운로드 받을까요?");
                            builder.setNegativeButton("아니오",
                                    (dialog, which) -> {
                                    });
                            builder.setPositiveButton("예",
                                    (dialog, which) -> {
                                        if (file2.delete()) {
                                            Log.d("dbFile", "delete");
                                        }
                                        dbDownLoad(param);
                                    });
                            builder.show();
                        }
                    } else {
                        if (file2.delete()) {
                            Log.d("dbFile", "delete");
                        }
                        dbDownLoad(param);
                    }
                });
    }



    void setName() {
        BluetoothAdapter mDevice = BluetoothAdapter.getDefaultAdapter();
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(namePre, mDevice.getName());
        editor.putString(kindPre, "1");
        editor.apply();
    }

    public String[] checkPermission() {
        ArrayList<String> permissions = new ArrayList<>();

        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.BLUETOOTH);
        permissions.add(Manifest.permission.BLUETOOTH_ADMIN);
        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.RECORD_AUDIO);
        permissions.add(Manifest.permission.INTERNET);
        String[] reqPermissionArray = new String[permissions.size()];
        reqPermissionArray = permissions.toArray(reqPermissionArray);

        return reqPermissionArray;
    }
}
