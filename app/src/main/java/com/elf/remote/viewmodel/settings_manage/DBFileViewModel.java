package com.elf.remote.viewmodel.settings_manage;

import androidx.databinding.ObservableField;

import com.elf.remote.model.data.MovFile;


public class DBFileViewModel {
    public final ObservableField<String> fname = new ObservableField<>();
    public final ObservableField<String> date = new ObservableField<>();
    public String path;

    public DBFileViewModel(MovFile movFile) {
        fname.set(movFile.getFname());
        date.set(movFile.getSize());
        path = movFile.getPath();
    }
}
