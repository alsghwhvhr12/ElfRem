package com.elf.remote.model.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MyResvDataBase {
    static final String DB_MYLOVE = "myResv.db";
    static final String TABLE_LOVES = "myRes";

    Context context;

    private static MyResvDataBase myLoveDataBase = null;
    private final SQLiteDatabase myDataBase;

    public static MyResvDataBase getInstance(Context context) {
        if (myLoveDataBase == null) {
            myLoveDataBase = new MyResvDataBase(context);
        }

        return myLoveDataBase;
    }

    private MyResvDataBase(Context context) {
        this.context = context;

        myDataBase = context.openOrCreateDatabase(DB_MYLOVE, Context.MODE_PRIVATE, null);


        myDataBase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_LOVES +
                "(" + "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "number INTEGER," +
                "title TEXT," +
                "singer TEXT," +
                "tempo INTEGER," +
                "tempo2 INTEGER," +
                "interval2 TEXT," +
                "count INTEGER," +
                "AbsMain INTEGER," +
                "interval TEXT);");
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
