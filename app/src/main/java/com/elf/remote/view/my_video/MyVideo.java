package com.elf.remote.view.my_video;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.ActivityMyVideoBinding;
import com.elf.remote.MyMovCall;
import com.elf.remote.utils.RecycleUtils;
import com.elf.remote.view.SlidePager;
import com.elf.remote.viewmodel.my_video.MyVideoViewModel;

public class MyVideo extends AppCompatActivity implements MyMovCall {
    ActivityMyVideoBinding binding;

    MyVideoViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ProgressDialog progressBar = new ProgressDialog(this);
        ProgressDialog progressBar2 = new ProgressDialog(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        model = new MyVideoViewModel(this, progressBar, progressBar2, builder);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_video);
        binding.setViewModel(model);
        binding.executePendingBindings();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        model.onCreate();

        Intent intent;
        if (model.preChk()) {
            intent = new Intent(this, SlidePager.class);
            intent.putExtra("kind", 2);
            startActivity(intent);
        }
    }

    @Override
    public void callBase() {
        Intent intent = new Intent(this, VideoViewFragment.class);
        intent.putExtra("kind", 5);
        intent.putExtra("path", model.VideoPath());
        startActivity(intent);
    }

    @Override
    public void callMovsel() {
        MyMovKindFragment mov = MyMovKindFragment.getInstance();
        mov.show(getSupportFragmentManager(), "kindDialog");

        mov.setDialogR((path, Fname) -> {
            binding.frameMov.setBackgroundResource(R.drawable.musicwav);
            model.setVideoName(Fname, path);
        });
    }

    @Override
    public void callAudsel() {
        MyAudMovFragment mov = MyAudMovFragment.getInstance();
        Bundle bundle = new Bundle();
        bundle.putInt("jong", 3);
        mov.setArguments(bundle);
        mov.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_NoTitleBar);
        mov.show(getSupportFragmentManager(), "tag");


        mov.setDialogR((path, Fname) -> {
            binding.frameAud.setBackgroundResource(R.drawable.musicwav);
            model.setAudioName(Fname, path);
        });
    }

    @Override
    public void callList() {
        MyMovListFragment mov = MyMovListFragment.getInstance();
        mov.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_NoTitleBar);
        mov.show(getSupportFragmentManager(), "tag");
    }

    @Override
    public void callClipmk() {
        startActivity(Intent.createChooser(model.shareMyVideo(), ""));
    }

    @Override
    public void onResume() {
        super.onResume();
        model.onResume();
    }

    @Override
    protected void onDestroy() {
        RecycleUtils.recursiveRecycle(getWindow().getDecorView());
        model.onPause();
        super.onDestroy();
    }
}