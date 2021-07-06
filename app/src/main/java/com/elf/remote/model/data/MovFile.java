package com.elf.remote.model.data;

import android.graphics.Bitmap;

public class MovFile {
    private String fname;
    private String size;
    private Bitmap poster;
    private String path;

    public void setFname(String fname) {
        this.fname = fname;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setPoster(Bitmap poster) {
        this.poster = poster;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFname() {
        return this.fname;
    }

    public String getSize() {
        return this.size;
    }

    public Bitmap getPoster() {
        return this.poster;
    }

    public String getPath() {
        return this.path;
    }
}
