package com.elf.remote.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.elf.mvvmremote.R;

public class Checkable extends LinearLayout implements android.widget.Checkable {

    public Checkable(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Change the checked state of the view
     *
     * @param checked The new checked state
     */
    @Override
    public void setChecked(boolean checked) {
        CheckBox cb = findViewById(R.id.chk);

        if (cb.isChecked() != checked) {
            cb.setChecked(checked);
        }
    }

    /**
     * @return The current checked state of the view
     */
    @Override
    public boolean isChecked() {
        CheckBox cb = findViewById(R.id.chk);

        return cb.isChecked();
    }

    /**
     * Change the checked state of the view to the inverse of its current state
     */
    @Override
    public void toggle() {
        CheckBox cb = findViewById(R.id.chk);

        setChecked(!cb.isChecked());
    }
}
