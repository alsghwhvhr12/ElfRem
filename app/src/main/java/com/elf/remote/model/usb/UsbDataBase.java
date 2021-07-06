package com.elf.remote.model.usb;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UsbDataBase {
    static final String DB_MYLOVE = "usbFile.db";
    static final String TABLE_LOVES = "UsbList";

    Context context;

    @SuppressLint("StaticFieldLeak")
    private static UsbDataBase myLoveDataBase = null;
    private final SQLiteDatabase myDataBase;

    public static UsbDataBase getInstance(Context context) {
        if (myLoveDataBase == null) {
            myLoveDataBase = new UsbDataBase(context);
        }

        return myLoveDataBase;
    }

    private UsbDataBase(Context context) {
        this.context = context;

        myDataBase = context.openOrCreateDatabase(DB_MYLOVE, Context.MODE_PRIVATE, null);

        myDataBase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_LOVES +
                "(" + "usbUri String," +
                "usbName INTEGER);");
    }

    public void insert(ContentValues addRowValue) {
        myDataBase.insert(TABLE_LOVES, null, addRowValue);
    }

    public void update(ContentValues updateRowValue, String whereClause, String[] whereArgs) {
        myDataBase.update(TABLE_LOVES, updateRowValue, whereClause, whereArgs);
    }

    public int delete(String whereClause, String[] whereArgs) {
        return myDataBase.delete(TABLE_LOVES, whereClause, whereArgs);
    }

    public Cursor query(String[] colums,
                        String selection,
                        String[] selectionArgs,
                        String groupBy,
                        String having,
                        String orderby) {
        return myDataBase.query(TABLE_LOVES,
                colums,
                selection,
                selectionArgs,
                groupBy,
                having,
                orderby);
    }
}
