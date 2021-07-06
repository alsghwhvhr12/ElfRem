package com.elf.remote.viewmodel.search;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableField;

import com.elf.remote.Application;
import com.elf.remote.SungokCall;
import com.elf.remote.model.bluetooth.BluetoothCon;
import com.elf.remote.model.bluetooth.BtDevice;
import com.elf.remote.model.data.MyResvDataBase;
import com.elf.remote.model.data.MySong;
import com.elf.remote.model.data.VerSionMachin;
import com.elf.remote.utils.LongPressRepeatListener;
import com.elf.remote.viewmodel.BaseViewModel;

public class SungokViewModel extends BaseObservable implements BaseViewModel {
    private final SungokCall navi;

    MyResvDataBase myResvDataBase;
    String interval, number, OrgAbs2;
    public int Tempo, temp, find, ftemp, c = 0, AbsMain, x, record = 0, OrgAbs, maxTempo, minTempo;
    String[] finds;
    public MySong song;

    BluetoothCon bluetoothCon;
    Bundle bundle;

    public boolean intSec = true, tmpSec = false;

    String[] a1 = {"Ab", "A", "Bb", "B", "C", "Db", "D", "Eb", "E", "F", "F#", "G"};
    String[] a2 = {"Ab", "A", "Bb", "B", "C", "Db", "D", "Eb", "E", "F", "Gb", "G"};
    String[] a3 = {"Am", "Bbm", "Bm", "Cm", "C#m", "Dm", "Ebm", "Em", "Fm", "F#m", "Gm", "G#m"};
    String[] a4 = {"Am", "Bbm", "Bm", "Cm", "C#m", "Dm", "D#m", "Em", "Fm", "F#m", "Gm", "G#m"};

    public final ObservableField<String> Txt = new ObservableField<>();
    public final ObservableField<String> Txt2 = new ObservableField<>();
    public final ObservableField<String> Txt3 = new ObservableField<>();
    public final ObservableField<String> intervalTxt = new ObservableField<>();
    public final ObservableField<String> tempoTxt = new ObservableField<>();

    public SungokViewModel(SungokCall navi, BluetoothCon bluetoothCon, Bundle bundle) {
        this.navi = navi;
        this.bluetoothCon = bluetoothCon;
        this.bundle = bundle;
    }

    public void onCloseClick() {
        navi.closeCall();
    }

    public void onTempoClick() {
        navi.oneCall();
    }

    public void onIntervalClick() {
        navi.twoCall();
    }

    public void onSloveClick() {
        navi.sixCall();
    }

    public void onCloveClick() {
        navi.sevenCall();
    }

    public void onSrloveClick() {
        ContentValues addRowValue = new ContentValues();

        addRowValue.put("number", song.Number);
        addRowValue.put("title", song.Title);
        addRowValue.put("singer", song.Singer);
        addRowValue.put("tempo", song.Tempo);
        addRowValue.put("interval", song.Main);
        addRowValue.put("tempo2", Tempo);
        addRowValue.put("interval2", finds[find]);
        addRowValue.put("count", c - ftemp);
        addRowValue.put("AbsMain", song.AbsMain);
        myResvDataBase.insert(addRowValue);

        navi.fiveCall();
    }

    public void onSungokClick() {
        if (!(BtDevice.getDevice() == null)) {
            if (Tempo == temp && c == ftemp && VerSionMachin.getName().equals("G10")) {
                String command = "SONGCURR" + number;
                bluetoothCon.commandBluetooth(command);
            } else {
                String tt = String.valueOf(temp);
                String Abs = String.valueOf(AbsMain);
                String tmp = String.valueOf(Tempo);
                for (int i = Abs.length(); i < 3; i++) {
                    Abs = "0" + Abs;
                }
                for (int i = tmp.length(); i < 3; i++) {
                    tmp = "0" + tmp;
                }
                if (temp < 100) tt = "0" + tt;

                if (!VerSionMachin.getName().equals("G10")) {
                    bluetoothCon.commandBluetooth2(BluetoothLvRsvAdjustCtrlGTransfer(1));
                } else {
                    String command = "ADJSNGCUR" + number + OrgAbs2 + Abs + tt + tmp;
                    bluetoothCon.commandBluetooth(command);
                }
            }
        }
        navi.eightCall();
    }

    public void onResvClick() {
        if (!(BtDevice.getDevice() == null)) {
            if (Tempo == temp && c == ftemp && VerSionMachin.getName().equals("G10")) {
                String command = "RESVSONG" + number;
                bluetoothCon.commandBluetooth(command);
            } else {
                String tt = String.valueOf(temp);
                String Abs = String.valueOf(AbsMain);
                String tmp = String.valueOf(Tempo);
                for (int i = Abs.length(); i < 3; i++) {
                    Abs = "0" + Abs;
                }
                for (int i = tmp.length(); i < 3; i++) {
                    tmp = "0" + tmp;
                }
                if (temp < 100) tt = "0" + tt;

                if (!VerSionMachin.getName().equals("G10")) {
                    bluetoothCon.commandBluetooth2(BluetoothLvRsvAdjustCtrlGTransfer(2));
                } else {
                    String command = "ADJSNGRSV" + number + OrgAbs2 + Abs + tt + tmp;
                    bluetoothCon.commandBluetooth(command);
                }
            }
        }
        navi.nineCall();
    }

    public void onFirstClick() {
        if (!(BtDevice.getDevice() == null)) {
            if (Tempo == temp && c == ftemp && VerSionMachin.getName().equals("G10")) {
                String command = "FIRSTSONG" + number;
                bluetoothCon.commandBluetooth(command);
            } else {
                String tt = String.valueOf(temp);
                String Abs = String.valueOf(AbsMain);
                String tmp = String.valueOf(Tempo);
                for (int i = Abs.length(); i < 3; i++) {
                    Abs = "0" + Abs;
                }
                for (int i = tmp.length(); i < 3; i++) {
                    tmp = "0" + tmp;
                }
                if (temp < 100) tt = "0" + tt;

                if (!VerSionMachin.getName().equals("G10")) {
                    bluetoothCon.commandBluetooth2(BluetoothLvRsvAdjustCtrlGTransfer(3));
                } else {
                    String command = "ADJSNGFIR" + number + OrgAbs2 + Abs + tt + tmp;
                    bluetoothCon.commandBluetooth(command);
                }
            }
        }
        navi.tenCall();
    }

    @Override
    public void onCreate() {
        myResvDataBase = MyResvDataBase.getInstance(Application.applicationContext());

        song = (MySong) bundle.getSerializable("song");
        record = bundle.getInt("record");

        interval = song.Main;
        Tempo = song.Tempo;
        temp = song.Tempo;
        number = String.valueOf(song.Number);
        AbsMain = song.AbsMain;
        OrgAbs = AbsMain;
        OrgAbs2 = "0" + AbsMain;

        if (interval.equals("C#")) a1[5] = "C#";

        for (int i = number.length(); i < 7; i++) {
            number = "0" + number;
        }

        findMyInterval(a1, interval);
        findMyInterval(a3, interval);

        if (finds == null) {
            findMyInterval(a2, interval);
            findMyInterval(a4, interval);
        }

        Txt.set(String.valueOf(song.Number));
        Txt2.set(song.Title);
        Txt3.set(song.Singer);
        intervalTxt.set("음정               " + finds[find]);
        tempoTxt.set("템포               " + Tempo);

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

    void findMyInterval(String[] b, String a) {
        int count = 0;
        for (Object o : b) {
            if (o.equals(a)) {
                find = count;
                ftemp = count;
                c = count;
                finds = b;
            }
            count++;
        }
    }

    public byte[] BluetoothLvRsvAdjustCtrlGTransfer(int i) {
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        byte[] bArr = new byte[100];
        if (i == 1) {
            bArr[0] = 65;
            bArr[1] = 68;
            bArr[2] = 74;
            bArr[3] = 83;
            bArr[4] = 78;
            bArr[5] = 71;
            bArr[6] = 67;
            bArr[7] = 85;
            bArr[8] = 82;
        } else if (i == 2) {
            bArr[0] = 65;
            bArr[1] = 68;
            bArr[2] = 74;
            bArr[3] = 83;
            bArr[4] = 78;
            bArr[5] = 71;
            bArr[6] = 82;
            bArr[7] = 83;
            bArr[8] = 86;
        } else {
            bArr[0] = 65;
            bArr[1] = 68;
            bArr[2] = 74;
            bArr[3] = 83;
            bArr[4] = 78;
            bArr[5] = 71;
            bArr[6] = 70;
            bArr[7] = 73;
            bArr[8] = 82;
        }
        i2 = Integer.parseInt(number);
        bArr[9] = (byte) ((i2 >> 24) & 255);
        bArr[10] = (byte) ((i2 >> 16) & 255);
        bArr[11] = (byte) ((i2 >> 8) & 255);
        bArr[12] = (byte) (i2 & 255);
        i3 = OrgAbs;
        bArr[13] = (byte) ((i3 >> 8) & 255);
        bArr[14] = (byte) (i3 & 255);
        i4 = AbsMain;
        bArr[15] = (byte) ((i4 >> 8) & 255);
        bArr[16] = (byte) (i4 & 255);
        i5 = temp;
        bArr[17] = (byte) ((i5 >> 8) & 255);
        bArr[18] = (byte) (i5 & 255);
        i6 = Tempo;
        bArr[19] = (byte) ((i6 >> 8) & 255);
        bArr[20] = (byte) (i6 & 255);

        for (int i8 = 0; i8 < 18; i8++) bArr[i8 + 21] = 0;

        int i10 = 0;
        for (int i11 = 0; i11 < 39; i11++) {
            i10 += bArr[i11] & 255;
        }
        bArr[50] = (byte) ((i10 >> 8) & 255);
        bArr[51] = (byte) (i10 & 255);

        return bArr;
    }

    public View.OnTouchListener pBtn = new LongPressRepeatListener(300, 100, v -> {
        if (intSec && c < ftemp + 11) {
            find += 1;
            c += 1;
            AbsMain += 1;
            if (find > 11) {
                find = 0;
            }
            if (c == ftemp) intervalTxt.set("음정               " + finds[find]);
            else if (c < ftemp)
                intervalTxt.set("음정               " + finds[find] + "(" + (c - ftemp) + ")");
            else intervalTxt.set("음정               " + finds[find] + "(+" + (c - ftemp) + ")");
        } else if (tmpSec && Tempo < maxTempo) {
            Tempo = Tempo + 1;
            if (Tempo == temp) tempoTxt.set("템포               " + Tempo);
            else if (Tempo < temp)
                tempoTxt.set("템포               " + Tempo + "(" + (Tempo - temp) + ")");
            else tempoTxt.set("템포               " + Tempo + "(+" + (Tempo - temp) + ")");
        }
    });

    public View.OnTouchListener mBtn = new LongPressRepeatListener(300, 100, v -> {
        if (intSec && c > ftemp - 11) {
            find -= 1;
            c -= 1;
            AbsMain -= 1;
            if (find < 0) {
                find = 11;
            }
            if (c == ftemp) intervalTxt.set("음정               " + finds[find]);
            else if (c > ftemp)
                intervalTxt.set("음정               " + finds[find] + "(" + (c - ftemp) + ")");
            else intervalTxt.set("음정               " + finds[find] + "(" + (c - ftemp) + ")");
        } else if (tmpSec && Tempo > minTempo) {
            Tempo = Tempo - 1;
            if (Tempo == temp) tempoTxt.set("템포               " + Tempo);
            else if (Tempo > temp)
                tempoTxt.set("템포               " + Tempo + "(+" + (Tempo - temp) + ")");
            else tempoTxt.set("템포               " + Tempo + "(" + (Tempo - temp) + ")");
        }
    });
}
