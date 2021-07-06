package com.elf.remote.view.search;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.FragmentSingerListBinding;
import com.elf.remote.CallActivity;
import com.elf.remote.model.data.DataAdapter;
import com.elf.remote.model.data.mySinger;
import com.elf.remote.viewmodel.search.SingerListViewModel;

import java.util.List;


public class SingerListFragment extends DialogFragment implements CallActivity {
    List<mySinger> mySingerList;
    SingerID singerID;

    public SingerListFragment() {
    }

    public static SingerListFragment getInstance() {
        return new SingerListFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentSingerListBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_singer_list, container, false);
        binding.setViewModel(new SingerListViewModel(this));
        binding.executePendingBindings();

        Bundle bundle = getArguments();

        String SingerName = null;
        String kind = null;
        int country = 0;

        if (bundle != null) {
            SingerName = bundle.getString("singer");
            kind = bundle.getString("kind");
            country = bundle.getInt("country");
        }

        int size = 0;

        initLoadSinger(SingerName, kind, country);

        String[] arr = new String[mySingerList.size()];
        for (mySinger temp : mySingerList) {
            arr[size++] = temp.Singer;
        }

        setCancelable(false);

        ArrayAdapter adapter = new ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, arr);

        binding.singerList.setAdapter(adapter);

        binding.singerList.setOnItemClickListener((adapterView, view, i, l) -> {
            singerID.result(mySingerList.get(i).SingerID);
            dismiss();
        });
        return binding.getRoot();
    }

    @Override
    public void callActivity() {

    }

    @Override
    public void exitActivity() {
        dismiss();
    }

    @Override
    public void callDialog() {

    }

    private void initLoadSinger(String text, String kind, int country) {
        DataAdapter mDbHelper = new DataAdapter(requireActivity().getApplicationContext());
        mDbHelper.open();
        // db에 있는 값들을 model을 적용해서 넣는다.
        mySingerList = mDbHelper.getSingerData(text, kind, country);
        // db 닫기
        mDbHelper.close();
    }

    public void SingerIDcol(SingerID id) {
        singerID = id;
    }

    public interface SingerID {
        void result(String id);
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
}