package com.elf.remote.view.view_recorded;

import android.app.Activity;
import android.app.Dialog;
import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.FragmentMovSaveBinding;
import com.elf.remote.Application;
import com.elf.remote.CallActivity;
import com.elf.remote.viewmodel.view_recorded.MovSaveViewModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;


public class MovSaveFragment extends DialogFragment implements CallActivity {
    String path, path2, Fname;

    public MovSaveFragment() {
    }

    int kind;

    public static MovSaveFragment getInstance() {
        return new MovSaveFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentMovSaveBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mov_save, container, false);
        binding.setViewModel(new MovSaveViewModel(this));
        binding.executePendingBindings();

        Bundle bundle = getArguments();

        if (bundle != null) {
            path = bundle.getString("text");
            path2 = bundle.getString("path2");
            Fname = bundle.getString("Fname");
        }

        setCancelable(false);

        binding.SaveKind.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.ts) kind = 1;
            else if (i == R.id.at) kind = 2;
        });

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void callActivity() {
        if (kind == 1) {
            File file = new File(path2);
            File newF = new File(path);

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    saveVideoFile(newF.getName(), path2, path);
                } else {
                    copy(file, newF);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            requireActivity().getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(newF)));
            Toast.makeText(getContext(), "파일이 저장되었습니다..", Toast.LENGTH_SHORT).show();
        } else if (kind == 2) {
            MovAnotherFragment another = MovAnotherFragment.getInstance();
            Bundle bundle = new Bundle();
            bundle.putString("text", path);
            bundle.putString("path2", path2);
            bundle.putString("Fname", Fname);
            another.setArguments(bundle);
            another.show(requireActivity().getSupportFragmentManager(), "another");
            dismiss();
        }
    }

    @Override
    public void exitActivity() {
        dismiss();
    }

    @Override
    public void callDialog() {

    }

    public void copy(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();

        dismiss();
    }

    String save;
    String name;

    private void saveVideoFile(String name, String save, String path) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            this.save = save;
            this.name = name;
            ContentValues values = new ContentValues();
            values.put(MediaStore.Video.Media.TITLE, name);
            values.put(MediaStore.Video.Media.DISPLAY_NAME, name);
            values.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000);

            values.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/Elf/ElfClip");
            values.put(MediaStore.Video.Media.IS_PENDING, 1);

            ContentResolver contentResolver = Application.applicationContext().getContentResolver();

            Uri item = getUriFromPath(path);

            try {
                ParcelFileDescriptor pdf = contentResolver.openFileDescriptor(item, "w", null);

                if (pdf == null) {
                    Log.d("asdf", "null");
                } else {
                    FileOutputStream fos = new FileOutputStream(pdf.getFileDescriptor());

                    File imageFile = new File(save);

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
                    values.put(MediaStore.Video.Media.IS_PENDING, 0);

                    contentResolver.update(item, values, null, null);

                    dismiss();
                }
            } catch (RecoverableSecurityException e) {
                IntentSender intent = e.getUserAction().getActionIntent().getIntentSender();
                try {
                    startIntentSenderForResult(intent, 105, null, 0, 0, 0, null);
                } catch (IntentSender.SendIntentException sendIntentException) {
                    sendIntentException.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == 105 && resultCode == Activity.RESULT_OK) {
            saveVideoFile(name, save, path);
        }
    }

    private Uri getUriFromPath(String filePath) {
        long videoId;
        Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.ImageColumns._ID};
        Cursor cursor = Application.applicationContext().getContentResolver().query(videoUri, projection, MediaStore.Images.ImageColumns.DATA + " LIKE ?", new String[]{filePath}, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(projection[0]);
        videoId = cursor.getLong(columnIndex);

        cursor.close();
        return Uri.parse(videoUri.toString() + "/" + videoId);
    }
}