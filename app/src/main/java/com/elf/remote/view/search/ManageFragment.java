package com.elf.remote.view.search;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.FragmentManageBinding;
import com.elf.remote.Application;
import com.elf.remote.CallActivity;
import com.elf.remote.view.settings_manage.SaveNameFragment;
import com.elf.remote.viewmodel.search.ManageViewModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class ManageFragment extends DialogFragment implements CallActivity {

    public ManageFragment() {
    }

    public static ManageFragment getInstance() {
        return new ManageFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentManageBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_manage, container, false);
        binding.setViewModel(new ManageViewModel(this));
        binding.executePendingBindings();

        binding.bkmanage.setBackgroundResource(R.drawable.sdbphonesvbk);
        binding.sdBtn1.setBackgroundResource(R.drawable.click_sr_sdphone);
        binding.sdBtn2.setBackgroundResource(R.drawable.click_sr_sdapp);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setCancelable(false);

        return binding.getRoot();
    }

    @Override
    public void callActivity() {
        TimeSetFragment ts = TimeSetFragment.getInstance();
        Bundle bundle = new Bundle();
        bundle.putInt("kind", 1);

        ts.setArguments(bundle);
        ts.show(getChildFragmentManager(), "ts");

    }

    @Override
    public void exitActivity() {
        dismiss();
    }

    @Override
    public void callDialog() {
        SaveNameFragment t = SaveNameFragment.getInstance();
        Bundle bundle = new Bundle();
        bundle.putString("text", "LoadFile");
        t.setArguments(bundle);
        t.show(getChildFragmentManager(), "DIALOG");

        t.setDialogR(result -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                saveVideoFile(result, "mylove.db");
                saveVideoFile("myResv.db", "myResv.db");
            } else {
                copyFileList(result, "myResv.db");
            }
        });
    }

    public void copyFileList(String copyFileName1, String copyFileName2) {
        String copyPath;
        String copyPath2;
        String savePath;
        //다운로드 경로를 지정
        copyPath = requireActivity().getApplicationInfo().dataDir + "/databases/mylove.db";
        copyPath2 = requireActivity().getApplicationInfo().dataDir + "/databases/";

        savePath = Environment.getExternalStorageDirectory() + "/ElfData/favorite/";

        File dirFile = new File(savePath);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }

        File newFile = new File(savePath + copyFileName1);
        File copyFile = new File(copyPath);

        File newFile2 = new File(savePath + copyFileName2);
        File copyFile2 = new File(copyPath2 + copyFileName2);

        if (newFile.exists()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("파일이 이미 존재합니다.\n덮어씌우시겠습니까?");
            builder.setNegativeButton("취소",
                    (dialog, which) -> {
                        callDialog();
                    });
            builder.setPositiveButton("확인",
                    (dialog, which) -> {
                        try {
                            copy(copyFile, newFile);
                            copy(copyFile2, newFile2);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
            builder.show();
        } else {
            try {
                copy(copyFile, newFile);
                copy(copyFile2, newFile2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void copy(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }

    String save;
    String name;
    private void saveVideoFile(String name, String save) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            this.save = save;
            this.name = name;
            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.TITLE, name);
            values.put(MediaStore.Downloads.DISPLAY_NAME, name);
            values.put(MediaStore.Downloads.DATE_ADDED, System.currentTimeMillis() / 1000);

            values.put(MediaStore.Downloads.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Downloads.RELATIVE_PATH, "Download/favorite");
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
                    if (path.equals(name.substring(0, name.indexOf('.'))) && path2.contains("favorite") && !path.equals("myResv")) {
                        uri = ContentUris.withAppendedId(MediaStore.Downloads.EXTERNAL_CONTENT_URI, rowId);
                        fullPathString = path;
                        break;
                    } else if (path.equals("myResv")) {
                        Uri resvUri = ContentUris.withAppendedId(MediaStore.Downloads.EXTERNAL_CONTENT_URI, rowId);
                        Application.applicationContext().getContentResolver().delete(resvUri, null, null);
                    }
                } while (video_c.moveToNext());
            }
            video_c.close();

            if (fullPathString.equals(name.substring(0, name.indexOf('.')))) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("파일이 이미 존재합니다.\n덮어씌우시겠습니까?");
                builder.setNegativeButton("취소",
                        (dialog, which) -> {
                            callDialog();
                        });
                Uri finalUri = uri;
                builder.setPositiveButton("확인",
                        (dialog, which) -> {
                            try {
                                Application.applicationContext().getContentResolver().delete(finalUri, null, null);

                                ParcelFileDescriptor pdf = contentResolver.openFileDescriptor(item, "w", null);

                                if (pdf == null) {
                                    Log.d("asdf", "null");
                                } else {
                                    FileOutputStream fos = new FileOutputStream(pdf.getFileDescriptor());

                                    File storageDir = new File(requireActivity().getApplicationInfo().dataDir + "/databases/");
                                    File imageFile = new File(storageDir, save);

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
                                    values.put(MediaStore.Downloads.IS_PENDING, 0);
                                    contentResolver.update(item, values, null, null);

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

                        File storageDir = new File(requireActivity().getApplicationInfo().dataDir + "/databases/");
                        File imageFile = new File(storageDir, save);

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
                        values.put(MediaStore.Downloads.IS_PENDING, 0);
                        try {
                            contentResolver.update(item, values, null, null);
                        } catch (RecoverableSecurityException e) {
                            IntentSender intent = e.getUserAction().getActionIntent().getIntentSender();
                            startIntentSenderForResult(intent, 1, null, 0, 0, 0, null);
                        }

                    }
                } catch (IOException | IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            saveVideoFile(name, save);
        }
    }

    @Override
    public void onResume() {
        dialog();
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

    public void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            builder.setMessage("곡목록 관리는 애창곡과 예약곡을 스마트폰 내장메모리/Download/favorite mylove.db, myresv.db로 저장하고 읽어오는 기능입니다.");
        } else {
            builder.setMessage("곡목록 관리는 애창곡과 예약곡을 스마트폰 내장메모리/ElfData/favorite에 mylove.db, myresv.db로 저장하고 읽어오는 기능입니다.");
        }
        builder.setNegativeButton("확인",
                (dialog, which) -> {
                });
        builder.show();
    }
}