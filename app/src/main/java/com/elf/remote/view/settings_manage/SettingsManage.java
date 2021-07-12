package com.elf.remote.view.settings_manage;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.ActivitySettingsManageBinding;
import com.elf.remote.Application;
import com.elf.remote.CallActivity;
import com.elf.remote.model.bluetooth.BluetoothCon;
import com.elf.remote.model.data.VerSionMachin;
import com.elf.remote.view.DelOkFragment;
import com.elf.remote.viewmodel.settings_manage.SettingsManageViewModel;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SettingsManage extends AppCompatActivity implements CallActivity {

    SmartEQFragment smart;
    MicEffectFragment mic;
    OtherEffectFragment other;
    FavoriteSongFragment favorite;
    SettingsManageViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BluetoothCon bluetoothCon = new BluetoothCon(this);
        ProgressDialog progressBar2 = new ProgressDialog(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        model = new SettingsManageViewModel(this, bluetoothCon, progressBar2, builder);

        ActivitySettingsManageBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_settings_manage);
        binding.setViewModel(model);
        binding.executePendingBindings();

        model.onCreate();

        smart = new SmartEQFragment();
        mic = new MicEffectFragment();
        other = new OtherEffectFragment();
        favorite = new FavoriteSongFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.set_manage_frame, smart).commit();
        VerSionMachin.setSetKind(0);

        binding.taptap.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                changeView(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void changeView(int index) {
        switch (index) {
            case 0 :
                getSupportFragmentManager().beginTransaction().replace(R.id.set_manage_frame, smart).commit();
                VerSionMachin.setSetKind(index);
                break;
            case 1 :
                getSupportFragmentManager().beginTransaction().replace(R.id.set_manage_frame, mic).commit();
                VerSionMachin.setSetKind(index);
                break;
            case 2 :
                getSupportFragmentManager().beginTransaction().replace(R.id.set_manage_frame, other).commit();
                VerSionMachin.setSetKind(index);
                break;
            case 3 :
                getSupportFragmentManager().beginTransaction().replace(R.id.set_manage_frame, favorite).commit();
                VerSionMachin.setSetKind(index);
                break;
        }
    }
    public void dataChg() {
        switch (VerSionMachin.getSetKind()) {
            case 0:
                SmartEQFragment.model.movFiles.clear();
                SmartEQFragment.model.getFileList("db");
                smart.binding.listView.setSelector(R.drawable.list_sel2);
                SmartEQFragment.myAdapter.notifyDataSetChanged();
                VerSionMachin.setMultiCheck(false);
                break;
            case 1:
                MicEffectFragment.model.movFiles.clear();
                MicEffectFragment.model.getFileList("db");
                mic.binding.listView.setSelector(R.drawable.list_sel2);
                MicEffectFragment.myAdapter.notifyDataSetChanged();
                VerSionMachin.setMultiCheck(false);
                break;
            case 2:
                OtherEffectFragment.model.movFiles.clear();
                OtherEffectFragment.model.getFileList("db");
                other.binding.listView.setSelector(R.drawable.list_sel2);
                OtherEffectFragment.myAdapter.notifyDataSetChanged();
                VerSionMachin.setMultiCheck(false);
                break;
            case 3:
                FavoriteSongFragment.model.movFiles.clear();
                FavoriteSongFragment.model.getFileList("db");
                favorite.binding.listView.setSelector(R.drawable.list_sel2);
                FavoriteSongFragment.myAdapter.notifyDataSetChanged();
                VerSionMachin.setMultiCheck(false);
                break;
        }
    }

    @Override
    public void callActivity() { // 삭제
        if (VerSionMachin.isMultiCheck()) {
            int num = VerSionMachin.getPathList().size();
            if (num > 0) {
                DelOkFragment t = DelOkFragment.getInstance();
                Bundle bundle = new Bundle();
                bundle.putString("text", String.valueOf(num));
                bundle.putBoolean("true", true);
                t.setArguments(bundle);
                t.show(getSupportFragmentManager(), "DIALOG");

                t.setDialogR(result -> {
                    if (result) {
                        for (String path : VerSionMachin.getPathList()) {
                            File bfFile = new File(path);
                            bfFile.delete();
                        }
                        dataChg();
                        VerSionMachin.setPathList(null);
                    }
                });
            }
        } else {
            if (VerSionMachin.getFilePath() != null) {
                DelOkFragment t = DelOkFragment.getInstance();
                String checkPath = VerSionMachin.getFilePath();
                String checkName = checkPath.substring(checkPath.lastIndexOf('/') + 1, checkPath.indexOf(".db"));
                Bundle bundle = new Bundle();
                bundle.putString("text", checkName);
                bundle.putBoolean("true", false);
                t.setArguments(bundle);
                t.show(getSupportFragmentManager(), "DIALOG");

                t.setDialogR(result -> {
                    if (result) {
                        File bfFile = new File(checkPath);
                        bfFile.delete();

                        dataChg();
                        VerSionMachin.setFilePath(null);
                    }
                });
            }
        }
    }

    @Override
    public void callDialog() { // 이름 변경
        if (VerSionMachin.isMultiCheck()) {
            int num = VerSionMachin.getPathList().size();
            if (num > 0) {
                for (String path : VerSionMachin.getPathList()) {
                    String realPath = path.substring(0, path.lastIndexOf('/') + 1);
                    String checkName = path.substring(path.lastIndexOf('/') + 1, path.indexOf(".db"));
                    SaveNameFragment t = SaveNameFragment.getInstance();
                    Bundle bundle = new Bundle();
                    bundle.putString("text", checkName);
                    t.setArguments(bundle);
                    t.show(getSupportFragmentManager(), path);

                    t.setDialogR(result -> {
                        File bfFile = new File(path);
                        File afFile = new File((realPath + result));
                        File parent = new File(realPath);
                        String name = result;
                        File[] list = parent.listFiles();

                        if (!bfFile.equals(afFile)) {
                            int i = 1;
                            if (list != null) {
                                for (int x = 0; x < list.length; x++) {
                                    if (list[x].getName().equals(result)) {
                                        result = name.substring(0, name.indexOf(".db")) + " (" + i + ").db";
                                        i++;
                                        x = -1;
                                    }
                                }
                            }
                            afFile = new File(realPath + result);
                        }

                        bfFile.renameTo(afFile);
                        dataChg();
                        VerSionMachin.setPathList(null);
                    });
                }
            }
        } else {
            if (VerSionMachin.getFilePath() != null) {
                String checkPath = VerSionMachin.getFilePath();
                String realPath = checkPath.substring(0, checkPath.lastIndexOf('/') + 1);
                String checkName = checkPath.substring(checkPath.lastIndexOf('/') + 1, checkPath.indexOf(".db"));
                SaveNameFragment t = SaveNameFragment.getInstance();
                Bundle bundle = new Bundle();
                bundle.putString("text", checkName);
                t.setArguments(bundle);
                t.show(getSupportFragmentManager(), "DIALOG");

                t.setDialogR(result -> {
                    File bfFile = new File(checkPath);
                    File afFile = new File((realPath + result));
                    File parent = new File(realPath);
                    String name = result;
                    File[] list = parent.listFiles();

                    if (!bfFile.equals(afFile)) {
                        int i = 1;
                        if (list != null) {
                            for (int x = 0; x < list.length; x++) {
                                if (list[x].getName().equals(result)) {
                                    result = name.substring(0, name.indexOf(".db")) + " (" + i + ").db";
                                    i++;
                                    x = -1;
                                }
                            }
                        }
                        afFile = new File(realPath + result);
                    }

                    bfFile.renameTo(afFile);
                    dataChg();
                    VerSionMachin.setFilePath(null);
                });
            }
        }
    }

    @Override
    public void exitActivity() { // 불러오기 저장
        SaveNameFragment t = SaveNameFragment.getInstance();
        Bundle bundle = new Bundle();
        if (VerSionMachin.getSetKind() == 0) {
            bundle.putString("text", "스마트 EQ");
        } else if (VerSionMachin.getSetKind() == 1) {
            bundle.putString("text", "마이크 이펙터");
        } else if (VerSionMachin.getSetKind() == 2) {
            bundle.putString("text", "기타 이펙터");
        } else {
            bundle.putString("text", "애창곡");
        }

        t.setArguments(bundle);
        t.show(getSupportFragmentManager(), "DIALOG");

        t.setDialogR(result -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                String what;
                if (VerSionMachin.getSetKind() == 0) {
                    what = "smart";
                } else if (VerSionMachin.getSetKind() == 1) {
                    what = "mic";
                } else if (VerSionMachin.getSetKind() == 2) {
                    what = "other";
                } else {
                    what = "favorite";
                }

                saveVideoFile(result, what);
            } else {
                String main, what;

                main = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ElfData/";

                if (VerSionMachin.getSetKind() == 0) {
                    what = main + "smart/";
                } else if (VerSionMachin.getSetKind() == 1) {
                    what = main + "mic/";
                } else if (VerSionMachin.getSetKind() == 2) {
                    what = main + "other/";
                } else {
                    what = main + "favorite/";
                }

                model.copyFileList(what + result, model.path);
            }

            dataChg();
            VerSionMachin.setFilePath(null);
        });
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
                            exitActivity();
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

                                    dataChg();
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