package com.elf.remote.model.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.elf.remote.model.data.VerSionMachin;
import com.elf.remote.view.my_recorded.MyRecorded;
import com.elf.remote.viewmodel.search.SendLoveViewModel;
import com.elf.remote.viewmodel.search.TimeSetViewModel;
import com.elf.remote.viewmodel.settings.SettingsViewModel;
import com.elf.remote.viewmodel.settings_manage.SettingsManageViewModel;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class BluetoothCon {
    final static UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public static ConnectedBluetoothThread mThreadConnectedBluetooth;
    public static ConnectBtThread mConnectBtThread;
    BluetoothDevice mBluetoothDevice;
    public static MyRecorded videoFragment;
    public boolean isThat = true;

    private final Context mContext;

    public static Handler handler = new Handler();

    public static String data, dataDir;

    public static SettingsManageViewModel timeSetFragment = null;

    public static TimeSetViewModel timeSetViewModel = null;

    public static SendLoveViewModel sendLoveViewModel = null;

    public static SettingsViewModel settingsViewModel = null;

    public static int totBlockNum, remainNum;

    public static byte[] dstTrData;

    public static byte[][] asd = new byte[1024][64];

    public static int Write = 0;
    public static int Read = 0;
    public static int chk = 0;

    static int Ido = 0;

    public BluetoothCon(Context mContext) {
        this.mContext = mContext;
    }

    public void getDataDir(String str) {
        dataDir = str;
    }

    public void getSettingsViewModel(SettingsViewModel settingsViewModel) {
        BluetoothCon.settingsViewModel = settingsViewModel;
    }
    public void getFragment(SettingsManageViewModel dialogFragment) {
        timeSetFragment = dialogFragment;
    }

    public void getTimeSet(TimeSetViewModel timeSetView) {
        timeSetViewModel = timeSetView;
    }

    public void getSendFragment(SendLoveViewModel sendLoveView) {
        sendLoveViewModel = sendLoveView;
    }

    public void connectSelectedDevice(BluetoothDevice selectedDeviceName) {
        mBluetoothDevice = selectedDeviceName;
        mConnectBtThread = new ConnectBtThread(mBluetoothDevice);
        mConnectBtThread.start();
    }

    public void disconnect () throws IOException {
        if (mConnectBtThread != null && mThreadConnectedBluetooth != null) {
            if (mThreadConnectedBluetooth.mmInStream != null) {
                mThreadConnectedBluetooth.mmInStream.close();
            }
            if (mThreadConnectedBluetooth.mmOutStream != null){
                mThreadConnectedBluetooth.mmOutStream.close();
            }
            if (mConnectBtThread.mSocket != null) {
                mConnectBtThread.mSocket.close();
            }
            VerSionMachin.setCon(false);
            isThat = true;
        }
    }

    public void commandBluetooth(String str) {
        if (mThreadConnectedBluetooth != null) {
            mThreadConnectedBluetooth.write(str);
        }
    }

    public void commandBluetooth2(byte[] str) {
        if (mThreadConnectedBluetooth != null) {
            mThreadConnectedBluetooth.outWrite(str);
        }
    }

    public void readBluetooth(MyRecorded video) {
        videoFragment = video;
        Ido = 1;
    }

    public static void readData() {
        mThreadConnectedBluetooth.readData();
    }

    private static class ConnectBtThread extends Thread {
        private final BluetoothSocket mSocket;

        public ConnectBtThread(BluetoothDevice device) {
            BluetoothSocket socket = null;
            try {
                socket = device.createInsecureRfcommSocketToServiceRecord(BT_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mSocket = socket;
        }

        @Override
        public void run() {
            try {
                mSocket.connect();
            } catch (IOException e) {
                try {
                    mSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
            mThreadConnectedBluetooth = new ConnectedBluetoothThread(mSocket);
            mThreadConnectedBluetooth.start();
            mThreadConnectedBluetooth.write("VERSIONREQ");
        }
    }

    public static byte[] aa = new byte[32];

    private static class ConnectedBluetoothThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        int z, x, fileblockNum, fileblockNum2 = 0;

        public ConnectedBluetoothThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void readData() {
            if (chk > 0 && Read < Write || chk > 0 && Read >= 1019 || chk > 0 && Write == 0) {
                System.arraycopy(asd[Read], 0, aa, 0, 32);
                Read++;

                if (Read >= 1024) Read = 0;
            }
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    int bytes = mmInStream.available();
                    if (bytes != 0) {
                        byte[] packetBytes = new byte[bytes];

                        mmInStream.read(packetBytes);

                        data = new String(packetBytes, StandardCharsets.US_ASCII);
                        Log.d("확인", data);

                        handler.post(() -> {
                            if (data.startsWith("SMARTEQ")) {
                                for (int i = 0; i < 5; i++) {
                                    System.arraycopy(packetBytes, 8 + 32 * i, asd[Write], 0, 32);
                                    Write++;
                                    if (Write >= 1024) {
                                        Write = 0;
                                    }
                                }
                                chk = 1;
                                return;
                            }
                            if (Ido == 1) {
                                if (data.startsWith("MACHSNGST") && videoFragment.Stop == 0 && videoFragment.mIsRecordingVideo) {
                                    videoFragment.stopRecordingVideo();
                                } else if (data.equals("MACHNOTRDY")) {
                                    VerSionMachin.setRdy(false);
                                } else if (data.equals("MACHRECRDY")) {
                                    VerSionMachin.setRdy(true);
                                }
                            }

                            if (data.startsWith("MACHVERS")) {
                                VerSionMachin.setName(data.substring(8, 11));
                                VerSionMachin.setCon(true);
                            }

                            if (data.startsWith("INFI")) {
                                byte[] a = new byte[bytes - 40];
                                System.arraycopy(packetBytes, 40, a, 0, packetBytes.length - 40);

                                z = (packetBytes[4] << 24) & 0xff000000;
                                z = z + ((packetBytes[5] << 16) & 16711680);
                                z = z + ((packetBytes[6] << 8) & 0x0000ff00);
                                z = z + (packetBytes[7] & 255);

                                x = ((packetBytes[26] << 8) & 65280);
                                x = x + (packetBytes[27] & 255);

                                outPutFile(a);

                                byte i = (byte) ((packetBytes[22] << 8) & 65280);
                                int j = i + (packetBytes[23] & 255);
                                j += 1;

                                byte[] arr = {66, 76, 79, 67, 75, (byte) ((j >> 8) & 0xff), (byte) (j & 255), 10};
                                outWrite(arr);

                                if (timeSetFragment != null)
                                    timeSetFragment.updateProgressDialog((x * 100) / z);
                                else if (timeSetViewModel != null)
                                    timeSetViewModel.updateProgressDialog((x * 100) / z);
                                else if (settingsViewModel != null)
                                    settingsViewModel.updateProgressDialog((x * 100) / z);

                            }

                            if (data.startsWith("BLOK")) {
                                byte[] a = new byte[bytes - 14];
                                System.arraycopy(packetBytes, 14, a, 0, packetBytes.length - 14);

                                int x = (packetBytes[8] << 8) & 0xff00;
                                x = x + (packetBytes[9] & 0xff);
                                this.x = this.x + x;

                                outPutFile(a);

                                fileblockNum = (byte) ((packetBytes[4] << 8) & 0xff);
                                fileblockNum += (packetBytes[5] & 0xff);
                                int j = fileblockNum + 1;
                                if (j == 256) fileblockNum2 =+ 256;

                                byte[] arr = {66, 76, 79, 67, 75, (byte) ((fileblockNum2 >> 8) & 0xff), (byte) (j & 0xff), 10};
                                outWrite(arr);

                                if (timeSetFragment != null) {
                                    timeSetFragment.updateProgressDialog((this.x * 100) / z);
                                    if ((this.x*100)/z == 100) fileblockNum2 = 0;
                                }
                                else if (timeSetViewModel != null) {
                                    timeSetViewModel.updateProgressDialog((this.x * 100) / z);
                                    if ((this.x*100)/z == 100) fileblockNum2 = 0;
                                }
                                else if (settingsViewModel != null) {
                                    settingsViewModel.updateProgressDialog((this.x * 100) / z);
                                    if ((this.x*100)/z == 100) fileblockNum2 = 0;
                                }
                            }

                            if (data.equals("HDBFILEREQ")) {
                                File file = new File(dataDir);
                                try {
                                    FileInputStream fileInputStream = new FileInputStream(file);
                                    int available = fileInputStream.available();
                                    z = available;
                                    byte[] bArr = new byte[available];
                                    dstTrData = bArr;
                                    fileInputStream.read(bArr);
                                    fileInputStream.close();
                                    if (available >= 180) {
                                        byte[] bArr2 = new byte[220];
                                        bArr2[0] = 68;
                                        bArr2[1] = 70;
                                        bArr2[2] = 73;
                                        bArr2[3] = 76;
                                        bArr2[4] = (byte) ((available >> 24) & 255);
                                        bArr2[5] = (byte) ((available >> 16) & 255);
                                        bArr2[6] = (byte) ((available >> 8) & 255);
                                        bArr2[7] = (byte) (available & 255);
                                        bArr2[8] = 7;
                                        bArr2[9] = 108;
                                        bArr2[10] = 111;
                                        bArr2[11] = 118;
                                        bArr2[12] = 46;
                                        bArr2[13] = 98;
                                        bArr2[14] = 105;
                                        bArr2[15] = 110;
                                        int i2 = 0;
                                        for (int i3 = 0; i3 < available; i3++) {
                                            i2 += dstTrData[i3] & 255;
                                        }
                                        bArr2[28] = (byte) ((i2 >> 8) & 255);
                                        bArr2[29] = (byte) (i2 & 255);
                                        bArr2[22] = 0;
                                        bArr2[23] = 1;
                                        bArr2[26] = (byte) 0;
                                        bArr2[27] = (byte) 180;
                                        int i4 = 0;
                                        for (int i5 = 0; i5 < 40; i5++) {
                                            if (i5 != 24) {
                                                if (i5 != 25) {
                                                    i4 += bArr2[i5] & 255;
                                                }
                                            }
                                        }
                                        for (int i6 = 0; i6 < 180; i6++) {
                                            i4 += dstTrData[i6] & 255;
                                        }
                                        bArr2[24] = (byte) ((i4 >> 8) & 255);
                                        bArr2[25] = (byte) (i4 & 255);
                                        int i7 = available - 180;
                                        totBlockNum = 1;
                                        int i8 = (i7 / 206) + 1;
                                        totBlockNum = i8;
                                        int i9 = i7 % 206;
                                        remainNum = i9;
                                        if (i9 > 0) {
                                            totBlockNum = i8 + 1;
                                        }
                                        System.arraycopy(dstTrData, 0, bArr2, 40, 180);
                                        outWrite(bArr2);
                                    } else {
                                        byte[] bArr3 = new byte[(available + 40)];
                                        bArr3[0] = 68;
                                        bArr3[1] = 70;
                                        bArr3[2] = 73;
                                        bArr3[3] = 76;
                                        bArr3[4] = (byte) ((available >> 24) & 255);
                                        bArr3[5] = (byte) ((available >> 16) & 255);
                                        bArr3[6] = (byte) ((available >> 8) & 255);
                                        bArr3[7] = (byte) (available & 255);
                                        bArr3[8] = 7;
                                        bArr3[9] = 108;
                                        bArr3[10] = 111;
                                        bArr3[11] = 118;
                                        bArr3[12] = 46;
                                        bArr3[13] = 98;
                                        bArr3[14] = 105;
                                        bArr3[15] = 110;
                                        int i11 = 0;
                                        for (int i12 = 0; i12 < available; i12++) {
                                            i11 += dstTrData[i12] & 255;
                                        }
                                        bArr3[28] = (byte) ((i11 >> 8) & 255);
                                        bArr3[29] = (byte) (i11 & 255);
                                        bArr3[22] = 0;
                                        bArr3[23] = 1;
                                        bArr3[26] = (byte) ((available >> 8) & 255);
                                        bArr3[27] = (byte) (available & 255);
                                        int i14 = 0;
                                        for (int i15 = 0; i15 < 40; i15++) {
                                            if (i15 != 24) {
                                                if (i15 != 25) {
                                                    i14 += bArr3[i15] & 255;
                                                }
                                            }
                                        }
                                        for (int i16 = 0; i16 < available; i16++) {
                                            i14 += dstTrData[i16] & 255;
                                        }
                                        bArr3[24] = (byte) ((i14 >> 8) & 255);
                                        bArr3[25] = (byte) (i14 & 255);
                                        totBlockNum = 1;
                                        remainNum = available % 180;
                                        System.arraycopy(dstTrData, 0, bArr3, 40, available);
                                        outWrite(bArr3);
                                    }

                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            if (data.startsWith("HBLOCK")) {
                                int i2 = ((packetBytes[6] << 8) & 65280) + (packetBytes[7] & 255);
                                int i3 = totBlockNum;

                                if (i2 <= i3) {
                                    if (i2 == i3) {
                                        if (remainNum > 0) {
                                            byte[] bArr = new byte[(remainNum + 14)];
                                            bArr[0] = 68;
                                            bArr[1] = 66;
                                            bArr[2] = 76;
                                            bArr[3] = 75;
                                            bArr[4] = (byte) ((i2 >> 8) & 255);
                                            bArr[5] = (byte) (i2 & 255);
                                            int i4 = remainNum;
                                            bArr[8] = (byte) ((i4 >> 8) & 255);
                                            bArr[9] = (byte) (i4 & 255);
                                            int i5 = 0;
                                            for (int i6 = 0; i6 < 14; i6++) {
                                                if (i6 != 6) {
                                                    if (i6 != 7) {
                                                        i5 += bArr[i6] & 255;
                                                    }
                                                }
                                            }
                                            for (int i7 = 0; i7 < remainNum; i7++) {
                                                i5 += dstTrData[((i2 - 2) * 206) + 180 + i7] & 255;
                                            }
                                            bArr[6] = (byte) ((i5 >> 8) & 255);
                                            bArr[7] = (byte) (i5 & 255);
                                            System.arraycopy(dstTrData, ((i2 - 2) * 206) + 180, bArr, 14, remainNum);
                                            outWrite(bArr);
                                            //dtaTrPercent = 100;
                                            //MachinTransferFlag = 0;
                                            if (timeSetFragment != null)
                                                timeSetFragment.updateProgressDialog(100);
                                            else if (sendLoveViewModel != null)
                                                sendLoveViewModel.updateProgressDialog(100);
                                            else if (settingsViewModel != null)
                                                settingsViewModel.updateProgressDialog(100);
                                            else if (timeSetViewModel != null)
                                                timeSetViewModel.updateProgressDialog(100);
                                            return;
                                        }
                                    }
                                    byte[] bArr2 = new byte[220];
                                    bArr2[0] = 68;
                                    bArr2[1] = 66;
                                    bArr2[2] = 76;
                                    bArr2[3] = 75;
                                    bArr2[4] = (byte) ((i2 >> 8) & 255);
                                    bArr2[5] = (byte) (i2 & 255);
                                    bArr2[8] = (byte) 0;
                                    bArr2[9] = (byte) 206;
                                    int i8 = 0;
                                    for (int i9 = 0; i9 < 14; i9++) {
                                        if (i9 != 6) {
                                            if (i9 != 7) {
                                                i8 += bArr2[i9] & 255;
                                            }
                                        }
                                    }
                                    for (int i10 = 0; i10 < 206; i10++) {
                                        i8 += dstTrData[((i2 - 2) * 206) + 180 + i10] & 255;
                                    }
                                    bArr2[6] = (byte) ((i8 >> 8) & 255);
                                    bArr2[7] = (byte) (i8 & 255);
                                    System.arraycopy(dstTrData, ((i2 - 2) * 206) + 180, bArr2, 14, 206);
                                    outWrite(bArr2);

                                    if (timeSetFragment != null)
                                        timeSetFragment.updateProgressDialog((((i2 - 1) * 206) * 100) / z);
                                    else if (sendLoveViewModel != null)
                                        sendLoveViewModel.updateProgressDialog((((i2 - 1) * 206) * 100) / z);
                                    else if (settingsViewModel != null)
                                        settingsViewModel.updateProgressDialog((((i2 - 1) * 206) * 100) / z);
                                    else if (timeSetViewModel != null)
                                        timeSetViewModel.updateProgressDialog((((i2 - 1) * 206) * 100) / z);
                                }
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        public void write(String str) {
            byte[] bytes = str.getBytes();
            try {
                mmOutStream.write(bytes);
                mmOutStream.flush();
                Thread.sleep(20);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void outWrite(byte[] str) {
            try {
                mmOutStream.write(str);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void outPutFile(byte[] packetBytes) {
            File file = new File(dataDir);
            FileOutputStream fos = null;
            BufferedOutputStream bufos = null;

            try {
                fos = new FileOutputStream(file, true);
                bufos = new BufferedOutputStream(fos);

                bufos.write(packetBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (bufos != null)
                    bufos.close();

                if (fos != null)
                    fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}