package com.elf.remote.view.view_recorded;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableArrayList;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.FragmentMovListBinding;
import com.elf.remote.Application;
import com.elf.remote.ListEdit;
import com.elf.remote.model.data.MovFile;
import com.elf.remote.view.ChgNameFragment;
import com.elf.remote.view.DelOkFragment;
import com.elf.remote.view.adapter.MovAdapter;
import com.elf.remote.viewmodel.MovFileViewModel;
import com.elf.remote.viewmodel.view_recorded.MovListViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("DefaultLocale")
public class MovListFragment extends AppCompatActivity implements ListEdit {
    private static MovAdapter myAdapter;
    MovListViewModel model;
    ArrayList<MovFile> fileData;
    ObservableArrayList<MovFileViewModel> movData;

    @SuppressLint("StaticFieldLeak")
    public static FragmentMovListBinding binding;

    Thread thread;

    int sel_tag = 0, kind = 0;

    Uri deleteUri;
    List<Uri> uris = new ArrayList<>();
    List<Integer> deleteMulInt = new ArrayList<>();
    List<String> MulPath = new ArrayList<>();
    List<String> MulResult = new ArrayList<>();

    File newfile;
    String result;
    int ss = 0;
    int ss2 = 0;

    Uri updateUri;
    ContentValues values;

    public MovListFragment() {
    }

    public static MovListFragment getInstance() {
        return new MovListFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = new MovListViewModel(this);
        binding = DataBindingUtil.setContentView(this, R.layout.fragment_mov_list);
        binding.setViewModel(model);
        binding.executePendingBindings();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        thread = new Thread(() -> runOnUiThread(() -> model.getFileList(".mp4")));

        thread.start();

        Intent intent = getIntent();
        kind = intent.getIntExtra("kind", 0);

        binding.movList.setOnItemLongClickListener((parent, view, position, id) -> {
            if (binding.movList.getChoiceMode() != AbsListView.CHOICE_MODE_MULTIPLE) {
                binding.movList.clearChoices();
                binding.movList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
                binding.movList.setSelector(R.drawable.list_sel2);
                binding.movList.setItemChecked(position, true);
                myAdapter.isChk(View.VISIBLE);
                myAdapter.notifyDataSetChanged();
                binding.mulBtn.setChecked(true);
            } else if (binding.movList.getChoiceMode() == AbsListView.CHOICE_MODE_MULTIPLE) {
                binding.movList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                myAdapter.isChk(View.GONE);
                myAdapter.notifyDataSetChanged();
                binding.movList.clearChoices();
                binding.mulBtn.setChecked(false);
            }
            return true;
        });

        binding.movList.setOnItemClickListener((parent, view, position, id) -> {
            if (binding.movList.getChoiceMode() != AbsListView.CHOICE_MODE_MULTIPLE) {
                binding.movList.setSelector(R.drawable.list_sel);
            }
        });

        binding.mulBtn.setOnClickListener(v -> {
            if (binding.movList.getChoiceMode() != AbsListView.CHOICE_MODE_MULTIPLE) {
                binding.movList.clearChoices();
                binding.movList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
                binding.movList.setSelector(R.drawable.list_sel2);
                myAdapter.isChk(View.VISIBLE);
                myAdapter.notifyDataSetChanged();
            } else if (binding.movList.getChoiceMode() == AbsListView.CHOICE_MODE_MULTIPLE) {
                binding.movList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                myAdapter.isChk(View.GONE);
                myAdapter.notifyDataSetChanged();
                binding.movList.clearChoices();
            }
        });
    }

    @BindingAdapter("app:items3")
    public static void setMovFileList(ListView listView, ObservableArrayList<MovFileViewModel> movFiles) {
        if (listView.getAdapter() == null) {
            myAdapter = new MovAdapter();
            listView.setAdapter(myAdapter);
        } else {
            myAdapter = (MovAdapter) listView.getAdapter();
        }

        myAdapter.addAll(movFiles, 2);
    }

    @Override
    public void editList() {
        if (!thread.isAlive()) {
            int count, checked;
            SparseBooleanArray sp;
            count = myAdapter.getCount();
            ChgNameFragment t = ChgNameFragment.getInstance();
            fileData = model.fileData;
            movData = model.movFiles;
            if (count > 0) {
                if (binding.movList.getChoiceMode() == AbsListView.CHOICE_MODE_SINGLE) {
                    checked = binding.movList.getCheckedItemPosition();
                    this.checked = checked;
                    if (checked > -1 && checked < count) {

                        String a = fileData.get(binding.movList.getCheckedItemPosition()).getFname();
                        int x = a.indexOf(".");
                        Bundle bundle = new Bundle();
                        bundle.putString("text", a.substring(0, x));
                        t.setArguments(bundle);
                        t.show(getSupportFragmentManager(), "DIALOG");

                        t.setDialogR(result -> {
                            this.result = result;
                            if (this.result.equals(".mp4")) {
                                Toast.makeText(this, "제목을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                            } else if (this.result.equals(" ")) {
                                Log.d("사용자 취소", "이름 바꾸기 취소");
                            } else {
                                File oldfile = new File(fileData.get(checked).getPath());

                                int number = 1;
                                int bfNum = 1;
                                if (!oldfile.getName().equals(this.result)) {
                                    for (int s = 0; s < fileData.size(); s++) {
                                        if (this.result.equals(fileData.get(s).getFname())) {
                                            if (number != 1) {
                                                this.result = this.result.substring(0, this.result.indexOf("(" + bfNum + ").mp4")) + "(" + number + ").mp4";
                                            } else {
                                                this.result = this.result.substring(0, this.result.indexOf(".mp4")) + " (" + number + ").mp4";
                                            }
                                            bfNum = number;
                                            number++;
                                            s = 0;
                                        }
                                    }
                                }

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                    newfile = new File(oldfile.getPath().substring(0, oldfile.getPath().indexOf(oldfile.getName())) + "/" + this.result);
                                } else {
                                    newfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ElfData/" + this.result);
                                }

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                    updateUri = getUriFromPath(fileData.get(checked).getPath());
                                    try {
                                        values = new ContentValues();
                                        values.put(MediaStore.Video.Media.DISPLAY_NAME, newfile.getName());
                                        Application.applicationContext().getContentResolver().update(updateUri, values, null, null);

                                        MovFile myList = new MovFile();
                                        myList.setPath(String.valueOf(newfile));
                                        myList.setPoster(fileData.get(checked).getPoster());
                                        myList.setSize(fileData.get(checked).getSize());
                                        myList.setFname(this.result);

                                        fileData.set(checked, myList);
                                        movData.set(checked, new MovFileViewModel(myList));
                                    } catch (RecoverableSecurityException e) {
                                        IntentSender intentSender = e.getUserAction().getActionIntent().getIntentSender();
                                        try {
                                            startIntentSenderForResult(intentSender, 1006,
                                                    null, 0, 0, 0, null);
                                        } catch (IntentSender.SendIntentException sendIntentException) {
                                            sendIntentException.printStackTrace();
                                        }
                                    }
                                } else {
                                    oldfile.renameTo(newfile);
                                    MovFile myList = new MovFile();
                                    myList.setPath(String.valueOf(newfile));
                                    myList.setPoster(fileData.get(checked).getPoster());
                                    myList.setSize(fileData.get(checked).getSize());
                                    myList.setFname(this.result);

                                    fileData.set(checked, myList);
                                    movData.set(checked, new MovFileViewModel(myList));
                                }

                                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(newfile)));
                                binding.movList.setSelector(R.drawable.list_sel2);
                                myAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                } else {
                    sp = binding.movList.getCheckedItemPositions();
                    int i = count - 1;
                    ss = 0;
                    ss2 = sp.size();
                    mul = 0;
                    while (i >= 0) {
                        if (sp.get(i)) {
                            t = ChgNameFragment.getInstance();
                            String a = fileData.get(i).getFname();
                            int x = a.indexOf(".");
                            Bundle bundle = new Bundle();
                            bundle.putString("text", a.substring(0, x));
                            t.setArguments(bundle);
                            t.show(getSupportFragmentManager(), "DIALOG" + i);

                            int finalI = i;
                            t.setDialogR(result -> {
                                ss++;
                                if (result.equals(".mp4")) {
                                    Toast.makeText(this, "제목을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                                } else if (result.equals(" ")) {
                                    Log.d("사용자 취소", "이름 바꾸기 취소");
                                } else {
                                    File newfile;
                                    File oldfile = new File(fileData.get(finalI).getPath());

                                    int number = 1;
                                    int bfNum = 1;
                                    if (!oldfile.getName().equals(result)) {
                                        for (int s = 0; s < fileData.size(); s++) {
                                            if (result.equals(fileData.get(s).getFname())) {
                                                if (number != 1) {
                                                    result = result.substring(0, result.indexOf("(" + bfNum + ").mp4")) + "(" + number + ").mp4";
                                                } else {
                                                    result = result.substring(0, result.indexOf(".mp4")) + " (" + number + ").mp4";
                                                }
                                                bfNum = number;
                                                number++;
                                                s = 0;
                                            }
                                        }
                                    }

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                        newfile = new File(oldfile.getPath().substring(0, oldfile.getPath().indexOf(oldfile.getName())) + "/" + result);
                                    } else {
                                        newfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ElfData/" + result);
                                    }

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                        updateUri = getUriFromPath(fileData.get(finalI).getPath());
                                        try {
                                            values = new ContentValues();
                                            values.put(MediaStore.Video.Media.DISPLAY_NAME, newfile.getName());
                                            Application.applicationContext().getContentResolver().update(updateUri, values, null, null);

                                            MovFile myList = new MovFile();
                                            myList.setPath(String.valueOf(newfile));
                                            myList.setPoster(fileData.get(finalI).getPoster());
                                            myList.setSize(fileData.get(finalI).getSize());
                                            myList.setFname(result);

                                            fileData.set(finalI, myList);
                                            movData.set(finalI, new MovFileViewModel(myList));
                                        } catch (RecoverableSecurityException e) {
                                            uris.add(updateUri);
                                            deleteMulInt.add(finalI);
                                            MulPath.add(String.valueOf(newfile));
                                            MulResult.add(result);
                                        }
                                    } else {
                                        oldfile.renameTo(newfile);
                                        MovFile myList = new MovFile();
                                        myList.setPath(String.valueOf(newfile));
                                        myList.setPoster(fileData.get(finalI).getPoster());
                                        myList.setSize(fileData.get(finalI).getSize());
                                        myList.setFname(result);

                                        fileData.set(finalI, myList);
                                        movData.set(finalI, new MovFileViewModel(myList));
                                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(newfile)));
                                    }
                                }

                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R && ss == ss2) {
                                    if (!uris.isEmpty()) {
                                        PendingIntent editPendingIntent = MediaStore.createWriteRequest(Application.applicationContext().getContentResolver(), uris);
                                        try {
                                            startIntentSenderForResult(editPendingIntent.getIntentSender(), 1007, null, 0, 0, 0, null);
                                        } catch (IntentSender.SendIntentException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                        }
                        i--;
                    }

                    myAdapter.notifyDataSetChanged();
                    if (binding.movList.getChoiceMode() == AbsListView.CHOICE_MODE_MULTIPLE) {
                        binding.movList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                        myAdapter.isChk(View.GONE);
                        myAdapter.notifyDataSetChanged();
                        binding.movList.clearChoices();
                        binding.mulBtn.setChecked(false);
                    }
                }
            }
        }
    }

    int checked = 0;
    int mul = 0;

    @Override
    public void dleList() {
        if (!thread.isAlive()) {
            int count, checked = 0, num = 0;
            SparseBooleanArray sp = null;
            count = myAdapter.getCount();
            DelOkFragment t = DelOkFragment.getInstance();
            fileData = model.fileData;
            movData = model.movFiles;

            if (count > 0) {
                if (binding.movList.getChoiceMode() == AbsListView.CHOICE_MODE_SINGLE) {
                    checked = binding.movList.getCheckedItemPosition();

                    if (checked > -1 && checked < count) {
                        Bundle bundle = new Bundle();
                        bundle.putString("text", fileData.get(binding.movList.getCheckedItemPosition()).getFname());
                        bundle.putBoolean("true", false);
                        t.setArguments(bundle);
                        t.show(getSupportFragmentManager(), "DIALOG");
                    }
                } else {
                    sp = binding.movList.getCheckedItemPositions();
                    for (int i = count - 1; i >= 0; i--) {
                        if (sp.get(i)) {
                            num++;
                        }
                    }
                    if (num != 0) {
                        Bundle bundle = new Bundle();
                        bundle.putString("text", String.valueOf(num));
                        bundle.putBoolean("true", true);
                        t.setArguments(bundle);
                        t.show(getSupportFragmentManager(), "DIALOG");
                    }
                }
            }

            this.checked = checked;
            SparseBooleanArray finalSp = sp;
            t.setDialogR(result -> {
                if (result) {
                    if (binding.movList.getChoiceMode() == AbsListView.CHOICE_MODE_SINGLE) {
                        File oldFile = new File(fileData.get(this.checked).getPath());

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            deleteUri = getUriFromPath(fileData.get(this.checked).getPath());
                            try {
                                Application.applicationContext().getContentResolver().delete(deleteUri, null, null);

                                fileData.remove(this.checked);
                                movData.remove(this.checked);
                            } catch (RecoverableSecurityException e) {
                                IntentSender intentSender = e.getUserAction().getActionIntent().getIntentSender();
                                try {
                                    startIntentSenderForResult(intentSender, 1004,
                                            null, 0, 0, 0, null);
                                } catch (IntentSender.SendIntentException sendIntentException) {
                                    sendIntentException.printStackTrace();
                                }
                            }
                        } else {
                            oldFile.delete();
                            fileData.remove(this.checked);
                            movData.remove(this.checked);
                        }
                        binding.movList.setSelector(R.drawable.list_sel2);
                    } else {
                        mul = 0;
                        for (int i = count - 1; i >= 0; i--) {
                            if (finalSp.get(i)) {
                                File oldFile = new File(fileData.get(i).getPath());
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                    deleteUri = getUriFromPath(fileData.get(i).getPath());
                                    try {
                                        Application.applicationContext().getContentResolver().delete(deleteUri, null, null);

                                        fileData.remove(i);
                                        movData.remove(i);
                                    } catch (RecoverableSecurityException e) {
                                        uris.add(deleteUri);
                                        deleteMulInt.add(i);
                                    }
                                } else {
                                    oldFile.delete();
                                    fileData.remove(i);
                                    movData.remove(i);
                                }
                            }
                        }
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                            if (!uris.isEmpty()) {
                                PendingIntent editPendingIntent = MediaStore.createWriteRequest(Application.applicationContext().getContentResolver(), uris);
                                try {
                                    startIntentSenderForResult(editPendingIntent.getIntentSender(), 1005, null, 0, 0, 0, null);
                                } catch (IntentSender.SendIntentException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    binding.movList.clearChoices();
                    myAdapter.notifyDataSetChanged();
                    if (binding.movList.getChoiceMode() == AbsListView.CHOICE_MODE_MULTIPLE) {
                        binding.movList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                        myAdapter.isChk(View.GONE);
                        myAdapter.notifyDataSetChanged();
                        binding.movList.clearChoices();
                        binding.mulBtn.setChecked(false);
                    }
                }
            });
        } else {
            Log.d("thread", "isalive");
        }
    }

    @Override
    public void exitList() {
        if (!thread.isAlive()) {
            finish();
        }
    }

    @Override
    public void chgFile() {
        if (!thread.isAlive()) {
            fileData = model.fileData;
            movData = model.movFiles;
            fileData.clear();
            movData.clear();

            if (sel_tag == 0) {
                sel_tag = 1;
                binding.chgFile.setBackgroundResource(R.drawable.click_gm_smifile);

                thread = new Thread(() -> model.getFileList(".smi"));
            } else {
                sel_tag = 0;
                binding.chgFile.setBackgroundResource(R.drawable.click_gm_file);

                thread = new Thread(() -> model.getFileList(".mp4"));
            }
            thread.start();
        }
    }

    @Override
    public void selList() {
        if (!thread.isAlive()) {
            int count, checked;
            count = myAdapter.getCount();
            fileData = model.fileData;

            if (count > 0) {
                checked = binding.movList.getCheckedItemPosition();

                if (checked > -1 && checked < count) {
                    Intent intent = new Intent(this, ViewRecorded.class);
                    intent.putExtra("Path", fileData.get(checked).getPath());
                    intent.putExtra("newFname", fileData.get(checked).getFname());
                    intent.putExtra("kind", kind);
                    startActivity(intent);
                    if (kind == 1) {
                        ViewRecorded view = (ViewRecorded) ViewRecorded.activity;
                        view.finish();
                        finish();
                    }
                }
            }
        }
    }

    @Override
    public void shareList() {
        if (!thread.isAlive()) {
            int count, checked, num = 0;
            SparseBooleanArray sp;
            count = myAdapter.getCount();
            ArrayList<Uri> Uris = new ArrayList<>();
            fileData = model.fileData;

            if (binding.movList.getChoiceMode() == AbsListView.CHOICE_MODE_SINGLE) {
                if (count > 0) {
                    checked = binding.movList.getCheckedItemPosition();

                    if (checked > -1 && checked < count) {
                        Uri uri = getUriFromPath(fileData.get(checked).getPath());
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.setType("video/*");
                        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        startActivity(Intent.createChooser(sharingIntent, ""));
                        binding.movList.setSelector(R.drawable.list_sel2);
                    }
                }
            } else {
                sp = binding.movList.getCheckedItemPositions();
                for (int i = count - 1; i >= 0; i--) {
                    if (sp.get(i)) {
                        num++;
                        Uris.add(getUriFromPath(fileData.get(i).getPath()));
                    }
                }
                if (num != 0) {
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    sharingIntent.setType("video/*");
                    sharingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, Uris);
                    startActivity(Intent.createChooser(sharingIntent, ""));
                }

                if (binding.movList.getChoiceMode() == AbsListView.CHOICE_MODE_MULTIPLE) {
                    binding.movList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                    myAdapter.isChk(View.GONE);
                    myAdapter.notifyDataSetChanged();
                    binding.movList.clearChoices();
                    binding.mulBtn.setChecked(false);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (binding.movList.getChoiceMode() == AbsListView.CHOICE_MODE_MULTIPLE) {
            binding.movList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            myAdapter.isChk(View.GONE);
            myAdapter.notifyDataSetChanged();
            binding.movList.clearChoices();
            binding.mulBtn.setChecked(false);
        } else {
            if (!thread.isAlive()) {
                finish();
            }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == 1004 && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            Application.applicationContext().getContentResolver().delete(deleteUri, null, null);
            fileData.remove(this.checked);
            movData.remove(this.checked);
        } else if (requestCode == 1005 && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            for (Uri uri : uris) {
                Application.applicationContext().getContentResolver().delete(uri, null, null);
                int chk = deleteMulInt.get(mul);
                fileData.remove(chk);
                movData.remove(chk);
                mul++;
            }
            uris = new ArrayList<>();
            deleteMulInt = new ArrayList<>();
        } else if (requestCode == 1005 && resultCode == Activity.RESULT_CANCELED) {
            uris = new ArrayList<>();
            deleteMulInt = new ArrayList<>();
        } else if (requestCode == 1006 && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            Application.applicationContext().getContentResolver().update(updateUri, values, null, null);

            MovFile myList = new MovFile();
            myList.setPath(String.valueOf(newfile));
            myList.setPoster(fileData.get(checked).getPoster());
            myList.setSize(fileData.get(checked).getSize());
            myList.setFname(result);

            fileData.set(checked, myList);
            movData.set(checked, new MovFileViewModel(myList));
        } else if (requestCode == 1007 && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            for (Uri uri : uris) {
                int chk = deleteMulInt.get(mul);
                String path = MulPath.get(mul);
                String result = MulResult.get(mul);

                File newFile = new File(path);
                values = new ContentValues();
                values.put(MediaStore.Video.Media.DISPLAY_NAME, newFile.getName());

                Application.applicationContext().getContentResolver().update(uri, values, null, null);

                MovFile myList = new MovFile();
                myList.setPath(path);
                myList.setPoster(fileData.get(chk).getPoster());
                myList.setSize(fileData.get(chk).getSize());
                myList.setFname(result);

                fileData.set(chk, myList);
                movData.set(chk, new MovFileViewModel(myList));

                mul++;
            }
            uris = new ArrayList<>();
            deleteMulInt = new ArrayList<>();
            MulPath = new ArrayList<>();
            MulResult = new ArrayList<>();
        } else if (requestCode == 1007 && resultCode == Activity.RESULT_CANCELED) {
            uris = new ArrayList<>();
            deleteMulInt = new ArrayList<>();
            MulPath = new ArrayList<>();
            MulResult = new ArrayList<>();
        }
    }
}