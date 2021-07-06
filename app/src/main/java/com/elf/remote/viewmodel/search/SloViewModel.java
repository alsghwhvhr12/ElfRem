package com.elf.remote.viewmodel.search;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import androidx.databinding.BaseObservable;

import com.elf.remote.Application;
import com.elf.remote.SubCall;
import com.elf.remote.model.data.CustomerDataBase;
import com.elf.remote.model.data.CustomerGroup;
import com.elf.remote.model.data.CustomerGroupDataBase;
import com.elf.remote.model.data.LoveDataBase;
import com.elf.remote.model.data.LoveGroup;
import com.elf.remote.model.data.LoveGroupDataBase;
import com.elf.remote.view.adapter.CustomerGroupAdapter;
import com.elf.remote.view.adapter.LoveGroupAdapter;
import com.elf.remote.view.search.SloFragment;
import com.elf.remote.viewmodel.BaseViewModel;

import java.util.ArrayList;

public class SloViewModel extends BaseObservable implements BaseViewModel {
    private final SubCall navi;

    public ArrayList<LoveGroup> loveGroups;
    public ArrayList<LoveGroup> tempFiles2;

    public ArrayList<CustomerGroup> customerGroups;
    public ArrayList<CustomerGroup> tempFiles;

    public LoveGroupDataBase loveGroupDataBase;
    public LoveDataBase loveDataBase;
    public CustomerGroupDataBase customerGroupDataBase;
    public CustomerDataBase customerDataBase;

    public LoveGroupAdapter loveGroupAdapter;
    public CustomerGroupAdapter customerGroupAdapter;

    public String loveKind;

    Bundle bundle;

    SloFragment.getId gt;

    public int stat = 0, cl = 0;

    public SloViewModel(SubCall navi, Bundle bundle, SloFragment.getId gt) {
        this.navi = navi;
        this.bundle = bundle;
        this.gt = gt;
    }

    public void onCloseClick() {
        navi.closeCall();
    }

    public void onAddClick() {
        navi.oneCall();
    }

    public void onChgClick() {
        navi.twoCall();
    }

    public void onDelClick() {
        navi.threeCall();
    }

    public void onEditClick() {
        navi.fourCall();
    }

    public void onUpClick() {
        navi.fiveCall();
    }

    public void onDownClick() {
        navi.sixCall();
    }

    public void onTopClick() {
        navi.sevenCall();
    }

    public void onBottomClick() {
        navi.eightCall();
    }

    public void onSendClick() {

    }

    @Override
    public void onCreate() {
        loveKind = bundle.getString("LoveKind");

        if (loveKind.equals("myLove")) {
            loveGroupDataBase = LoveGroupDataBase.getInstance(Application.applicationContext());
            this.getLoveData();
            loveGroupAdapter = new LoveGroupAdapter(Application.applicationContext(), loveGroups);

            loveDataBase = LoveDataBase.getInstance(Application.applicationContext(), "MySong");
        } else {
            customerGroupDataBase = CustomerGroupDataBase.getInstance(Application.applicationContext());
            this.getCustomerData();
            customerGroupAdapter = new CustomerGroupAdapter(Application.applicationContext(), customerGroups);

            customerDataBase = CustomerDataBase.getInstance(Application.applicationContext());
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

    public AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
            if (cl == 0) {
                if (loveKind.equals("myLove")) {
                    if (stat == 0) {
                        gt.Id(loveGroups.get(i).getGroupId());
                    }
                    loveGroupAdapter.setChecked(i);
                    loveGroupAdapter.notifyDataSetChanged();
                } else {
                    if (stat == 0) {
                        gt.Id(customerGroups.get(i).getCustomerId());
                    }
                    customerGroupAdapter.setChecked(i);
                    customerGroupAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    public void addSlo(String title, String memo) {
        ContentValues addRowValue = new ContentValues();

        if (loveKind.equals("myLove")) {
            addRowValue.put("Name", title);
            addRowValue.put("Memo", memo);
            addRowValue.put("GroupID", loveGroupAdapter.getCount() + 1);
            loveGroupDataBase.insert(addRowValue);

            LoveGroup data = new LoveGroup();

            data.setId(loveGroupAdapter.getCount() + 1);
            data.setGroupId(loveGroupAdapter.getCount() + 1);
            data.setName(title);
            data.setMemo(memo);

            loveGroups.add(data);
            tempFiles2.add(data);
            loveGroupAdapter.setChecked(-1);
            loveGroupAdapter.notifyDataSetChanged();
        } else {
            addRowValue.put("Name", title);
            addRowValue.put("Memo", memo);
            addRowValue.put("CustomerID", customerGroupAdapter.getCount() + 1);
            customerGroupDataBase.insert(addRowValue);

            CustomerGroup data = new CustomerGroup();

            data.setId(customerGroupAdapter.getCount() + 1);
            data.setCustomerId(customerGroupAdapter.getCount() + 1);
            data.setName(title);
            data.setMemo(memo);

            customerGroups.add(data);
            tempFiles.add(data);
            customerGroupAdapter.setChecked(-1);
            customerGroupAdapter.notifyDataSetChanged();
        }
    }

    public void saveEdit(String finish) {
        if (loveKind.equals("myLove")) {
            if (finish.equals("1")) {
                for (int i = 0; i < loveGroups.size(); i++) {
                    ContentValues addRowValue = new ContentValues();
                    addRowValue.put("Name", loveGroups.get(i).getName());
                    addRowValue.put("Memo", loveGroups.get(i).getMemo());
                    addRowValue.put("GroupID", loveGroups.get(i).getGroupId());
                    String where = "rowid = " + loveGroups.get(i).getId();
                    loveGroupDataBase.update(addRowValue, where, null);
                    tempFiles2.clear();
                    tempFiles2.addAll(loveGroups);
                }
            } else {
                loveGroups.clear();

                loveGroups.addAll(tempFiles2);

            }
            loveGroupAdapter.setChecked(-1);
            loveGroupAdapter.notifyDataSetChanged();
        } else {
            if (finish.equals("1")) {
                for (int i = 0; i < customerGroups.size(); i++) {
                    ContentValues addRowValue = new ContentValues();
                    addRowValue.put("Name", customerGroups.get(i).getName());
                    addRowValue.put("Memo", customerGroups.get(i).getMemo());
                    addRowValue.put("CustomerID", customerGroups.get(i).getCustomerId());
                    String where = "rowid = " + customerGroups.get(i).getId();
                    customerGroupDataBase.update(addRowValue, where, null);
                    tempFiles.clear();
                    tempFiles.addAll(customerGroups);
                }
            } else {
                customerGroups.clear();
                customerGroups.addAll(tempFiles);
            }
            customerGroupAdapter.setChecked(-1);
            customerGroupAdapter.notifyDataSetChanged();
        }
    }

    public void getLoveData() {
        loveGroups = new ArrayList<>();
        tempFiles2 = new ArrayList<>();

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
                tempFiles2.add(LoveData);
            }
        }
    }

    public void getCustomerData() {
        customerGroups = new ArrayList<>();
        tempFiles = new ArrayList<>();

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
                tempFiles.add(LoveData);
            }
        }
    }
}