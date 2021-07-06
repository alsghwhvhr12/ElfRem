package com.elf.remote.view.my_video;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.MediaController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.FragmentVideoViewBinding;
import com.elf.remote.utils.RecycleUtils;
import com.elf.remote.viewmodel.my_video.VideoViewViewModel;

import java.io.File;

public class VideoViewFragment extends AppCompatActivity {
    private FragmentVideoViewBinding binding;
    String path;
    int kind;
    MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.fragment_video_view);
        binding.setViewModel(new VideoViewViewModel());
        binding.executePendingBindings();

        AudioManager mAudioManager = (AudioManager)getSystemService(AUDIO_SERVICE);

        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                (int)(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING) * 0.30),
                AudioManager.FLAG_PLAY_SOUND);

        Intent intent = getIntent();
        kind = intent.getIntExtra("kind", 0);
        path = intent.getStringExtra("path");

        Uri uri = Uri.fromFile(new File(path));

        if(kind == 4) {
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.musicbkfull);
            binding.videoView.setForeground(drawable);
        } else {
            binding.videoView.setForeground(null);
        }

        binding.videoView.setVideoURI(uri);

        binding.videoView.setOnPreparedListener(mediaPlayer -> {
            mediaController = new MediaController(this){
                @Override
                public boolean dispatchKeyEvent(KeyEvent event) {
                    if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
                        super.hide();//Hide mediaController
                        return true;//If press Back button, finish here
                    }
                    //If not Back button, other button (volume) work as usual.
                    return super.dispatchKeyEvent(event);
                }
            };

            binding.videoView.setMediaController(mediaController);
            binding.videoView.start();
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        if (binding.videoView.isPlaying()) binding.videoView.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        binding.videoView.stopPlayback();

        RecycleUtils.recursiveRecycle(getWindow().getDecorView());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if(binding.videoView.isPlaying()) binding.videoView.stopPlayback();
            mediaController.hide();
            finish();
            return false;
        }
        return false;
    }
}