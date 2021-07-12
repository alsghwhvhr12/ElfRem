package com.elf.remote.view.my_recorded;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.usb.UsbDevice;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.DialogFragment;

import com.elf.mvvmremote.R;
import com.elf.remote.Application;
import com.elf.remote.model.usb.UsbDataBase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import static android.provider.DocumentsContract.EXTRA_INITIAL_URI;


public class UsbPerFragment extends DialogFragment {
    public static final String TAG_EVENT_DIALOG = "dialog_event";

    UsbDataBase usbDataBase;
    UsbDevice device;

    OnMyDialogResult mDr;
    String name;

    public UsbPerFragment() {
    }

    public static UsbPerFragment getInstance() {
        return new UsbPerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.imagefile, container);
        Button con = v.findViewById(R.id.shareButton);
        setCancelable(false);

        usbDataBase = UsbDataBase.getInstance(getContext());

        Bundle bundle = getArguments();
        if (bundle != null) {
            name = bundle.getString("name");
            device = bundle.getParcelable("device");
        }

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        con.setOnClickListener(v1 -> getUsbStoragePer(name));

        return v;
    }

    public void getUsbStoragePer(String usbName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DocumentFile file = DocumentFile.fromTreeUri(Application.applicationContext(), Uri.parse("content://com.android.externalstorage.documents/tree/" + usbName + "%3ATEMP/document/" + usbName + "%3ATEMP"));
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            i.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            if (file != null) {
                i.putExtra(EXTRA_INITIAL_URI, file.getUri());
            }
            //startActivityForResult(i, 2);
            UsbPerChk.launch(i);
        }
    }
    private final byte[] bytes = new byte[1024];

    ActivityResultLauncher<Intent> UsbPerChk = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Uri uri;
                if (result.getData() != null) {
                    uri = result.getData().getData();

                    Application.applicationContext().getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    Uri docUri = DocumentsContract.buildDocumentUriUsingTree(uri, DocumentsContract.getTreeDocumentId(uri));

                    DocumentFile file = DocumentFile.fromTreeUri(Application.applicationContext(), docUri);
                    DocumentFile[] file2 = new DocumentFile[0];
                    if (file != null) {
                        file2 = file.listFiles();
                    }

                    ContentValues addRowValue = new ContentValues();
                    addRowValue.put("usbUri", String.valueOf(docUri));
                    addRowValue.put("usbName", device.getProductId());
                    usbDataBase.insert(addRowValue);

                    for (DocumentFile ff : file2) {
                        try {
                            File files = new File(Application.applicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/ElfData/" + ff.getName());
                            InputStream is = Application.applicationContext().getContentResolver().openInputStream(ff.getUri());
                            FileOutputStream outStream = new FileOutputStream(files);
                            int read;
                            while ((read = is.read(bytes)) != -1) {
                                outStream.write(bytes, 0, read);
                            }
                            is.close();
                            outStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    mDr.finish(true);
                }
            }

            dismiss();
            }
    });

    /*@Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            Uri uri;
            if (resultData != null) {
                uri = resultData.getData();

                Application.applicationContext().getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                Uri docUri = DocumentsContract.buildDocumentUriUsingTree(uri, DocumentsContract.getTreeDocumentId(uri));

                DocumentFile file = DocumentFile.fromTreeUri(Application.applicationContext(), docUri);
                DocumentFile[] file2 = new DocumentFile[0];
                if (file != null) {
                    file2 = file.listFiles();
                }

                ContentValues addRowValue = new ContentValues();
                addRowValue.put("usbUri", String.valueOf(docUri));
                addRowValue.put("usbName", device.getProductId());
                usbDataBase.insert(addRowValue);

                for (DocumentFile ff : file2) {
                    try {
                        File files = new File(Application.applicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/ElfData/" + ff.getName());
                        InputStream is = Application.applicationContext().getContentResolver().openInputStream(ff.getUri());
                        FileOutputStream outStream = new FileOutputStream(files);
                        int read;
                        while ((read = is.read(bytes)) != -1) {
                            outStream.write(bytes, 0, read);
                        }
                        is.close();
                        outStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                mDr.finish(true);
            }
        }

        dismiss();
    }*/

    public void setDialogR(OnMyDialogResult dialogR) {
        mDr = dialogR;
    }

    public interface OnMyDialogResult {
        void finish(boolean result);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}