package com.elf.remote.view.main;

import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.ActivityPermissionBinding;
import com.elf.remote.Application;
import com.elf.remote.CallActivity;
import com.elf.remote.viewmodel.main.PermissionViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PermissionActivity extends AppCompatActivity implements CallActivity {
    ActivityPermissionBinding binding;
    String[] reqPermissionArray;
    int num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_permission);
        binding.setViewModel(new PermissionViewModel(this));
        binding.executePendingBindings();

        reqPermissionArray = getIntent().getExtras().getStringArray("permission");
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        finishAndRemoveTask();
        System.exit(0);
    }

    @Override
    public void callActivity() {
        for (String check : reqPermissionArray) {
            if (ActivityCompat.checkSelfPermission(this, check) != 0) {
                ActivityCompat.requestPermissions(this, reqPermissionArray, 1);
            }
        }
    }

    @Override
    public void exitActivity() {

    }

    @Override
    public void callDialog() {

    }

    @Override
    public void onRequestPermissionsResult(int reqCode, @NonNull String[] permissions, @NonNull int[] grantResult) {
        super.onRequestPermissionsResult(reqCode, permissions, grantResult);
        for (int check : grantResult) {
            if (check == 0) {
                num += 1;
            }
        }

        if (num >= 8) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                makeDirAfterR();
            } else {
                makeDirBeforeR();
            }

            finish();
        } else {
            num = 0;
        }
    }

    public static void makeDirAfterR() {
        String main, logo, movie;

        String savePath = Application.applicationContext().getApplicationInfo().dataDir + "/databases/";
        main = Application.applicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/ElfData/";
        movie = Application.applicationContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES) + "/ElfData/";
        logo = Application.applicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/ElfData/logo/";

        File dir1 = new File(main);
        File dir2 = new File(movie);
        File dir4 = new File(logo);

        if (!dir1.exists()) {
            if(dir1.mkdirs()) {
                Log.d("main", "make");
            }
        }
        if (!dir2.exists()) {
            if(dir2.mkdirs()) {
                Log.d("movie", "make");
            }
        }
        if (!dir4.exists()) {
            if(dir4.mkdirs()) {
                Log.d("logo", "make");
            }
        }

        File logoDir = new File(dir4 + "/mainlogo.webp");
        File Nanum = new File(dir4 + "/nanum.ttf");
        File eul10 = new File(dir4 + "/eul10.ttf");
        File eul = new File(dir4 + "/eul.ttf");
        File grand = new File(dir4 + "/grand.ttf");
        File mugung = new File(dir4 + "/mugung.ttf");
        File up = new File(dir4 + "/up.ttf");
        File somi = new File(dir4 + "/somi.ttf");
        File happy = new File(dir4 + "/happy.ttf");
        File db = new File(savePath + "/mylove.db");

        if (!logoDir.exists()) saveLogoFiles(logoDir, "mainlogo.webp");
        if (!Nanum.exists()) saveLogoFiles(Nanum, "font/nanum.ttf");
        if (!eul10.exists()) saveLogoFiles(eul10, "font/eul10.ttf");
        if (!eul.exists()) saveLogoFiles(eul, "font/eul.ttf");
        if (!grand.exists()) saveLogoFiles(grand, "font/grand.ttf");
        if (!mugung.exists()) saveLogoFiles(mugung, "font/mugung.ttf");
        if (!up.exists()) saveLogoFiles(up, "font/up.ttf");
        if (!somi.exists()) saveLogoFiles(somi, "font/somi.ttf");
        if (!happy.exists()) saveLogoFiles(happy, "font/happy.ttf");
        if (!db.exists()) saveLogoFiles(db, "mylove.db");
    }

    public static void makeDirBeforeR() {
        String main, video, audio, logo;

        String savePath = Application.applicationContext().getApplicationInfo().dataDir + "/databases/";
        main = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ElfData/";
        video = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ElfData/baseVideo/";
        audio = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ElfData/baseAudio/";
        logo = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ElfData/logo/";

        File dir1 = new File(main);
        File dir2 = new File(video);
        File dir3 = new File(audio);
        File dir4 = new File(logo);

        if (!dir1.exists()) {
            if(dir1.mkdirs()) {
                Log.d("main", "make");
            }
        }
        if (!dir2.exists()) {
            if(dir2.mkdirs()) {
                Log.d("video", "make");
            }
        }
        if (!dir3.exists()) {
            if(dir3.mkdirs()) {
                Log.d("audio", "make");
            }
        }
        if (!dir4.exists()) {
            if(dir4.mkdirs()) {
                Log.d("logo", "make");
            }
        }

        File logoDir = new File(dir4 + "/mainlogo.webp");
        File Nanum = new File(dir4 + "/nanum.ttf");
        File eul10 = new File(dir4 + "/eul10.ttf");
        File eul = new File(dir4 + "/eul.ttf");
        File grand = new File(dir4 + "/grand.ttf");
        File mugung = new File(dir4 + "/mugung.ttf");
        File up = new File(dir4 + "/up.ttf");
        File somi = new File(dir4 + "/somi.ttf");
        File happy = new File(dir4 + "/happy.ttf");
        File db = new File(savePath + "/mylove.db");

        if (!logoDir.exists()) saveLogoFiles(logoDir, "mainlogo.webp");
        if (!Nanum.exists()) saveLogoFiles(Nanum, "font/nanum.ttf");
        if (!eul10.exists()) saveLogoFiles(eul10, "font/eul10.ttf");
        if (!eul.exists()) saveLogoFiles(eul, "font/eul.ttf");
        if (!grand.exists()) saveLogoFiles(grand, "font/grand.ttf");
        if (!mugung.exists()) saveLogoFiles(mugung, "font/mugung.ttf");
        if (!up.exists()) saveLogoFiles(up, "font/up.ttf");
        if (!somi.exists()) saveLogoFiles(somi, "font/somi.ttf");
        if (!happy.exists()) saveLogoFiles(happy, "font/happy.ttf");
        if (!db.exists()) saveLogoFiles(db, "mylove.db");
    }

    private static void saveLogoFiles(File file, String name) {
        AssetManager manager = Application.applicationContext().getAssets();
        InputStream open;
        FileOutputStream fos;

        try {
            open = manager.open(name);
            int size = open.available();
            byte[] buffer = new byte[size];
            fos = new FileOutputStream(file);
            for (int c = open.read(buffer); c != -1; c = open.read(buffer)) {
                fos.write(buffer, 0, c);
            }
            open.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}