package com.elf.remote.viewmodel.search;

import android.os.Bundle;

import androidx.databinding.ObservableField;

import com.elf.remote.Application;
import com.elf.remote.SubCall;
import com.elf.remote.model.bluetooth.BluetoothCon;
import com.elf.remote.model.bluetooth.BtDevice;
import com.elf.remote.model.data.CustomerFile;
import com.elf.remote.model.data.DataAdapter;
import com.elf.remote.model.data.LoveFile;
import com.elf.remote.model.data.MySlove;
import com.elf.remote.model.data.MySong;
import com.elf.remote.model.data.VerSionMachin;
import com.elf.remote.viewmodel.BaseViewModel;

import java.util.List;

public class SubSungokViewModel implements BaseViewModel {
    private final SubCall navi;
    public int kind, touch, Tempo, temp, c, AbsMain, record = 0, OrgAbs;
    public String number, OrgAbs2, text;
    public MySlove song;
    public LoveFile lSong;
    public CustomerFile cSong;
    public List<MySong> loveList;
    BluetoothCon bluetoothCon;
    Bundle bundle;

    public final ObservableField<String> Txt = new ObservableField<>();
    public final ObservableField<String> Txt2 = new ObservableField<>();
    public final ObservableField<String> Txt3 = new ObservableField<>();

    public SubSungokViewModel(SubCall navi, BluetoothCon bluetoothCon, Bundle bundle) {
        this.navi = navi;
        this.bluetoothCon = bluetoothCon;
        this.bundle = bundle;
    }

    public void onCloseClick() {
        navi.closeCall();
    }

    public void onEditClick() {
        navi.oneCall();
    }

    public void onDelClick() {
        navi.twoCall();
    }

    public void onSungokClick() {
        if (!(BtDevice.getDevice() == null)) {
            if (Tempo == temp && c == 0 && VerSionMachin.getName().equals("G10")) {
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
        navi.threeCall();
    }

    public void onResvClick() {
        if (!(BtDevice.getDevice() == null)) {
            if (Tempo == temp && c == 0 && VerSionMachin.getName().equals("G10")) {
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
        navi.fourCall();
    }

    public void onFirstClick() {
        if (!(BtDevice.getDevice() == null)) {
            if (Tempo == temp && c == 0 && VerSionMachin.getName().equals("G10")) {
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
        navi.fiveCall();
    }

    @Override
    public void onCreate() {
        record = bundle.getInt("record");
        kind = bundle.getInt("kind");
        touch = bundle.getInt("touch");
        if (kind == 1) {
            lSong = (LoveFile) bundle.getSerializable("song");
            getLoveSong(lSong.Number);
            Tempo = lSong.Tempo; // 바뀐 템포
            AbsMain = lSong.PlayKey; // 바뀐 음정
            number = String.valueOf(lSong.Number);
            c = 0;
            temp = loveList.get(0).Tempo;
            OrgAbs = loveList.get(0).AbsMain;
            OrgAbs2 = "0" + loveList.get(0).AbsMain;

            Txt.set(String.valueOf(lSong.Number));
            Txt2.set(loveList.get(0).Title);
            Txt3.set(loveList.get(0).Singer);
        } else if (kind == 2) {
            song = (MySlove) bundle.getSerializable("song");
            c = song.count;
            Tempo = song.Tmep; // 바뀐 템포
            AbsMain = song.AbsMain + c; // 바뀐 음정
            number = String.valueOf(song.Number);
            temp = song.Tempo; // 오리지널 템포
            OrgAbs = song.AbsMain; // 오리지널 음정
            OrgAbs2 = "0" + song.AbsMain; // 오리지널 음정

            Txt.set(String.valueOf(song.Number));
            Txt2.set(song.Title);
            Txt3.set(song.Singer);
        } else {
            cSong = (CustomerFile) bundle.getSerializable("song");
            getLoveSong(cSong.Number);
            Tempo = cSong.Tempo; // 바뀐 템포
            AbsMain = cSong.PlayKey; // 바뀐 음정
            number = String.valueOf(cSong.Number);
            c = 0;
            temp = loveList.get(0).Tempo;
            OrgAbs = loveList.get(0).AbsMain;
            OrgAbs2 = "0" + loveList.get(0).AbsMain;

            text = cSong.Number + "\n" + loveList.get(0).Title + "\n" + loveList.get(0).Singer;

            Txt.set(String.valueOf(cSong.Number));
            Txt2.set(loveList.get(0).Title);
            Txt3.set(loveList.get(0).Singer);
        }

        for (int i = number.length(); i < 7; i++) {
            number = "0" + number;
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

    private void getLoveSong(int text) {
        DataAdapter mDbHelper = new DataAdapter(Application.applicationContext());
        mDbHelper.open();
        // db에 있는 값들을 model을 적용해서 넣는다.
        loveList = mDbHelper.getCustomer(text);
        // db 닫기
        mDbHelper.close();
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
}
