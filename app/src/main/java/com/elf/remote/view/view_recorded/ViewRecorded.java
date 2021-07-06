package com.elf.remote.view.view_recorded;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.MediaController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

import com.arthenica.mobileffmpeg.FFmpeg;
import com.arthenica.mobileffmpeg.FFprobe;
import com.arthenica.mobileffmpeg.MediaInformation;
import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.ActivityViewRecordedBinding;
import com.elf.remote.SubCall;
import com.elf.remote.utils.LongPressRepeatListener;
import com.elf.remote.utils.RecycleUtils;
import com.elf.remote.view.my_recorded.MyRecorded;
import com.elf.remote.viewmodel.view_recorded.ViewRecordedViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

@SuppressLint({"DefaultLocale", "SetTextI18n", "ClickableViewAccessibility"})
public class ViewRecorded extends AppCompatActivity implements SubCall {
    public static Activity activity;
    private static boolean i = false;
    private static SharedPreferences preferences;
    private myT mm;
    private double sinkSec = 0;
    public Context context;

    String Fname1, path1;

    int sin = 0, kind = 0;

    ActivityViewRecordedBinding activityViewRBinding;
    MediaController mediaController;

    private ProgressDialog progressBar2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        activityViewRBinding = DataBindingUtil.setContentView(this, R.layout.activity_view_recorded);
        activityViewRBinding.setViewModel(new ViewRecordedViewModel(this));
        activityViewRBinding.executePendingBindings();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        threeCall();
        fourCall();

        Intent intent = getIntent();
        Fname1 = intent.getStringExtra("newFname");
        path1 = intent.getStringExtra("Path");
        kind = intent.getIntExtra("kind", 0);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        i = preferences.getBoolean("hides", false);

        if (i) {
            activityViewRBinding.hideBtn.setBackgroundResource(R.drawable.click_gm_advie);
            activityViewRBinding.minusBtn.setVisibility(View.INVISIBLE);
            activityViewRBinding.plusBtn.setVisibility(View.INVISIBLE);
            activityViewRBinding.sinkTv.setVisibility(View.INVISIBLE);
        } else {
            activityViewRBinding.hideBtn.setBackgroundResource(R.drawable.click_gm_adhid);
            activityViewRBinding.minusBtn.setVisibility(View.VISIBLE);
            activityViewRBinding.plusBtn.setVisibility(View.VISIBLE);
            activityViewRBinding.sinkTv.setVisibility(View.VISIBLE);
        }

        if (path1 != null && Fname1 != null) {
            AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            String command = "-y -i '" + path1 + "' -c copy " + getVideoWithSubtitlesFile().getAbsolutePath();
            FFmpeg.executeAsync(command, (executionId, returnCode) -> {
            });
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                    (int) (mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING) * 0.30),
                    AudioManager.FLAG_PLAY_SOUND);
            sinkSec = 0.0;
            sin = 0;
            activityViewRBinding.videoTxt.setText("현재영상:" + Fname1);
            activityViewRBinding.vv.setVisibility(View.VISIBLE);
            activityViewRBinding.sinkTv.setText("오디오 0.025초 (" + String.format("%.3f", sinkSec) + ")");
            if (kind == 0) {
                activityViewRBinding.vv.setVideoURI(Uri.fromFile(new File(path1)));
                activityViewRBinding.vv.seekTo(1);
            } else {
                invisible();
                closeCall();
            }
        }
    }

    @BindingAdapter({"viewRMessage"})
    public static void hideMe(View view, String message) {
        if (message == "hide") {
            if (i) {
                view.findViewById(R.id.hide_btn).setBackgroundResource(R.drawable.click_gm_adhid);
                view.findViewById(R.id.minus_btn).setVisibility(View.VISIBLE);
                view.findViewById(R.id.plus_btn).setVisibility(View.VISIBLE);
                view.findViewById(R.id.sink_tv).setVisibility(View.VISIBLE);
                i = false;

                SharedPreferences.Editor editor = preferences.edit();

                editor.putBoolean("hides", false);
                editor.apply();
            } else {
                view.findViewById(R.id.hide_btn).setBackgroundResource(R.drawable.click_gm_advie);
                view.findViewById(R.id.minus_btn).setVisibility(View.INVISIBLE);
                view.findViewById(R.id.plus_btn).setVisibility(View.INVISIBLE);
                view.findViewById(R.id.sink_tv).setVisibility(View.INVISIBLE);
                i = true;

                SharedPreferences.Editor editor = preferences.edit();

                editor.putBoolean("hides", true);
                editor.apply();
            }
        }
    }

    @Override
    public void closeCall() {
        if (path1 != null) {
            if (mm != null) {
                mm.cancel();
                mm.onFinish();
            }
            File videoWithSubtitlesFile = getVideoWithSubtitlesFile();
            File videoFIle = getVideoFile();
            File audioFile = getAudioFile();
            File audioFile2 = getAudioFile2();
            File mergeFile = getMergeFile();
            String command, time = null;

            if (!activityViewRBinding.vv.isPlaying()) {
                if (sinkSec != 0 && sin == 1) {
                    showProgressDialog();
                    MediaInformation info = FFprobe.getMediaInformation(path1);
                    JSONObject jsonObject = info.getAllProperties();
                    try {
                        JSONObject obj = jsonObject.getJSONObject("format");
                        time = obj.getString("duration");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (sinkSec < 0) {
                        command = "-y -i '" + path1 + "' -map 0:a:0 -acodec copy -ss " + (sinkSec * -1) + " " + audioFile2.getAbsolutePath();
                        FFmpeg.execute(command);
                        command = "-y -ss 0 -i '" + path1 + "' -t " + time + " -i " + audioFile2.getAbsolutePath() + " -c:v copy -c:a copy -strict experimental -map 0:v:0 -map 1:a:0 " + videoWithSubtitlesFile.getAbsolutePath();
                        FfmpegEx(command);
                    } else {
                        command = "-y -f lavfi -i anullsrc=channel_layout=5.1:sample_rate=48000 -t " + sinkSec + " " + videoFIle.getAbsolutePath();
                        FFmpeg.execute(command);
                        command = "-y -i '" + path1 + "' -vn -c:a libmp3lame " + audioFile.getAbsolutePath();
                        String finalTime = time;
                        FFmpeg.executeAsync(command, (executionId, returnCode) -> {
                            String command2 = "-y -i 'concat:" + videoFIle.getAbsolutePath() + "|" + audioFile.getAbsolutePath() + "' -acodec copy " + mergeFile.getAbsolutePath();
                            FFmpeg.execute(command2);
                            command2 = "-y -ss 0 -i '" + path1 + "' -t " + finalTime + " -i " + mergeFile.getAbsolutePath() + " -c:v copy -c:a copy -strict experimental -map 0:v:0 -map 1:a:0 -shortest " + videoWithSubtitlesFile.getAbsolutePath();
                            FfmpegEx(command2);
                        });
                    }
                } else {
                    activityViewRBinding.vv.setVisibility(View.VISIBLE);
                    if (sinkSec == 0)
                        activityViewRBinding.vv.setVideoURI(Uri.fromFile(new File(path1)));
                    else
                        activityViewRBinding.vv.setVideoURI(Uri.parse("file://" + getVideoWithSubtitlesFile().getAbsolutePath()));

                    activityViewRBinding.vidStart.setBackgroundResource(R.drawable.click_rec_stop);

                    activityViewRBinding.vv.setOnPreparedListener(mediaPlayer -> {
                        mediaController = new MediaController(this) {
                            @Override
                            public boolean dispatchKeyEvent(KeyEvent event) {
                                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                                    super.hide();//Hide mediaController
                                    return true;//If press Back button, finish here
                                }
                                //If not Back button, other button (volume) work as usual.
                                return super.dispatchKeyEvent(event);
                            }

                            @Override
                            public void hide() {
                                super.hide();
                                if (activityViewRBinding.vv.isPlaying()) {
                                    invisible();
                                }
                            }

                            @Override
                            public void show() {
                                super.show();
                                if (activityViewRBinding.vv.isPlaying()) {
                                    visible();
                                }
                            }
                        };

                        activityViewRBinding.vv.setMediaController(mediaController);
                        activityViewRBinding.vv.start();
                    });
                    activityViewRBinding.vv.setOnCompletionListener(mp -> {
                        activityViewRBinding.vidStart.setBackgroundResource(R.drawable.click_gm_play);
                        enableT();
                        visible();
                    });

                    enableF();
                }
            } else {
                activityViewRBinding.vv.pause();
                enableT();
                activityViewRBinding.vidStart.setBackgroundResource(R.drawable.click_gm_play);
            }
        }
    }

    public void FfmpegEx(String command) {
        File videoFIle = getVideoFile();
        File audioFile = getAudioFile();
        File audioFile2 = getAudioFile2();
        File mergeFile = getMergeFile();

        FFmpeg.executeAsync(command, (executionId, returnCode) -> {
            activityViewRBinding.vv.setVisibility(View.VISIBLE);
            activityViewRBinding.vv.setVideoURI(Uri.parse("file://" + getVideoWithSubtitlesFile().getAbsolutePath()));
            activityViewRBinding.vidStart.setBackgroundResource(R.drawable.click_rec_stop);

            activityViewRBinding.vv.setOnPreparedListener(mediaPlayer -> {
                mediaController = new MediaController(this) {
                    @Override
                    public boolean dispatchKeyEvent(KeyEvent event) {
                        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                            super.hide();//Hide mediaController
                            return true;//If press Back button, finish here
                        }
                        //If not Back button, other button (volume) work as usual.
                        return super.dispatchKeyEvent(event);
                    }

                    @Override
                    public void hide() {
                        super.hide();
                        if (activityViewRBinding.vv.isPlaying()) {
                            invisible();
                        }
                    }

                    @Override
                    public void show() {
                        super.show();
                        if (activityViewRBinding.vv.isPlaying()) {
                            visible();
                        }
                    }
                };

                activityViewRBinding.vv.setMediaController(mediaController);
                activityViewRBinding.vv.start();
            });
            activityViewRBinding.vv.setOnCompletionListener(mp -> {
                activityViewRBinding.vidStart.setBackgroundResource(R.drawable.click_gm_play);
                enableT();
                visible();
            });

            enableF();

            sin = 0;
            hideProgressDialog();
            mergeFile.delete();
            audioFile.delete();
            videoFIle.delete();
            audioFile2.delete();
        });
    }

    @Override
    public void oneCall() {
        File videoWithSubtitlesFile = getVideoWithSubtitlesFile();
        MovSaveFragment save = MovSaveFragment.getInstance();
        if (Fname1 != null && path1 != null) {
            int x = Fname1.indexOf(".");
            Bundle bundle = new Bundle();
            bundle.putString("text", path1);
            bundle.putString("path2", videoWithSubtitlesFile.getAbsolutePath());
            bundle.putString("Fname", Fname1.substring(0, x));
            save.setArguments(bundle);
            save.show(getSupportFragmentManager(), "saveDialog");
        }
    }

    @Override
    public void twoCall() {
        if (kind == 1) {
            Intent intent = new Intent(this, MovListFragment.class);
            intent.putExtra("kind", 1);
            startActivity(intent);
            activity = ViewRecorded.this;
        } else {
            finish();
        }
    }

    @Override
    public void threeCall() {
        activityViewRBinding.minusBtn.setOnTouchListener(new LongPressRepeatListener(300, 50, view -> {
            if (path1 != null) {
                if (mm != null) mm.cancel();
                activityViewRBinding.sinkImg.setBackgroundResource(R.drawable.gmovaudleft);
                activityViewRBinding.sinkImg.setVisibility(View.VISIBLE);
                mm = new myT(4000, 1000);

                sinkSec = sinkSec - 0.025;
                sinkSec = Double.parseDouble(String.format("%.3f", sinkSec));
                activityViewRBinding.sinkTv.setText("오디오 0.025초 (" + String.format("%.3f", sinkSec) + ")");
                sin = 1;

                mm.start();
            }
        }));
    }

    @Override
    public void fourCall() {
        activityViewRBinding.plusBtn.setOnTouchListener(new LongPressRepeatListener(300, 50, view -> {
            if (path1 != null) {
                if (mm != null) mm.cancel();
                activityViewRBinding.sinkImg.setBackgroundResource(R.drawable.gmovaudright);
                activityViewRBinding.sinkImg.setVisibility(View.VISIBLE);
                mm = new myT(4000, 1000);

                sinkSec = sinkSec + 0.025;
                sinkSec = Double.parseDouble(String.format("%.03f", sinkSec));
                activityViewRBinding.sinkTv.setText("오디오 0.025초 (" + String.format("%.3f", sinkSec) + ")");
                sin = 1;

                mm.start();
            }
        }));
    }

    class myT extends CountDownTimer {

        public myT(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
        }

        @Override
        public void onFinish() {
            activityViewRBinding.sinkImg.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (activityViewRBinding.vv.isPlaying()) activityViewRBinding.vv.pause();
    }

    @Override
    protected void onDestroy() {
        RecycleUtils.recursiveRecycle(getWindow().getDecorView());
        super.onDestroy();

        activityViewRBinding.vv.stopPlayback();
    }

    public File getVideoWithSubtitlesFile() {
        return new File(this.getFilesDir(), "sync.mp4");
    }

    public File getVideoFile() {
        return new File(this.getFilesDir(), "silence.mp3");
    }

    public File getAudioFile() {
        return new File(this.getFilesDir(), "audio.mp3");
    }

    public File getAudioFile2() {
        return new File(this.getFilesDir(), "audio.mp4");
    }

    public File getMergeFile() {
        return new File(this.getFilesDir(), "merge.ts");
    }

    protected void showProgressDialog() {
        progressBar2 = new ProgressDialog(this);
        progressBar2.setMessage("영상 생성중...");
        progressBar2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar2.setIndeterminate(true);
        progressBar2.setCancelable(true);

        progressBar2.show();
    }

    protected void hideProgressDialog() {
        progressBar2.dismiss();
    }

    void visible() {
        if (!i) {
            activityViewRBinding.plusBtn.setVisibility(View.VISIBLE);
            activityViewRBinding.minusBtn.setVisibility(View.VISIBLE);
            activityViewRBinding.sinkTv.setVisibility(View.VISIBLE);
        }

        activityViewRBinding.hideBtn.setVisibility(View.VISIBLE);
        activityViewRBinding.listBtn.setVisibility(View.VISIBLE);
        activityViewRBinding.saveVideo.setVisibility(View.VISIBLE);
        activityViewRBinding.videoTxt.setVisibility(View.VISIBLE);
        activityViewRBinding.vidStart.setVisibility(View.VISIBLE);
    }

    void invisible() {
        activityViewRBinding.plusBtn.setVisibility(View.INVISIBLE);
        activityViewRBinding.minusBtn.setVisibility(View.INVISIBLE);
        activityViewRBinding.hideBtn.setVisibility(View.INVISIBLE);

        activityViewRBinding.listBtn.setVisibility(View.INVISIBLE);
        activityViewRBinding.saveVideo.setVisibility(View.INVISIBLE);
        activityViewRBinding.sinkTv.setVisibility(View.INVISIBLE);
        activityViewRBinding.videoTxt.setVisibility(View.INVISIBLE);
        activityViewRBinding.vidStart.setVisibility(View.INVISIBLE);
    }

    void enableT() {
        activityViewRBinding.minusBtn.setEnabled(true);
        activityViewRBinding.plusBtn.setEnabled(true);
        activityViewRBinding.saveVideo.setEnabled(true);
    }

    void enableF() {
        activityViewRBinding.minusBtn.setEnabled(false);
        activityViewRBinding.plusBtn.setEnabled(false);
        activityViewRBinding.saveVideo.setEnabled(false);
    }

    @Override
    public void fiveCall() {

    }

    @Override
    public void sixCall() {

    }

    @Override
    public void sevenCall() {

    }

    @Override
    public void eightCall() {

    }

    @Override
    public void onBackPressed() {
        if (kind != 0) {
            Intent intent = new Intent(this, MyRecorded.class);
            startActivity(intent);
        }
        finish();
    }
}