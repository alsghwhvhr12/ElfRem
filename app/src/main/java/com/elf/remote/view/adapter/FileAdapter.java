package com.elf.remote.view.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableArrayList;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.CustomFileBinding;
import com.elf.mvvmremote.databinding.FragmentFavoriteSongBinding;
import com.elf.mvvmremote.databinding.FragmentMicEffectBinding;
import com.elf.mvvmremote.databinding.FragmentOtherEffectBinding;
import com.elf.mvvmremote.databinding.FragmentSmartEqBinding;
import com.elf.remote.viewmodel.settings_manage.DBFileViewModel;

@SuppressLint({"ViewHolder", "InflateParams"})
public class FileAdapter extends BaseAdapter {
    private ObservableArrayList<DBFileViewModel> movFiles = new ObservableArrayList<>();
    int Visible = View.GONE;
    FragmentFavoriteSongBinding favoriteBinding;
    FragmentOtherEffectBinding otherBinding;
    FragmentMicEffectBinding micBinding;
    FragmentSmartEqBinding smartBinding;
    int kind;

    public void addAll(ObservableArrayList<DBFileViewModel> movFiles) {
        this.movFiles = movFiles;
        notifyDataSetChanged();
    }

    public void setFBinding(FragmentFavoriteSongBinding favoriteBinding) {
        this.favoriteBinding = favoriteBinding;
        kind = 1;
    }

    public void setOBinding(FragmentOtherEffectBinding otherBinding) {
        this.otherBinding = otherBinding;
        kind = 2;
    }

    public void setMBinding(FragmentMicEffectBinding micBinding) {
        this.micBinding = micBinding;
        kind = 3;
    }

    public void setSBinding(FragmentSmartEqBinding smartBinding) {
        this.smartBinding = smartBinding;
        kind = 4;
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
    public DBFileViewModel getItem(int position) {
        return movFiles.get(position);
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        CustomFileBinding binding;

        if(converView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            binding = DataBindingUtil.inflate(inflater, R.layout.custom_file, parent, false);
            converView = binding.getRoot();
            converView.setTag(binding);
        } else {
            binding = (CustomFileBinding) converView.getTag();
        }

        binding.chk.setVisibility(Visible);
        binding.chk.setOnClickListener(v -> {
            switch (kind) {
                case 1 :
                    favoriteBinding.listView.setItemChecked(position, binding.chk.isChecked());
                    break;
                case 2 :
                    otherBinding.listView.setItemChecked(position, binding.chk.isChecked());
                    break;
                case 3 :
                    micBinding.listView.setItemChecked(position, binding.chk.isChecked());
                    break;
                case 4 :
                    smartBinding.listView.setItemChecked(position, binding.chk.isChecked());
                    break;

            }
        });
        try {
            binding.setViewModel(movFiles.get(position));
        } catch (OutOfMemoryError e) {
            System.gc();
            return getView(position, converView, parent);
        }

        return converView;
    }

    public void isChk(int visible) {
        this.Visible = visible;
    }
}