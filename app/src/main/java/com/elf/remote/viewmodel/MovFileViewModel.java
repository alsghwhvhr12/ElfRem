package com.elf.remote.viewmodel;

import android.graphics.Bitmap;

import androidx.databinding.ObservableField;

import com.elf.remote.model.data.MovFile;


public class MovFileViewModel {
    public final ObservableField<String> fname = new ObservableField<>();
    public final ObservableField<String> size = new ObservableField<>();
    public final ObservableField<Bitmap> poster = new ObservableField<>();
    public final ObservableField<String> path = new ObservableField<>();

    public MovFileViewModel(MovFile movFile) {
        fname.set(movFile.getFname());
        size.set(movFile.getSize());
        poster.set(movFile.getPoster());
        path.set(movFile.getPath());
    }
}
