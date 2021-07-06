package com.elf.remote.viewmodel.search;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.ObservableField;

import com.elf.remote.Application;
import com.elf.remote.SubCall;
import com.elf.remote.model.data.CustomerDataBase;
import com.elf.remote.model.data.CustomerFile;
import com.elf.remote.model.data.DataAdapter;
import com.elf.remote.model.data.LoveDataBase;
import com.elf.remote.model.data.LoveFile;
import com.elf.remote.model.data.MyResvDataBase;
import com.elf.remote.model.data.MySlove;
import com.elf.remote.model.data.MySong;
import com.elf.remote.model.data.VerSionMachin;
import com.elf.remote.utils.LongPressRepeatListener;
import com.elf.remote.view.search.SearchMusic;
import com.elf.remote.viewmodel.BaseViewModel;

import java.util.List;

public class SubsunEditViewModel implements BaseViewModel {
    private final SubCall navi;

    private final Bundle bundle;

    private List<MySong> loveList;
    private MySlove song;
    private LoveFile lSong;
    private CustomerFile cSong;
    private int Tempo, temp, find, kind, c = 0, touch, maxTempo, minTempo;
    private String[] finds;
    private MyResvDataBase myResvDataBase;
    private LoveDataBase loveDataBase;
    private CustomerDataBase customerDataBase;

    public boolean intSec = true, tmpSec = false;

    String[] a1 = {"Ab", "A", "Bb", "B", "C", "Db", "D", "Eb", "E", "F", "F#", "G"};
    String[] a2 = {"Ab", "A", "Bb", "B", "C", "Db", "D", "Eb", "E", "F", "Gb", "G"};
    String[] a3 = {"Am", "Bbm", "Bm", "Cm", "C#m", "Dm", "Ebm", "Em", "Fm", "F#m", "Gm", "G#m"};
    String[] a4 = {"Am", "Bbm", "Bm", "Cm", "C#m", "Dm", "D#m", "Em", "Fm", "F#m", "Gm", "G#m"};

    public final ObservableField<String> intervalTxt = new ObservableField<>();
    public final ObservableField<String> tempoTxt = new ObservableField<>();

    SearchMusic searchMusic;

    public SubsunEditViewModel(SubCall navi, Bundle bundle, SearchMusic searchMusic) {
        this.navi = navi;
        this.bundle = bundle;
        this.searchMusic = searchMusic;
    }

    public void onExitClick() {
        navi.closeCall();
    }

    public void onSaveClick() {
        if (kind == 1) {
            ContentValues addRowValue = new ContentValues();
            addRowValue.put("Tempo", Tempo);
            addRowValue.put("PlayKey", c + loveList.get(0).AbsMain);
            String where = "rowid = " + lSong.Id;

            LoveFile data = new LoveFile();
            data.setId(lSong.Id);
            data.setGroupID(lSong.GroupID);
            data.setNumber(lSong.Number);
            data.setPlayKey(c + loveList.get(0).AbsMain);
            data.setTempo(Tempo);

            loveDataBase.update(addRowValue, where, null);

            searchMusic.loveFiles.set(touch, data);

            searchMusic.loveAdapter.notifyDataSetChanged();
        } else if (kind == 2) {
            ContentValues addRowValue = new ContentValues();
            addRowValue.put("tempo2", Tempo);
            addRowValue.put("interval2", finds[find]);
            addRowValue.put("count", c);
            String where = "_id = " + song.id;

            MySlove data = new MySlove();
            data.setId(song.id);
            data.setTitle(song.Title);
            data.setTempo(song.Tempo);
            data.setNumber(song.Number);
            data.setMain(song.Main);
            data.setAbsMain(song.AbsMain);
            data.setSinger(song.Singer);
            data.setIntaval(finds[find]);
            data.setTmep(Tempo);
            data.setCount(c);

            myResvDataBase.update(addRowValue, where, null);

            searchMusic.myResvList.set(touch, data);

            searchMusic.myResvAdapter.notifyDataSetChanged();
        } else {
            ContentValues addRowValue = new ContentValues();
            addRowValue.put("Tempo", Tempo);
            addRowValue.put("PlayKey", c + loveList.get(0).AbsMain);
            String where = "rowid = " + cSong.Id;

            CustomerFile data = new CustomerFile();
            data.setId(cSong.Id);
            data.setCustomerID(cSong.CustomerID);
            data.setNumber(cSong.Number);
            data.setPlayKey(c + loveList.get(0).AbsMain);
            data.setTempo(Tempo);

            customerDataBase.update(addRowValue, where, null);

            searchMusic.customerFiles.set(touch, data);

            searchMusic.customerAdapter.notifyDataSetChanged();
        }
        navi.oneCall();
    }

    public void onTempClick() {
        navi.fourCall();
    }

    public void onIntervalClick() {
        navi.fiveCall();
    }

    @Override
    public void onCreate() {
        myResvDataBase = MyResvDataBase.getInstance(Application.applicationContext());
        customerDataBase = CustomerDataBase.getInstance(Application.applicationContext());
        loveDataBase = LoveDataBase.getInstance(Application.applicationContext(), "MySong");

        kind = bundle.getInt("kind");
        touch = bundle.getInt("touch");

        if (kind == 1) {
            lSong = (LoveFile) bundle.getSerializable("song");
            getLoveSong(lSong.Number);
            Tempo = lSong.Tempo; // 바뀐 템포
            temp = loveList.get(0).Tempo;

            if (loveList.get(0).Main.equals("C#")) a1[5] = "C#";

            findMyInterval(a1, loveList.get(0).Main);
            findMyInterval(a3, loveList.get(0).Main);

            if (finds == null) {
                findMyInterval(a2, loveList.get(0).Main);
                findMyInterval(a4, loveList.get(0).Main);
            }

            if (loveList.get(0).AbsMain == lSong.PlayKey) {
                intervalTxt.set("음정               " + loveList.get(0).Main);
            } else if (loveList.get(0).AbsMain < lSong.PlayKey) {
                int c = lSong.PlayKey - loveList.get(0).AbsMain;
                this.c = c;
                find = find + c;
                if (find >= 12) find = find - 12;
                intervalTxt.set("음정               " + finds[find] + "(+" + (lSong.PlayKey - loveList.get(0).AbsMain) + ")");
            } else {
                int c = loveList.get(0).AbsMain - lSong.PlayKey;
                this.c = -c;
                find = find - c;
                if (find < 0) find = find + 12;
                intervalTxt.set("음정               " + finds[find] + "(" + (lSong.PlayKey - loveList.get(0).AbsMain) + ")");
            }
        } else if (kind == 2) {
            song = (MySlove) bundle.getSerializable("song");
            Tempo = song.Tmep; // 바뀐 템포
            c = song.count;
            temp = song.Tempo; // 오리지널 템포
            String interval = song.Intaval;

            if (interval.equals("C#")) a1[5] = "C#";

            findMyInterval(a1, interval);
            findMyInterval(a3, interval);

            if (finds == null) {
                findMyInterval(a2, interval);
                findMyInterval(a4, interval);
            }

            if (song.count == 0) intervalTxt.set("음정               " + finds[find]);
            else if (song.count < 0)
                intervalTxt.set("음정               " + finds[find] + "(" + (song.count) + ")");
            else
                intervalTxt.set("음정               " + finds[find] + "(+" + (song.count) + ")");
        } else {
            cSong = (CustomerFile) bundle.getSerializable("song");
            getLoveSong(cSong.Number);
            Tempo = cSong.Tempo; // 바뀐 템포
            temp = loveList.get(0).Tempo;

            if (loveList.get(0).Main.equals("C#")) a1[5] = "C#";

            findMyInterval(a1, loveList.get(0).Main);
            findMyInterval(a3, loveList.get(0).Main);

            if (finds == null) {
                findMyInterval(a2, loveList.get(0).Main);
                findMyInterval(a4, loveList.get(0).Main);
            }

            c = find;

            if (loveList.get(0).AbsMain == cSong.PlayKey) {
                intervalTxt.set("음정               " + loveList.get(0).Main);
            } else if (loveList.get(0).AbsMain < cSong.PlayKey) {
                int c = cSong.PlayKey - loveList.get(0).AbsMain;
                this.c = c;
                find = find + c;
                if (find >= 12) find = find - 12;
                intervalTxt.set("음정               " + finds[find] + "(+" + (cSong.PlayKey - loveList.get(0).AbsMain) + ")");
            } else {
                int c = loveList.get(0).AbsMain - cSong.PlayKey;
                this.c = -c;
                find = find - c;
                if (find < 0) find = find + 12;
                intervalTxt.set("음정               " + finds[find] + "(" + (cSong.PlayKey - loveList.get(0).AbsMain) + ")");
            }
        }

        if (Tempo == temp) tempoTxt.set("템포               " + Tempo);
        else if (Tempo < temp) tempoTxt.set("템포               " + Tempo + "(" + (Tempo - temp) + ")");
        else tempoTxt.set("템포               " + Tempo + "(+" + (Tempo - temp) + ")");

        if (VerSionMachin.getName().equals("G10")) {
            maxTempo = (temp * 15) / 10;

            if (maxTempo > 234) {
                maxTempo = 234;
                minTempo = temp - (maxTempo - temp);
            } else {
                minTempo = (maxTempo / ((15 * 2) / 10));
            }
        } else {
            maxTempo = (int) (temp * 1.9f);
            minTempo = (int) (temp * 0.5f + 0.9f);
        }
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }

    public View.OnTouchListener pBtn = new LongPressRepeatListener(300, 100, v -> {
        if (intSec && c < 11) {
            find = find + 1;
            c = c + 1;
            if (find > 11) {
                find = 0;
            }
            if (c == 0) intervalTxt.set("음정               " + finds[find]);
            else if (c < 0) intervalTxt.set("음정               " + finds[find] + "(" + c + ")");
            else intervalTxt.set("음정               " + finds[find] + "(+" + c + ")");
        } else if (tmpSec && Tempo < maxTempo) {
            Tempo = Tempo + 1;
            if (Tempo == temp) tempoTxt.set("템포               " + Tempo);
            else if (Tempo < temp) tempoTxt.set("템포               " + Tempo + "(" + (Tempo - temp) + ")");
            else tempoTxt.set("템포               " + Tempo + "(+" + (Tempo - temp) + ")");
        }
    });

    public View.OnTouchListener mBtn = new LongPressRepeatListener(300, 100, v -> {
        if (intSec && c > -11) {
            find = find - 1;
            c = c - 1;
            if (find < 0) {
                find = 11;
            }
            if (c == 0) intervalTxt.set("음정               " + finds[find]);
            else if (c > 0) intervalTxt.set("음정               " + finds[find] + "(+" + c + ")");
            else intervalTxt.set("음정               " + finds[find] + "(" + c + ")");
        } else if (tmpSec && Tempo > minTempo) {
            Tempo = Tempo - 1;
            if (Tempo == temp) tempoTxt.set("템포               " + Tempo);
            else if (Tempo > temp) tempoTxt.set("템포               " + Tempo + "(+" + (Tempo - temp) + ")");
            else tempoTxt.set("템포               " + Tempo + "(" + (Tempo - temp) + ")");
        }
    });

    void findMyInterval(String[] b, String a) {
        int count = 0;
        for (Object o : b) {
            if (o.equals(a)) {
                find = count;
                finds = b;
            }
            count++;
        }
    }

    private void getLoveSong(int text) {
        DataAdapter mDbHelper = new DataAdapter(Application.applicationContext());
        mDbHelper.open();
        // db에 있는 값들을 model을 적용해서 넣는다.
        loveList = mDbHelper.getCustomer(text);
        // db 닫기
        mDbHelper.close();
    }
}
