package com.elf.remote.viewmodel.settings;

import android.bluetooth.BluetoothAdapter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableField;

import com.elf.mvvmremote.R;
import com.elf.remote.Application;
import com.elf.remote.CallActivity;
import com.elf.remote.viewmodel.BaseViewModel;

public class SyncViewModel extends BaseObservable implements BaseViewModel {
    private final CallActivity navi;

    int kind;
    public String color, font, video;
    String kindPre = "KindTip", namePre = "singer", jaePre = "title", ounPre = "songBy", colorPre = "color", fontPre = "font", videoPre = "videoQ";
    BluetoothAdapter mDevice;
    SharedPreferences preferences;

    public final ObservableField<Boolean> scroll = new ObservableField<>();
    public final ObservableField<String> singer = new ObservableField<>();
    public final ObservableField<String> title = new ObservableField<>();
    public final ObservableField<String> songBy = new ObservableField<>();
    public final ObservableField<Integer> textColor = new ObservableField<>();
    public final ObservableField<Typeface> textFont = new ObservableField<>();
    public final ObservableField<Integer> titleSize = new ObservableField<>();
    public final ObservableField<Integer> songBySize = new ObservableField<>();

    public SyncViewModel(CallActivity navi) {
        this.navi = navi;
    }

    public void onSaveClick() {
    }

    public void onExitClick() {

    }

    @Override
    public void onCreate() {
        mDevice = BluetoothAdapter.getDefaultAdapter();
        preferences = PreferenceManager.getDefaultSharedPreferences(Application.applicationContext());

        String str = preferences.getString(kindPre, "1");
        String str2 = preferences.getString(namePre, mDevice.getName());
        String str3 = preferences.getString(jaePre, "50");
        String str4 = preferences.getString(ounPre, "50");
        color = preferences.getString(colorPre, "black");
        font = preferences.getString(fontPre, "nanum.ttf");
        video = preferences.getString(videoPre, "1080");
        String strPre = "1";

        if (str.equals(strPre)) {
            kind = 1;
            scroll.set(true);
        } else {
            kind = 2;
            scroll.set(false);
        }

        singer.set(str2);

        title.set(str3);
        songBy.set(str4);

        titleSize.set(Integer.valueOf(str3));
        songBySize.set(Integer.valueOf(str4));
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(namePre, singer.get());

        editor.apply();
        navi.callActivity();
    }

    public CompoundButton.OnCheckedChangeListener changeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                kind = 1;
                scroll.set(true);
            } else {
                kind = 2;
                scroll.set(false);
            }
            SharedPreferences.Editor editor = preferences.edit();
            if (kind == 1) {
                editor.putString(kindPre, "1");
            } else {
                editor.putString(kindPre, "0");
            }
            editor.apply();
        }
    };

    public SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (progress < 50) progress = 50;
            if (seekBar.getId() == R.id.seekBar) {
                title.set(String.valueOf(progress));
                titleSize.set(progress);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(jaePre, title.get());
                editor.apply();
            } else if (seekBar.getId() == R.id.seekBar2) {
                songBy.set(String.valueOf(progress));
                songBySize.set(progress);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(ounPre, songBy.get());
                editor.apply();
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    public AdapterView.OnItemSelectedListener selectedFont = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Typeface typeface;
            if (position == 0) {
                font = "nanum.ttf";
                typeface = Typeface.createFromAsset(Application.applicationContext().getAssets(), "font/nanum.ttf");
            } else if (position == 1) {
                font = "eul10.ttf";
                typeface = Typeface.createFromAsset(Application.applicationContext().getAssets(), "font/eul10.ttf");
            } else if (position == 2) {
                font = "eul.ttf";
                typeface = Typeface.createFromAsset(Application.applicationContext().getAssets(), "font/eul.ttf");
            } else if (position == 3) {
                font = "grand.ttf";
                typeface = Typeface.createFromAsset(Application.applicationContext().getAssets(), "font/grand.ttf");
            } else if (position == 4) {
                font = "mugung.ttf";
                typeface = Typeface.createFromAsset(Application.applicationContext().getAssets(), "font/mugung.ttf");
            } else if (position == 5) {
                font = "happy.ttf";
                typeface = Typeface.createFromAsset(Application.applicationContext().getAssets(), "font/happy.ttf");
            } else if (position == 6) {
                font = "up.ttf";
                typeface = Typeface.createFromAsset(Application.applicationContext().getAssets(), "font/up.ttf");
            } else {
                font = "somi.ttf";
                typeface = Typeface.createFromAsset(Application.applicationContext().getAssets(), "font/somi.ttf");
            }

            textFont.set(typeface);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(fontPre, font);
            editor.apply();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    public AdapterView.OnItemSelectedListener selectedFontColor = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) {
                color = "black";
                textColor.set(Color.parseColor("#000000"));
            } else if (position == 1) {
                color = "white";
                textColor.set(Color.parseColor("#FFFFFF"));
            } else if (position == 2) {
                color = "red";
                textColor.set(Color.parseColor("#FF0000"));
            } else if (position == 3) {
                color = "orange";
                textColor.set(Color.parseColor("#FFA500"));
            } else if (position == 4) {
                color = "yellow";
                textColor.set(Color.parseColor("#FFFF00"));
            } else if (position == 5) {
                color = "green";
                textColor.set(Color.parseColor("#008000"));
            } else if (position == 6) {
                color = "blue";
                textColor.set(Color.parseColor("#0000FF"));
            } else if (position == 7) {
                color = "navy";
                textColor.set(Color.parseColor("#000080"));
            } else if (position == 8) {
                color = "purple";
                textColor.set(Color.parseColor("#800080"));
            } else if (position == 9) {
                color = "pink";
                textColor.set(Color.parseColor("#FFC0CB"));
            } else {
                color = "skyblue";
                textColor.set(Color.parseColor("#87CEEB"));
            }

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(colorPre, color);
            editor.apply();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    public AdapterView.OnItemSelectedListener selectedVideoQ = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) {
                video = "1080";
            } else if (position == 1) {
                video = "720";
            } else if (position == 2) {
                video = "480";
            } else {
                video = "360";
            }

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(videoPre, video);
            editor.apply();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
}