package com.elf.remote.view.my_recorded;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.elf.mvvmremote.R;
import com.elf.remote.Application;
import com.elf.remote.view.view_recorded.ViewRecorded;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class CustomDialog {

    private final Context context;

    public CustomDialog(Context context) {
        this.context = context;
    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction(String path, String name, MyRecorded videoFragment) {
        File file = new File(path);
        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.custom_dialog);

        // 커스텀 다이얼로그를 노출한다.
        dlg.show();

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        final EditText message = dlg.findViewById(R.id.editName);
        final Button okButton = dlg.findViewById(R.id.okButton);
        final Button cancelButton = dlg.findViewById(R.id.cancelButton);
        final Button shareButton = dlg.findViewById(R.id.shareButton);

        name = name.replace("\"", "_");
        name = name.replace("/", "_");
        message.setText(name);

        okButton.setOnClickListener(view -> {
            String asd = String.valueOf(message.getText());
            if (asd.equals("")) {
                Toast.makeText(Application.applicationContext(), "제목을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            } else {
                if (file.exists()) {
                    File oldfile = new File(path);

                    String Fname = file.getName();
                    int x = path.indexOf(Fname);
                    String newName = path.substring(0, x) + message.getText() + ".mp4";
                    String name2 = message.getText() + ".mp4";
                    String getName = String.valueOf(message.getText());
                    String getName2 = String.valueOf(message.getText());

                    File file1 = new File(newName);

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                        int i = 1;
                        File[] list = file1.getParentFile().listFiles();
                        if (list != null) {
                            for (File files : list) {
                                if (files.getName().equals(getName + ".mp4")) {
                                    getName = getName2 + " ("+i+")";
                                    i++;
                                    file1 = new File(path.substring(0,x) + getName + ".mp4");
                                }
                            }
                        }
                    }

                    try {
                        copy(oldfile, file1, name2);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // 커스텀 다이얼로그를 종료한다.
                    dlg.dismiss();
                    MovResultDialog(name2, videoFragment, file1);
                }
            }
        });
        cancelButton.setOnClickListener(view -> {
            if (file.exists()) file.delete();
            // 커스텀 다이얼로그를 종료한다.
            dlg.dismiss();
            videoFragment.startPreview();
        });
        shareButton.setOnClickListener(v -> {
            String asd = String.valueOf(message.getText());
            if (asd.equals("")) {
                Toast.makeText(Application.applicationContext(), "제목을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            } else {
                if (file.exists()) {
                    File oldfile = new File(path);

                    String Fname = file.getName();
                    int x = path.indexOf(Fname);
                    String newName = path.substring(0, x) + message.getText() + ".mp4";
                    String name2 = message.getText() + ".mp4";
                    String getName = String.valueOf(message.getText());
                    String getName2 = String.valueOf(message.getText());

                    File file12 = new File(newName);

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                        int i = 1;
                        File[] list = file12.getParentFile().listFiles();
                        if (list != null) {
                            for (File files : list) {
                                if (files.getName().equals(getName + ".mp4")) {
                                    getName = getName2 + " ("+i+")";
                                    i++;
                                    file12 = new File(path.substring(0,x) + getName + ".mp4");
                                }
                            }
                        }
                    }

                    try {
                        copy(oldfile, file12, name2);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                        test = FileProvider.getUriForFile(videoFragment.getBaseContext(), "com.sunkokremot", file12);
                    }
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("video/*");
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, test);
                    videoFragment.startActivity(Intent.createChooser(sharingIntent, ""));

                    dlg.dismiss();
                    videoFragment.startPreview();
                }
            }
        });
    }

    Uri test;
    public void copy(File src, File dst, String name) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            saveVideoFile(name, src.getAbsolutePath());
        else {
            FileInputStream inStream = new FileInputStream(src);
            FileOutputStream outStream = new FileOutputStream(dst);
            FileChannel inChannel = inStream.getChannel();
            FileChannel outChannel = outStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            inStream.close();
            outStream.close();

            src.delete();

            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(dst)));
        }
    }

    private void saveVideoFile(String name, String path) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Video.Media.TITLE, name);
            values.put(MediaStore.Video.Media.DISPLAY_NAME, name);
            values.put(MediaStore.Video.Media.MIME_TYPE, "video/*");
            values.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000);

            values.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/Elf/ElfClip");
            values.put(MediaStore.Video.Media.IS_PENDING, 1);

            ContentResolver contentResolver = Application.applicationContext().getContentResolver();
            Uri collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            Uri item = contentResolver.insert(collection, values);

            try {
                ParcelFileDescriptor pdf = contentResolver.openFileDescriptor(item, "w", null);

                if (pdf == null) {
                    Log.d("asdf", "null");
                } else {
                    FileOutputStream fos = new FileOutputStream(pdf.getFileDescriptor());

                    File imageFile = new File(path);

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

                    test=item;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void MovResultDialog(String name, MyRecorded video, File file) {
        Intent intent = new Intent(context, ViewRecorded.class);
        String subName = name;
        String tempName = subName;
        int i = 1;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            Cursor video_c = Application.applicationContext().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null);

            if (video_c != null && video_c.moveToFirst()) {
                do {
                    String path = video_c.getString(video_c.getColumnIndex("_data"));
                    String getName = path.substring(path.lastIndexOf("/") + 1, path.indexOf("."));
                    if (getName.equals(subName.substring(0, subName.indexOf(".")))) {
                        intent.putExtra("newFname", subName);
                        intent.putExtra("Path", path);
                        intent.putExtra("kind", 1);
                        tempName = subName;
                        subName = name.substring(0, name.indexOf(".")) + " (" + i + ").mp4";
                        i++;
                        video_c.moveToFirst();
                    }
                } while (video_c.moveToNext());
            }
            if (video_c != null) {
                video_c.close();
            }
        } else {
            intent.putExtra("newFname", subName);
            intent.putExtra("Path", file.getAbsolutePath());
            intent.putExtra("kind", 1);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("영상 제작 완료");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            builder.setMessage("영상이 완료되었습니다.\n" + "/Movies/Elf/ElfClip/" + tempName + "로 저장되었습니다. 영상을 확인 하시겠습니까?");
        } else {
            builder.setMessage("영상이 완료되었습니다.\n" + "/ElfData/" + tempName + "로 저장되었습니다. 영상을 확인 하시겠습니까?");
        }
        builder.setNegativeButton("아니요",
                (dialog, which) -> video.startPreview());
        builder.setPositiveButton("예",
                (dialog, which) -> {
                    video.finish();
                    context.startActivity(intent);
                });
        builder.show();
    }
}