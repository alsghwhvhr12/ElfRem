package com.elf.remote.view.settings;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.ActivitySettingsBinding;
import com.elf.remote.Application;
import com.elf.remote.ListEdit;
import com.elf.remote.model.bluetooth.BluetoothCon;
import com.elf.remote.model.bluetooth.BtDevice;
import com.elf.remote.model.data.VerSionMachin;
import com.elf.remote.view.banjugi.BanConnect;
import com.elf.remote.view.search.TimeSetFragment;
import com.elf.remote.view.settings_manage.SaveNameFragment;
import com.elf.remote.viewmodel.settings.SettingsViewModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Settings extends AppCompatActivity implements ListEdit {
    SettingsViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BluetoothCon bluetoothCon = new BluetoothCon(this);
        ProgressDialog progressBar2 = new ProgressDialog(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        model = new SettingsViewModel(this, bluetoothCon, progressBar2, builder);

        ActivitySettingsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        binding.setViewModel(model);
        binding.executePendingBindings();

        if (VerSionMachin.getName().equals("919")) {
            binding.speaker.setVisibility(View.VISIBLE);
            binding.speakerLine.setVisibility(View.VISIBLE);
            binding.save.setVisibility(View.GONE);
            binding.saveLine.setVisibility(View.GONE);
            binding.load.setVisibility(View.GONE);
            binding.loadLine.setVisibility(View.GONE);
        } else {
            binding.speaker.setVisibility(View.GONE);
            binding.speakerLine.setVisibility(View.GONE);
            binding.save.setVisibility(View.VISIBLE);
            binding.saveLine.setVisibility(View.VISIBLE);
            binding.load.setVisibility(View.VISIBLE);
            binding.loadLine.setVisibility(View.VISIBLE);
        }

        PackageInfo i = null;
        try {
            i = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (i != null) {
            binding.versionTxt.setText(i.versionName);
        }
    }

    @Override
    public void editList() {
        SyncFragment mov = SyncFragment.getInstance();
        mov.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Light_NoTitleBar);
        mov.show(getSupportFragmentManager(), "tag");
    }

    @Override
    public void dleList() {
        TimeSetFragment ts = TimeSetFragment.getInstance();
        Bundle bundle = new Bundle();
        bundle.putInt("kind", 99);
        ts.setArguments(bundle);
        ts.show(getSupportFragmentManager(), "timeSet");
    }

    @Override
    public void exitList() {
        if (BtDevice.getDevice() == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("스피커 음향 분석은 \n반주기 연결이 필요합니다.\n반주기를 연결하시겠습니까?");
            builder.setNegativeButton("취소",
                    (dialog, which) -> {
                    });
            builder.setPositiveButton("연결",
                    (dialog, which) -> startActivity(new Intent(this, BanConnect.class)));
            builder.show();
        } else {
            SpeakerFragment mov = SpeakerFragment.getInstance();
            mov.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Light_NoTitleBar);
            mov.show(getSupportFragmentManager(), "tag");
        }
    }

    @Override
    public void shareList() {
        SaveNameFragment t = SaveNameFragment.getInstance();
        Bundle bundle = new Bundle();
        bundle.putString("text", "애창곡");
        t.setArguments(bundle);
        t.show(getSupportFragmentManager(), "DIALOG");

        t.setDialogR(result -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                String what;
                what = "favorite";

                saveVideoFile(result, what);
            } else {
                String main, what;

                main = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ElfData/";
                what = main + "favorite/";

                model.copyFileList(what + result, model.path);
            }

            VerSionMachin.setFilePath(null);
        });
    }

    @Override
    public void chgFile() {
        TimeSetFragment ts = TimeSetFragment.getInstance();
        Bundle bundle = new Bundle();
        bundle.putInt("kind", 6);

        ts.setArguments(bundle);
        ts.show(getSupportFragmentManager(), "ts");
    }

    @Override
    public void selList() {

    }

    private void saveVideoFile(String name, String saveF) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.TITLE, name);
            values.put(MediaStore.Downloads.DISPLAY_NAME, name);
            values.put(MediaStore.Downloads.DATE_ADDED, System.currentTimeMillis() / 1000);

            values.put(MediaStore.Downloads.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Downloads.RELATIVE_PATH, "Download/" + saveF);
            values.put(MediaStore.Downloads.IS_PENDING, 1);

            ContentResolver contentResolver = Application.applicationContext().getContentResolver();

            Uri collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);

            Uri item = contentResolver.insert(collection, values);

            Cursor video_c = Application.applicationContext().getContentResolver().query(MediaStore.Downloads.EXTERNAL_CONTENT_URI, null, null, null, null);
            int vidid_index = video_c.getColumnIndex(MediaStore.Downloads._ID);
            int viddata_index = video_c.getColumnIndex(MediaStore.Downloads.TITLE);
            int full = video_c.getColumnIndex(MediaStore.Downloads.DATA);

            String fullPathString = "";
            Uri uri = null;

            if (video_c.moveToFirst()) {
                do {
                    long rowId = video_c.getLong(vidid_index);
                    String path = video_c.getString(viddata_index);
                    String path2 = video_c.getString(full);
                    if (path.equals(name.substring(0, name.indexOf('.'))) && path2.contains(saveF)) {
                        uri = ContentUris.withAppendedId(MediaStore.Downloads.EXTERNAL_CONTENT_URI, rowId);
                        fullPathString = path;
                        break;
                    }
                } while (video_c.moveToNext());
            }
            video_c.close();

            if (fullPathString.equals(name.substring(0, name.indexOf('.')))) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("파일이 이미 존재합니다.\n덮어씌우시겠습니까?");
                builder.setNegativeButton("취소",
                        (dialog, which) -> {
                        });
                Uri finalUri = uri;
                builder.setPositiveButton("확인",
                        (dialog, which) -> {
                            try {
                                getContentResolver().delete(finalUri, null, null);

                                ParcelFileDescriptor pdf = contentResolver.openFileDescriptor(item, "w", null);

                                if (pdf == null) {
                                    Log.d("asdf", "null");
                                } else {
                                    FileOutputStream fos = new FileOutputStream(pdf.getFileDescriptor());

                                    File storageDir = new File(model.path);

                                    FileInputStream in = new FileInputStream(storageDir);

                                    byte[] buf = new byte[8192];
                                    int len;
                                    while ((len = in.read(buf)) > 0) {
                                        fos.write(buf, 0, len);
                                    }

                                    in.close();
                                    fos.close();
                                    pdf.close();

                                    values.clear();
                                    values.put(MediaStore.Downloads.IS_PENDING, 0);
                                    contentResolver.update(item, values, null, null);

                                    File file = new File(model.path);
                                    file.delete();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                builder.show();
            } else {
                try {
                    ParcelFileDescriptor pdf = contentResolver.openFileDescriptor(item, "w", null);

                    if (pdf == null) {
                        Log.d("asdf", "null");
                    } else {
                        FileOutputStream fos = new FileOutputStream(pdf.getFileDescriptor());

                        File storageDir = new File(model.path);

                        FileInputStream in = new FileInputStream(storageDir);

                        byte[] buf = new byte[8192];
                        int len;
                        while ((len = in.read(buf)) > 0) {
                            fos.write(buf, 0, len);
                        }

                        in.close();
                        fos.close();
                        pdf.close();

                        values.clear();
                        values.put(MediaStore.Downloads.IS_PENDING, 0);
                        contentResolver.update(item, values, null, null);

                        File file = new File(model.path);
                        file.delete();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}