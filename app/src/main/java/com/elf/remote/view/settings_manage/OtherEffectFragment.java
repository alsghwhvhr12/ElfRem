package com.elf.remote.view.settings_manage;

import android.content.Context;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableArrayList;
import androidx.fragment.app.Fragment;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.FragmentOtherEffectBinding;
import com.elf.remote.CallActivity;
import com.elf.remote.model.data.VerSionMachin;
import com.elf.remote.view.adapter.FileAdapter;
import com.elf.remote.viewmodel.settings_manage.DBFileViewModel;
import com.elf.remote.viewmodel.settings_manage.OtherEffectViewModel;

import java.util.ArrayList;


public class OtherEffectFragment extends Fragment implements CallActivity {
    public static FileAdapter myAdapter;
    public static OtherEffectViewModel model;
    FragmentOtherEffectBinding binding;

    public OtherEffectFragment() {
    }

    public static OtherEffectFragment getInstance() {
        return new OtherEffectFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        model = new OtherEffectViewModel(this);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_other_effect, container, false);
        binding.setViewModel(model);
        binding.executePendingBindings();

        model.getFileList("db");
        // Inflate the layout for this fragment

        VerSionMachin.setMultiCheck(false);
        VerSionMachin.setFilePath(null);
        VerSionMachin.setPathList(null);

        binding.listView.setOnItemLongClickListener((parent, view, position, id) -> {
            if (binding.listView.getChoiceMode() != AbsListView.CHOICE_MODE_MULTIPLE) {
                binding.listView.clearChoices();
                binding.listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
                binding.listView.setSelector(R.drawable.list_sel2);
                binding.listView.setItemChecked(position, true);
                myAdapter.isChk(View.VISIBLE);
                myAdapter.setOBinding(binding);
                myAdapter.notifyDataSetChanged();
                VerSionMachin.setMultiCheck(true);
                SparseBooleanArray sp = binding.listView.getCheckedItemPositions();
                ArrayList<String> pathList = new ArrayList<>();
                int i = binding.listView.getCount() - 1;
                while (i >= 0) {
                    if (sp.get(i)) {
                        String path = model.movFiles.get(i).path;
                        pathList.add(path);
                    }
                    i--;
                }
                VerSionMachin.setPathList(pathList);
            } else if (binding.listView.getChoiceMode() == AbsListView.CHOICE_MODE_MULTIPLE) {
                callActivity();
            }

            return true;
        });

        binding.listView.setOnItemClickListener((parent, view, position, id) -> {
            if (VerSionMachin.isMultiCheck()) {
                SparseBooleanArray sp = binding.listView.getCheckedItemPositions();
                ArrayList<String> pathList = new ArrayList<>();
                int i = binding.listView.getCount() - 1;
                while (i >= 0) {
                    if (sp.get(i)) {
                        String path = model.movFiles.get(i).path;
                        pathList.add(path);
                    }
                    i--;
                }
                VerSionMachin.setPathList(pathList);
            } else {
                String asd = model.movFiles.get(position).path;
                VerSionMachin.setFilePath(asd);
                binding.listView.setSelector(R.drawable.list_sel);
            }
        });

        return binding.getRoot();
    }

    @BindingAdapter("app:items6")
    public static void setDBFileList(ListView listView, ObservableArrayList<DBFileViewModel> movFiles) {
        if (listView.getAdapter() == null) {
            myAdapter = new FileAdapter();
            listView.setAdapter(myAdapter);
        } else {
            myAdapter = (FileAdapter) listView.getAdapter();
        }

        myAdapter.addAll(movFiles);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (binding.listView.getChoiceMode() == AbsListView.CHOICE_MODE_MULTIPLE) {
                    callActivity();
                } else {
                    requireActivity().finish();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public void callActivity() {
        binding.listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        myAdapter.isChk(View.GONE);
        myAdapter.notifyDataSetChanged();
        binding.listView.clearChoices();
        VerSionMachin.setMultiCheck(false);
        VerSionMachin.setPathList(null);
    }

    @Override
    public void exitActivity() {

    }

    @Override
    public void callDialog() {

    }
}