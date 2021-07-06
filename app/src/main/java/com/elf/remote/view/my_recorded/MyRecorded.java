package com.elf.remote.view.my_recorded;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.ActivityMyRecordedBinding;
import com.elf.remote.Application;
import com.elf.remote.CallActivity;
import com.elf.remote.model.bluetooth.BluetoothCon;
import com.elf.remote.model.data.VerSionMachin;
import com.elf.remote.model.usb.UsbCon;
import com.elf.remote.utils.RecycleUtils;
import com.elf.remote.view.SlidePager;
import com.elf.remote.view.remotecon.RemoteController;
import com.elf.remote.view.search.SearchMusic;
import com.elf.remote.viewmodel.my_recorded.MyRecordedViewModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MyRecorded extends AppCompatActivity implements CallActivity, View.OnClickListener {
    public static Activity myRecorded;

    private static final String DETAIL_PATH = "ElfData/";
    private String UsbName = "";

    ActivityMyRecordedBinding binding;
    int Time = 2000;
    public int Stop = 2;
    int ssId = 0;
    boolean pause = false;

    // 카메라 전면, 후면
    private static final String CAM_FRONT = "1";
    private static final String CAM_REAR = "0";

    public static String mCamId;

    private String mGaId;

    CameraCaptureSession mCameraCaptureSession;
    CameraDevice mCameraDevice;
    CameraManager mCameraManager;

    Size mVideoSize;
    Size mPreviewSize;
    CaptureRequest.Builder mCaptureRequestBuilder;

    int mSensorOrientation;

    Semaphore mSemaphore = new Semaphore(1);

    HandlerThread mBackgroundThread;
    Handler mBackgroundHandler;

    MediaRecorder mMediaRecorder;

    private String mNextVideoAbsolutePath;
    public boolean mIsRecordingVideo;
    public boolean recRdy;
    public boolean countDown;
    public int autoSet = 0;
    myT mm;
    myT2 mm2;

    int[] aa = {0, R.drawable.cnt0, R.drawable.cnt1,
            R.drawable.cnt2, R.drawable.cnt3,
            R.drawable.cnt4, R.drawable.cnt5,
            R.drawable.cnt6, R.drawable.cnt7,
            R.drawable.cnt8, R.drawable.cnt9};

    BluetoothCon bluetoothCon = new BluetoothCon(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_recorded);
        binding.setViewModel(new MyRecordedViewModel(this));
        binding.executePendingBindings();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        myRecorded = this;

        mCamId = CAM_REAR;
        mGaId = "0";

        mSensorOrientation = 90;

        binding.pictureBtn.setOnClickListener(this);
        binding.switchImgBtn.setOnClickListener(this);
        binding.recGaBtn.setOnClickListener(this);
        binding.songPlay.setOnClickListener(this);
        binding.songStop.setOnClickListener(this);
        binding.recAutoBtn.setOnClickListener(this);

        File f = new File(getVideoFilePath());
        if (f.exists()) {
            dialog(f);
        }

        Intent intent;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String namePre = "RecTip";
        String timePre = "timerTip";
        String autoPre = "autoSet";
        String str = preferences.getString(namePre, "0");
        int str2 = preferences.getInt(timePre, 2000);

        String strPre = "0";
        if (str.equals(strPre)) {
            intent = new Intent(this, SlidePager.class);
            intent.putExtra("kind", 3);
            startActivity(intent);
        }

        if (str2 == 3000) {
            Time = str2;
            binding.recTimerBtn.setBackgroundResource(R.drawable.click_rec_timer2);
        } else if (str2 == 6000) {
            Time = str2;
            binding.recTimerBtn.setBackgroundResource(R.drawable.click_rec_timer5);
        } else if (str2 == 11000) {
            Time = str2;
            binding.recTimerBtn.setBackgroundResource(R.drawable.click_rec_timer10);
        } else {
            Time = 2000;
            binding.recTimerBtn.setBackgroundResource(R.drawable.click_rec_timer0);
        }

        if (VerSionMachin.getName().equals("G10")) {
            binding.songPlay.setVisibility(View.GONE);
            binding.songStop.setVisibility(View.GONE);
            autoSet = 0;
        } else {
            binding.songPlay.setVisibility(View.VISIBLE);
            binding.songStop.setVisibility(View.VISIBLE);

            autoSet = preferences.getInt(autoPre, 0);

            if (autoSet == 0) {
                binding.recAutoBtn.setBackgroundResource(R.drawable.click_rec_auto);
            } else if (autoSet == 1) {
                binding.recAutoBtn.setBackgroundResource(R.drawable.click_rec_auto2);
            } else {
                binding.recAutoBtn.setBackgroundResource(R.drawable.click_rec_manual);
            }
        }

        binding.running.setVisibility(View.INVISIBLE);

        bluetoothCon.readBluetooth(this);

        VerSionMachin.setStop(Stop);

        getUsbName();
    }

    public void getUsbName() {
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        for (UsbDevice usbDevice : deviceList.values()) {
            UsbName = usbDevice.getProductName();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        startBackgroundThread();
        if (binding.preview.isAvailable()) {
            if (!mIsRecordingVideo)
                openCamera(binding.preview.getWidth(), binding.preview.getHeight());
        } else {
            binding.preview.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    private final TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
            openCamera(binding.preview.getWidth(), binding.preview.getHeight());
        }

        @Override
        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
            if (!mIsRecordingVideo) configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

        }
    };


    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            mCameraDevice = camera;
            startPreview();
            mSemaphore.release();
            configureTransform(binding.preview.getWidth(), binding.preview.getHeight());
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            if (!mIsRecordingVideo) {
                mSemaphore.release();
                camera.close();
                mCameraDevice = null;
            }
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            mSemaphore.release();
            camera.close();
            mCameraDevice = null;
            finish();
        }
    };


    //카메라 기능 호출
    private void openCamera(int width, int height) {
        mMediaRecorder = new MediaRecorder();
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            if (!mSemaphore.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            CameraCharacteristics cc = mCameraManager.getCameraCharacteristics(mCamId);
            StreamConfigurationMap scm = cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            if (scm == null) {
                throw new RuntimeException("Cannot get available preview/video sizes");
            }

            mVideoSize = chooseVideoSize(scm.getOutputSizes(MediaRecorder.class));
            mPreviewSize = chooseOptimalSize(scm.getOutputSizes(SurfaceTexture.class), width, height, mVideoSize);

            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                binding.preview.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            } else {
                binding.preview.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
            }

            configureTransform(width, height);
            mCameraManager.openCamera(mCamId, mStateCallback, mBackgroundHandler);
        } catch (CameraAccessException | SecurityException | NullPointerException | InterruptedException e) {
            e.printStackTrace();
            finish();
        }

    }

    //녹화되는 비디오 해상도
    private static Size chooseVideoSize(Size[] choices) {
        for (Size size : choices) {
            if (size.getWidth() == size.getHeight() * 16 / 9 && size.getWidth() <= 1920) {
                return size;
            }
        }
        return choices[choices.length - 1];
    }

    //사용자에게 보이는 실제 프리뷰 해상도
    private static Size chooseOptimalSize(Size[] choices, int width, int height, Size aspectRatio) {
        List<Size> bigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size ops : choices) {
            if (ops.getHeight() == ops.getWidth() * h / w && ops.getWidth() >= width && ops.getHeight() >= height) {
                bigEnough.add(ops);
            }
        }

        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            return choices[0];
        }
    }


    // 카메라 닫기
    private void closeCamera() {
        try {
            mSemaphore.acquire();
            closePreviewSession();
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mMediaRecorder) {
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } finally {
            mSemaphore.release();
        }
    }

    //미리보기 기능
    public void startPreview() {
        if (null == mCameraDevice || !binding.preview.isAvailable() || null == mPreviewSize) {
            return;
        }
        try {
            recRdy = false;
            mm2 = new myT2(2000, 1000);
            mm2.start();
            closePreviewSession();
            SurfaceTexture texture = binding.preview.getSurfaceTexture();
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

            Surface previewSurface = new Surface(texture);
            mCaptureRequestBuilder.addTarget(previewSurface);
            mCameraDevice.createCaptureSession(Collections.singletonList(previewSurface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    mCameraCaptureSession = session;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void updatePreview() {
        if (null == mCameraDevice) {
            return;
        }
        try {
            setUpCaptureRequestBuilder(mCaptureRequestBuilder);
            HandlerThread thread = new HandlerThread("CameraPreview");
            thread.start();
            mCameraCaptureSession.setRepeatingRequest(mCaptureRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void setUpCaptureRequestBuilder(CaptureRequest.Builder builder) {
        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
    }


    private void configureTransform(int viewWidth, int viewHeight) {
        Activity activity = this;
        if (null == mPreviewSize) {
            return;
        }
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getWidth(), mPreviewSize.getHeight());

        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();

        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth()
            );
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        activity.runOnUiThread(() -> binding.preview.setTransform(matrix));
    }

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closePreviewSession() {
        if (mCameraCaptureSession != null) {
            mCameraCaptureSession.close();
            mCameraCaptureSession = null;
        }
    }

    private static final SparseIntArray DEFAULT_ORIENTATIONS = new SparseIntArray();
    private static final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();

    static {
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_0, 90);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_90, 0);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_180, 270);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    static {
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0);
    }

    //영상녹화 설정
    private void setUpMediaRecorder() throws IOException {
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        if (mNextVideoAbsolutePath == null || mNextVideoAbsolutePath.isEmpty()) {
            mNextVideoAbsolutePath = getVideoFilePath();
        }
        mMediaRecorder.setOutputFile(mNextVideoAbsolutePath);
        mMediaRecorder.setVideoEncodingBitRate(10000000);
        mMediaRecorder.setVideoFrameRate(60);
        mMediaRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        switch (mSensorOrientation) {
            case 90:
                mMediaRecorder.setOrientationHint(DEFAULT_ORIENTATIONS.get(0));
                break;
            case 180:
                mMediaRecorder.setOrientationHint(INVERSE_ORIENTATIONS.get(90));
                break;
        }
        mMediaRecorder.prepare();
    }

    //영상녹화 설정
    private void setUpMediaRecorderC() throws IOException {
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        if (mNextVideoAbsolutePath == null || mNextVideoAbsolutePath.isEmpty()) {
            mNextVideoAbsolutePath = getVideoFilePath();
        }
        mMediaRecorder.setOutputFile(mNextVideoAbsolutePath);
        mMediaRecorder.setVideoEncodingBitRate(10000000);

        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        switch (mSensorOrientation) {
            case 90:
                mMediaRecorder.setOrientationHint(DEFAULT_ORIENTATIONS.get(0));
                break;
            case 180:
                mMediaRecorder.setOrientationHint(INVERSE_ORIENTATIONS.get(90));
                break;
        }
        mMediaRecorder.prepare();
    }

    //파일 이름 및 저장경로를 만듭니다.
    private String getVideoFilePath() {
        String path;

        final File dir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        path = dir.getPath() + "/" + DETAIL_PATH;
        File dst = new File(path);
        if (!dst.exists()) dst.mkdirs();

        return path + "myRec.mp4";
    }

    //녹화시작
    @SuppressLint("UseCompatLoadingForDrawables")
    private void startRecordingVideo() {
        Stop = 0;
        VerSionMachin.setStop(Stop);
        if (null == mCameraDevice || !binding.preview.isAvailable() || null == mPreviewSize) {
            return;
        }

        try {
            closePreviewSession();
            if (UsbName.equals("ELF Digital Audio Bridge")) setUpMediaRecorderC();
            else setUpMediaRecorder();
            SurfaceTexture texture = binding.preview.getSurfaceTexture();
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);

            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

            List<Surface> surfaces = new ArrayList<>();

            Surface previewSurface = new Surface(texture);
            surfaces.add(previewSurface);
            mCaptureRequestBuilder.addTarget(previewSurface);

            Surface recordSurface = mMediaRecorder.getSurface();
            surfaces.add(recordSurface);
            mCaptureRequestBuilder.addTarget(recordSurface);

            mCameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    mCameraCaptureSession = session;
                    updatePreview();
                    runOnUiThread(() -> {
                        mIsRecordingVideo = true;
                        mMediaRecorder.start();
                    });
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                }
            }, mBackgroundHandler);
            timer();
            binding.running.setVisibility(View.VISIBLE);
        } catch (CameraAccessException | IOException e) {
            e.printStackTrace();
        }
    }

    //녹화 중지
    public void stopRecordingVideo() {
        Stop = 1;

        RemoteController remoteController = (RemoteController) RemoteController.remoteController;
        if (RemoteController.remoteController != null) {
            remoteController.finish();
        }

        VerSionMachin.setStop(Stop);
        bluetoothCon.commandBluetooth("SONGSTOP\n");
        if (!VerSionMachin.getName().equals("G10") && !UsbName.equals("ELF Digital Audio Bridge")) {
            bluetoothCon.commandBluetooth("CAUDIO_STOP\n");
            VerSionMachin.setRdy(false);
        } else if (!VerSionMachin.getName().equals("G10")) {
            VerSionMachin.setRdy(false);
        } else if (UsbName.equals("ELF Digital Audio Bridge")) {
            bluetoothCon.commandBluetooth("USBMIC_STOP\n");
        }

        binding.pictureBtn.setBackgroundResource(R.drawable.click_rec_start);
        ssId = 0;
        binding.songPlay.setBackgroundResource(R.drawable.click_rec_sstart);
        binding.switchImgBtn.setEnabled(true);
        binding.recSearchBtn.setEnabled(true);
        binding.recGaBtn.setEnabled(true);
        binding.running.setVisibility(View.INVISIBLE);

        mIsRecordingVideo = false;
        countDown = false;
        pause = false;
        mMediaRecorder.stop();
        mMediaRecorder.reset();

        File file = new File(mNextVideoAbsolutePath);

        Application.applicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

        if (remoteController != null) {
            new Thread(new Runnable() {
                boolean what = true;

                @Override
                public void run() {
                    while (what) {
                        if (remoteController.isDestroyed()) {
                            if (UsbName.equals("ELF Digital Audio Bridge")) {
                                UsbCon usbCon;
                                usbCon = new UsbCon(MyRecorded.this, new IntentFilter(), null, mNextVideoAbsolutePath, MyRecorded.this);
                                usbCon.AttachedCType();
                            } else {
                                RecOkFragment rk = RecOkFragment.getInstance();
                                Bundle bundle = new Bundle();
                                rk.readVideo(MyRecorded.this);
                                bundle.putString("mov", mNextVideoAbsolutePath);
                                rk.setArguments(bundle);
                                rk.show(getSupportFragmentManager(), "DIALOG");
                                mNextVideoAbsolutePath = null;
                            }
                            what = false;
                        }
                    }
                }
            }).start();
        } else {
            if (UsbName.equals("ELF Digital Audio Bridge")) {
                UsbCon usbCon;
                usbCon = new UsbCon(this, new IntentFilter(), null, mNextVideoAbsolutePath, this);
                usbCon.AttachedCType();
            } else {
                RecOkFragment rk = RecOkFragment.getInstance();
                Bundle bundle = new Bundle();
                rk.readVideo(this);
                bundle.putString("mov", mNextVideoAbsolutePath);
                rk.setArguments(bundle);
                rk.show(getSupportFragmentManager(), "DIALOG");
                mNextVideoAbsolutePath = null;
            }
        }

        stop();
    }

    //카메라 전, 후, 광각 변경
    // 본인 카메라에 맞게 적용하면 됨.
    @Override
    public void onClick(View v) {
        int id = v.getId();
        LinearLayout.LayoutParams plc;
        if (id == R.id.pictureBtn) {
            if (mIsRecordingVideo && countDown) {
                stopRecordingVideo();
            } else if (!mIsRecordingVideo && recRdy) {
                getUsbName();
                mm = new myT(Time, 1000);
                binding.recCnt.setVisibility(View.VISIBLE);
                binding.pictureBtn.setBackgroundResource(R.drawable.click_rec_stop);
                binding.switchImgBtn.setEnabled(false);
                binding.recSearchBtn.setEnabled(false);
                binding.recGaBtn.setEnabled(false);
                mm.start();
            }
        } else if (id == R.id.switchImgBtn) {
            switch (mCamId) {
                case CAM_REAR:
                    mCamId = CAM_FRONT;
                    break;
                case CAM_FRONT:
                    mCamId = CAM_REAR;
                    break;
            }
            closeCamera();
            openCamera(binding.preview.getWidth(), binding.preview.getHeight());
        } else if (id == R.id.rec_ga_btn) {
            switch (mGaId) {
                case "0":
                    binding.recGaBtn.setBackgroundResource(R.drawable.click_rec_cla);
                    binding.recGaBtn.setRotation(90);
                    binding.recSearchBtn.setRotation(90);
                    binding.recTimerBtn.setRotation(90);
                    binding.recRemoteBtn.setRotation(90);
                    binding.switchImgBtn.setRotation(90);
                    binding.recAutoBtn.setRotation(90);
                    binding.recordTimeTxtView.setRotation(90);
                    binding.songPlay.setRotation(90);
                    binding.songStop.setRotation(90);
                    binding.running.setRotation(90);
                    binding.recCnt.setRotation(90);
                    binding.recordTimeTxtView.setText("00:00/\n15:00");
                    plc = (LinearLayout.LayoutParams) binding.recordTimeTxtView.getLayoutParams();
                    plc.setMargins(0, 0, 0, 0);
                    binding.recordTimeTxtView.setLayoutParams(plc);
                    mSensorOrientation = 180;
                    mGaId = "1";
                    break;
                case "1":
                    binding.recGaBtn.setBackgroundResource(R.drawable.click_rec_ptr);
                    binding.recGaBtn.setRotation(0);
                    binding.recSearchBtn.setRotation(0);
                    binding.recTimerBtn.setRotation(0);
                    binding.recRemoteBtn.setRotation(0);
                    binding.switchImgBtn.setRotation(0);
                    binding.recAutoBtn.setRotation(0);
                    binding.recordTimeTxtView.setRotation(0);
                    binding.songPlay.setRotation(0);
                    binding.songStop.setRotation(0);
                    binding.running.setRotation(0);
                    binding.recCnt.setRotation(0);
                    binding.recordTimeTxtView.setText(R.string.time);
                    plc = (LinearLayout.LayoutParams) binding.recordTimeTxtView.getLayoutParams();
                    plc.setMargins(20, 20, 20, 20);
                    binding.recordTimeTxtView.setLayoutParams(plc);
                    mSensorOrientation = 90;
                    mGaId = "0";
                    break;
            }
        } else if (id == R.id.songPlay) {
            if (Stop == 0) {
                if (ssId == 0) {
                    ssId = 1;
                    binding.songPlay.setBackgroundResource(R.drawable.click_rec_pause);

                    if (pause) {
                        bluetoothCon.commandBluetooth("SNGRESTART\n");
                    } else {
                        bluetoothCon.commandBluetooth("SONGSTART\n");
                    }
                } else {
                    ssId = 0;
                    binding.songPlay.setBackgroundResource(R.drawable.click_rec_sstart);
                    bluetoothCon.commandBluetooth("SNGPAUSE\n");
                    pause = true;
                }
            } else {
                Toast.makeText(this, "녹화 상태에서만 작동합니다.", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.songStop) {
            if (Stop == 0) {
                ssId = 0;
                binding.songPlay.setBackgroundResource(R.drawable.click_rec_sstart);
                bluetoothCon.commandBluetooth("SONGSTOP\n");
                pause = false;
            } else {
                Toast.makeText(this, "녹화 상태에서만 작동합니다.", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.rec_auto_btn) {
            if (!VerSionMachin.getName().equals("G10")) {
                SharedPreferences preferences;
                preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                String autoPre = "autoSet";

                if (autoSet == 2) {
                    AutoPlayFragment auto = new AutoPlayFragment(this);
                    auto.callFunction();
                    auto.setDialogR(result -> {
                        autoSet = result;
                        editor.putInt(autoPre, autoSet);
                        editor.apply();
                        if (autoSet == 0) {
                            binding.recAutoBtn.setBackgroundResource(R.drawable.click_rec_auto);
                        } else {
                            binding.recAutoBtn.setBackgroundResource(R.drawable.click_rec_auto2);
                        }
                    });
                } else {
                    binding.recAutoBtn.setBackgroundResource(R.drawable.click_rec_manual);
                    autoSet = 2;
                    editor.putInt(autoPre, autoSet);
                    editor.apply();
                }
            }
        }
    }

    @Override
    public void callActivity() {
        Intent intent = new Intent(this, RemoteController.class);
        intent.putExtra("btnOn", 1);
        startActivity(intent);
    }

    @Override
    public void exitActivity() {
        Intent intent = new Intent(this, SearchMusic.class);
        intent.putExtra("record", 1);
        startActivity(intent);
    }

    @Override
    public void callDialog() {
        timerFragment t = timerFragment.getInstance();
        t.show(getSupportFragmentManager(), "DIALOG");
        t.setDialogR(result -> {
            Time = result;
            if (Time == 3000)
                binding.recTimerBtn.setBackgroundResource(R.drawable.click_rec_timer2);
            else if (Time == 6000)
                binding.recTimerBtn.setBackgroundResource(R.drawable.click_rec_timer5);
            else if (Time == 11000)
                binding.recTimerBtn.setBackgroundResource(R.drawable.click_rec_timer10);
            else {
                Time = 2000;
                binding.recTimerBtn.setBackgroundResource(R.drawable.click_rec_timer0);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mIsRecordingVideo) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("종료 하시겠습니까?");
            builder.setNegativeButton("아니오",
                    (dialog, which) -> {
                    });
            builder.setPositiveButton("예",
                    (dialog, which) -> stopRecordingVideo());
            builder.show();
        } else {
            super.onBackPressed();
        }
    }

    static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() - (long) rhs.getWidth() * rhs.getHeight());
        }
    }


    private void timer() {
        mCompositeDisposable = new CompositeDisposable();
        Observable<Long> duration = Observable.interval(1, TimeUnit.SECONDS)
                .map(sec -> sec += 1);
        Disposable disposable = duration.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(timeout -> {
                    long min = timeout / 60;
                    long sec = timeout % 60;
                    String sMin;
                    String sSec;
                    if (min < 10) sMin = "0" + min;
                    else sMin = String.valueOf(min);

                    if (sec < 10) sSec = "0" + sec;
                    else sSec = String.valueOf(sec);
                    String elapseTime;
                    if (mSensorOrientation == 90) elapseTime = sMin + ":" + sSec + "/15:00";
                    else elapseTime = sMin + ":" + sSec + "/\n15:00";
                    binding.recordTimeTxtView.setText(elapseTime);
                    if (sMin.equals("15")) stopRecordingVideo();
                });
        mCompositeDisposable.add(disposable);
    }

    //녹화시간 카운트 정지
    @SuppressLint("SetTextI18n")
    private void stop() {
        if (!mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
            if (mSensorOrientation == 90) binding.recordTimeTxtView.setText("00:00/15:00");
            else binding.recordTimeTxtView.setText("00:00/\n15:00");
        }
    }

    CompositeDisposable mCompositeDisposable;

    class myT extends CountDownTimer {
        int i = 0;

        public myT(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            binding.recCnt.setBackgroundResource(aa[(int) l / 1000]);
            if (!VerSionMachin.isRdy() && VerSionMachin.getName().equals("G10")) {
                if (i == 0) bluetoothCon.commandBluetooth("CAUDIO_READY\n");
                else {
                    mm.onFinish();
                    mm.cancel();
                }
                i++;
            } else if (!VerSionMachin.getName().equals("G10")) {
                if (i == 0 && UsbName.equals("ELF Digital Audio Bridge"))
                    bluetoothCon.commandBluetooth("CAUDIO_END\n");
                VerSionMachin.setRdy(true);
                i++;
            }
        }

        @Override
        public void onFinish() {
            if (VerSionMachin.isRdy()) {
                if (UsbName.equals("ELF Digital Audio Bridge"))
                    bluetoothCon.commandBluetooth("USBMIC_START\n");
                else
                    bluetoothCon.commandBluetooth("CAUDIO_START\n");
                if (!VerSionMachin.getName().equals("G10")) {
                    if (autoSet == 0 || autoSet == 1) {
                        binding.songPlay.setBackgroundResource(R.drawable.click_rec_pause);
                        bluetoothCon.commandBluetooth("SONGSTART\n");
                        ssId = 1;
                    } else {
                        ssId = 0;
                    }
                }

                startRecordingVideo();
                binding.recCnt.setVisibility(View.INVISIBLE);
                countDown = true;
            } else {
                dialog2();
            }
        }
    }

    class myT2 extends CountDownTimer {

        public myT2(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
        }

        @Override
        public void onFinish() {
            if (!UsbName.equals("ELF Digital Audio Bridge"))
                bluetoothCon.commandBluetooth("CAUDIO_READY\n");
            recRdy = true;
        }
    }

    public void dialog(File f) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("이전 녹화 영상이 발견되었습니다.\n이전 녹화 영상을 합성하시겠습니까?");
        builder.setNegativeButton("취소",
                (dialog, which) -> f.delete());
        builder.setPositiveButton("확인",
                (dialog, which) -> {
                    mm2.cancel();
                    closePreviewSession();
                    bluetoothCon.commandBluetooth("CAUDIO_END\n");
                    mNextVideoAbsolutePath = f.getAbsolutePath();
                    RecOkFragment rk = RecOkFragment.getInstance();
                    rk.readVideo(this);
                    Bundle bundle = new Bundle();
                    bundle.putString("mov", mNextVideoAbsolutePath);
                    rk.setArguments(bundle);
                    rk.show(getSupportFragmentManager(), "DIALOG");
                    mNextVideoAbsolutePath = null;
                });
        builder.show();
    }

    public void dialog2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("반주기 녹음 상태를 확인해 주세요.");
        builder.setPositiveButton("확인",
                (dialog, which) -> {
                });
        builder.show();

        binding.recCnt.setVisibility(View.INVISIBLE);
        binding.pictureBtn.setBackgroundResource(R.drawable.click_rec_start);
        binding.switchImgBtn.setEnabled(true);
        binding.recSearchBtn.setEnabled(true);
        binding.recGaBtn.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        RecycleUtils.recursiveRecycle(getWindow().getDecorView());
        bluetoothCon.commandBluetooth("CAUDIO_END\n");

        if (mCompositeDisposable != null) {
            if (!mCompositeDisposable.isDisposed()) {
                mCompositeDisposable.dispose();
            }
        }

        closePreviewSession();
        closeCamera();

        stopBackgroundThread();

        Stop = 1;
        VerSionMachin.setStop(Stop);

        myRecorded = null;
        super.onDestroy();
    }
}