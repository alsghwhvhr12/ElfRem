package com.elf.remote.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.elf.mvvmremote.R;
import com.elf.remote.view.adapter.PhotoAdapter;

public class SlidePager extends FragmentActivity {
    int kind;
    String namePre;
    CheckBox chk;
    Button btn;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);

        Intent intent = getIntent();

        kind = intent.getExtras().getInt("kind");

        chk = findViewById(R.id.chk);
        btn = findViewById(R.id.Tip_xBtn);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Instantiate a ViewPager2 and a PagerAdapter.
        ViewPager2 viewPager = findViewById(R.id.pager);
        FragmentStateAdapter pagerAdapter = new PhotoAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        btn.setOnClickListener(view -> onBackPressed());
    }

    @Override
    public void onDestroy() {
        if(chk.isChecked()) {
            SharedPreferences.Editor editor = preferences.edit();
            if (kind == 2) {
                namePre = "VideoTip";
            } else {
                namePre = "RecTip";
            }

            editor.putString(namePre, "1");
            editor.apply();
        }

        super.onDestroy();
    }

}
