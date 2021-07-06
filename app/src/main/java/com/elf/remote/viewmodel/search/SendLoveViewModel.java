package com.elf.remote.viewmodel.search;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.databinding.BaseObservable;

import com.elf.remote.Application;
import com.elf.remote.CallActivity;
import com.elf.remote.model.bluetooth.BluetoothCon;
import com.elf.remote.model.data.LoveCount;
import com.elf.remote.model.data.LoveCountDDataBase;
import com.elf.remote.model.data.LoveCountDataBase;
import com.elf.remote.model.data.LoveDataBase;
import com.elf.remote.model.data.LoveFile;
import com.elf.remote.view.adapter.LoveCountAdapter;
import com.elf.remote.viewmodel.BaseViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class SendLoveViewModel extends BaseObservable implements BaseViewModel {
    private final CallActivity navi;

    public ArrayList<LoveCount> loveCounts;
    public ArrayList<LoveFile> loveFiles = new ArrayList<>();
    public LoveCountDataBase loveCountDataBase;
    public LoveCountDataBase loveCountDataBase2;
    public LoveDataBase loveDataBase;
    public LoveCountAdapter loveCountAdapter;
    public LoveCountDDataBase loveCountDDataBase;

    public String loveKind;
    public int GroupId, kind;
    public ArrayList<Integer> GroupIds = new ArrayList<>();

    Bundle bundle;
    BluetoothCon bluetoothCon;
    ProgressDialog progressDialog;

    int sel;

    public SendLoveViewModel(CallActivity navi, Bundle bundle, BluetoothCon bluetoothCon, ProgressDialog progressDialog) {
        this.navi = navi;
        this.bundle = bundle;
        this.bluetoothCon = bluetoothCon;
        this.progressDialog = progressDialog;
    }

    public void onExitClick() {
        navi.exitActivity();
    }

    public void onSendClick() {
        if (sel > 0) {
            navi.callActivity();
            String Group;
            if (loveKind.equals("myLove")) Group = "GroupID";
            else Group = "CustomerID";

            for (LoveFile lv : loveFiles) {
                ContentValues addRowValue = new ContentValues();

                addRowValue.put(Group, sel);
                addRowValue.put("Number", lv.getNumber());
                addRowValue.put("Tempo", lv.getTempo());
                addRowValue.put("PlayKey", lv.getPlayKey());
                loveCountDDataBase.insert(addRowValue);
            }

            for (LoveCount lc : loveCounts) {
                ContentValues addRowValue = new ContentValues();

                addRowValue.put(Group, lc.getGroupId());
                addRowValue.put("Name", lc.getName());
                loveCountDataBase2.insert(addRowValue);
            }

            showProgressDialog();
            bluetoothCon.commandBluetooth("SENDSTR");
        }
    }

    @Override
    public void onCreate() {
        kind = bundle.getInt("kind");
        loveKind = bundle.getString("loveKind");
        GroupId = bundle.getInt("GroupId");
        GroupIds = bundle.getIntegerArrayList("GroupIds");

        File db = new File(Application.applicationContext().getApplicationInfo().dataDir + "/databases/mycount.db");
        if (db.exists()) {
            db.delete();
        }
        saveLogoFiles(db);

        bluetoothCon.getDataDir(db.getAbsolutePath());

        if (loveKind.equals("myLove")) {
            loveCountDataBase = LoveCountDataBase.getInstance(Application.applicationContext(), "MySongGroup", "lovecount.db");
            loveDataBase = LoveDataBase.getInstance(Application.applicationContext(), "MySong");
            loveCountDDataBase = LoveCountDDataBase.getInstance(Application.applicationContext(), "MySong");
            loveCountDataBase2 = LoveCountDataBase.getInstance(Application.applicationContext(), "MySongGroup", "mycount.db");
        } else {
            loveCountDataBase = LoveCountDataBase.getInstance(Application.applicationContext(), "Customer", "lovecount.db");
            loveDataBase = LoveDataBase.getInstance(Application.applicationContext(), "CustomerSong");
            loveCountDDataBase = LoveCountDDataBase.getInstance(Application.applicationContext(), "CustomerSong");
            loveCountDataBase2 = LoveCountDataBase.getInstance(Application.applicationContext(), "Customer", "mycount.db");
        }
        this.getLoveData();
        loveCountAdapter = new LoveCountAdapter(Application.applicationContext(), loveCounts);

        if (GroupId > 0) {
            getMyLoveData(GroupId);
        } else {
            for (int i : GroupIds) {
                getMyLoveData(i);
            }
        }
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }

    public void getLoveData() {
        loveCounts = new ArrayList<>();
        String Group;

        if (loveKind.equals("myLove")) Group = "GroupID";
        else Group = "CustomerID";

        String[] columns = new String[]{"rowid", Group, "Name", "Total"};
        String order = "rowid ASC";

        Cursor cursor = loveCountDataBase.query(columns, null, null, null, null, order);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                LoveCount LoveData = new LoveCount();

                LoveData.setId(cursor.getInt(0));
                LoveData.setGroupId(cursor.getInt(1));
                LoveData.setName(cursor.getString(2));
                LoveData.setTotal(cursor.getInt(3));

                loveCounts.add(LoveData);
            }
        }
    }

    public void getMyLoveData(int id) {
        String Group, selection;

        if (loveKind.equals("myLove")) Group = "GroupID";
        else Group = "CustomerID";

        String[] columns = new String[]{"rowid", Group, "Number", "Tempo", "PlayKey"};
        if (kind ==2) selection = Group + " = " + id;
        else selection = "rowid = " + id;
        String order = "rowid ASC";

        Cursor cursor = loveDataBase.query(columns, selection, null, null, null, order);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                LoveFile LoveData = new LoveFile();

                LoveData.setId(cursor.getInt(0));
                LoveData.setGroupID(cursor.getInt(1));
                LoveData.setNumber(cursor.getInt(2));
                LoveData.setTempo(cursor.getInt(3));
                LoveData.setPlayKey(cursor.getInt(4));

                loveFiles.add(LoveData);
            }
        }
    }

    public AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            sel = loveCounts.get(position).getGroupId();
        }
    };

    protected void showProgressDialog() {
        bluetoothCon.getSendFragment(this);

        progressDialog.setMessage("애창곡 전송중...");

        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        progressDialog.show();
    }

    public void hideProgressDialog() {
        progressDialog.dismiss();
        bluetoothCon.getSendFragment(null);
    }

    public void updateProgressDialog(int i) {
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(100);
        progressDialog.setProgress(i);
        progressDialog.setCancelable(false);

        if (i == 100) {
            hideProgressDialog();
        }
    }

    void saveLogoFiles(File file) {
        AssetManager manager = Application.applicationContext().getAssets();
        InputStream open;
        FileOutputStream fos;

        try {
            open = manager.open("mylove.db");
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
