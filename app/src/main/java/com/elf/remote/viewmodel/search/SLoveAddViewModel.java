package com.elf.remote.viewmodel.search;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.elf.remote.Application;
import com.elf.remote.SubCall;
import com.elf.remote.model.data.CustomerDataBase;
import com.elf.remote.model.data.CustomerGroup;
import com.elf.remote.model.data.CustomerGroupDataBase;
import com.elf.remote.model.data.LoveDataBase;
import com.elf.remote.model.data.LoveGroup;
import com.elf.remote.model.data.LoveGroupDataBase;
import com.elf.remote.model.data.MySong;
import com.elf.remote.view.adapter.CustomerGroupAdapter;
import com.elf.remote.view.adapter.LoveGroupAdapter;
import com.elf.remote.viewmodel.BaseViewModel;

import java.util.ArrayList;

public class SLoveAddViewModel implements BaseViewModel {
    private final SubCall navi;
    public String loveKind;
    int Tempo, count;

    ArrayList<LoveGroup> loveGroups;
    ArrayList<CustomerGroup> customerGroups;

    LoveDataBase loveDataBase;
    LoveGroupDataBase loveGroupDataBase;
    public LoveGroupAdapter loveGroupAdapter;

    CustomerDataBase customerDataBase;
    CustomerGroupDataBase customerGroupDataBase;
    public CustomerGroupAdapter customerGroupAdapter;

    MySong song;

    Bundle bundle;

    public SLoveAddViewModel(SubCall navi, Bundle bundle) {
        this.navi = navi;
        this.bundle = bundle;
    }

    public void onExitClick() {
        navi.closeCall();
    }

    public void onAddClikc() {
        navi.oneCall();
    }

    @Override
    public void onCreate() {
        loveKind = bundle.getString("LoveKind");
        int Tempo2 = bundle.getInt("tempo2");
        count = bundle.getInt("count");
        song = (MySong) bundle.getSerializable("song");

        if (song.Tempo != Tempo2) {
            Tempo = Tempo2;
        } else {
            Tempo = song.Tempo;
        }

        if (loveKind.equals("myLove")) {
            loveGroupDataBase = LoveGroupDataBase.getInstance(Application.applicationContext());
            loveDataBase = LoveDataBase.getInstance(Application.applicationContext(), "MySong");

            this.getMyLoveData();

            loveGroupAdapter = new LoveGroupAdapter(Application.applicationContext(), loveGroups);
        } else {
            customerGroupDataBase = CustomerGroupDataBase.getInstance(Application.applicationContext());
            customerDataBase = CustomerDataBase.getInstance(Application.applicationContext());

            this.getCustomerData();

            customerGroupAdapter = new CustomerGroupAdapter(Application.applicationContext(), customerGroups);
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

    public AdapterView.OnItemClickListener LoveClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
            ContentValues addRowValue = new ContentValues();

            if (loveKind.equals("myLove")) {
                int id = loveGroups.get(i).getGroupId();

                addRowValue.put("GroupID", id);
                addRowValue.put("Number", song.Number);
                addRowValue.put("Tempo", Tempo);
                addRowValue.put("PlayKey", song.AbsMain + count);
                loveDataBase.insert(addRowValue);
            } else {
                int id = customerGroups.get(i).getCustomerId();

                addRowValue.put("CustomerID", id);
                addRowValue.put("Number", song.Number);
                addRowValue.put("Tempo", Tempo);
                addRowValue.put("PlayKey", song.AbsMain + count);
                customerDataBase.insert(addRowValue);
            }
            navi.twoCall();
        }
    };

    public void SloAdd(String title, String memo) {
        ContentValues addRowValue = new ContentValues();

        if (loveKind.equals("myLove")) {
            addRowValue.put("Name", title);
            addRowValue.put("Memo", memo);
            addRowValue.put("GroupID", loveGroupAdapter.getCount() + 1);
            loveGroupDataBase.insert(addRowValue);

            getMyLoveData();

            loveGroupAdapter = new LoveGroupAdapter(Application.applicationContext(), loveGroups);
        } else {
            addRowValue.put("Name", title);
            addRowValue.put("Memo", memo);
            addRowValue.put("CustomerID", customerGroupAdapter.getCount() + 1);
            customerGroupDataBase.insert(addRowValue);

            getCustomerData();

            customerGroupAdapter = new CustomerGroupAdapter(Application.applicationContext(), customerGroups);
        }
    }

    public void getMyLoveData() {
        loveGroups = new ArrayList<>();

        String[] columns = new String[]{"rowid", "GroupID", "Name", "Memo"};
        String order = "rowid ASC";

        Cursor cursor = loveGroupDataBase.query(columns, null, null, null, null, order);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                LoveGroup LoveData = new LoveGroup();

                LoveData.setId(cursor.getInt(0));
                LoveData.setGroupId(cursor.getInt(1));
                LoveData.setName(cursor.getString(2));
                LoveData.setMemo(cursor.getString(3));

                loveGroups.add(LoveData);
            }
        }
    }

    public void getCustomerData() {
        customerGroups = new ArrayList<>();

        String[] columns = new String[]{"rowid", "CustomerID", "Name", "Memo"};
        String order = "rowid ASC";

        Cursor cursor = customerGroupDataBase.query(columns, null, null, null, null, order);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                CustomerGroup LoveData = new CustomerGroup();

                LoveData.setId(cursor.getInt(0));
                LoveData.setCustomerId(cursor.getInt(1));
                LoveData.setName(cursor.getString(2));
                LoveData.setMemo(cursor.getString(3));

                customerGroups.add(LoveData);
            }
        }
    }
}
