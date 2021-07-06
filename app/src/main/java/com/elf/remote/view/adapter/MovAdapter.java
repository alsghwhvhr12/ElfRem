package com.elf.remote.view.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableArrayList;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.CustomMovlistBinding;
import com.elf.remote.utils.RecycleUtils;
import com.elf.remote.view.my_video.MyMovListFragment;
import com.elf.remote.view.view_recorded.MovListFragment;
import com.elf.remote.viewmodel.MovFileViewModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

@SuppressLint({"ViewHolder", "InflateParams"})
public class MovAdapter extends BaseAdapter {
    private ObservableArrayList<MovFileViewModel> movFiles = new ObservableArrayList<>();
    int Visible = View.GONE;
    int viewAct;
    private final List<WeakReference<View>> mRecycleList = new ArrayList<>();

    public void addAll(ObservableArrayList<MovFileViewModel> movFiles, int i) {
        this.movFiles = movFiles;
        notifyDataSetChanged();
        viewAct = i;
    }

    @Override
    public int getCount() {
        return movFiles.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public MovFileViewModel getItem(int position) {
        return movFiles.get(position);
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        CustomMovlistBinding binding;

        if(converView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            binding = DataBindingUtil.inflate(inflater, R.layout.custom_movlist, parent, false);
            converView = binding.getRoot();
            converView.setTag(binding);
        } else {
            binding = (CustomMovlistBinding) converView.getTag();
        }

        binding.chk.setVisibility(Visible);
        try {
            binding.setViewModel(movFiles.get(position));
        } catch (OutOfMemoryError e) {
            recycleHalf();
            System.gc();
            return getView(position, converView, parent);
        }

        mRecycleList.add(new WeakReference<>(binding.poster));

        binding.chk.setOnClickListener(v -> {
            if (viewAct == 1) MyMovListFragment.binding2.MymovList.setItemChecked(position, binding.chk.isChecked());
            else MovListFragment.binding.movList.setItemChecked(position, binding.chk.isChecked());
        });

        return converView;
    }

    public void isChk(int visible) {
        this.Visible = visible;
    }

    public void recycleHalf() {
        int halfSize = mRecycleList.size() / 8;

        List<WeakReference<View>> recycleHalfList = mRecycleList.subList(0, halfSize);

        RecycleUtils.recursiveRecycle(recycleHalfList);

        if (halfSize > 0) {
            mRecycleList.subList(0, halfSize).clear();
        }
    }
}