package com.elf.remote.model.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class LoveCountDataBase {
    static String DB_MYLOVE = "lovecount.db";
    static String TABLE_LOVES = "MySongGroup";

    Context context;

    private final SQLiteDatabase myDataBase;

    public static LoveCountDataBase getInstance(Context context, String table, String db) {
        DB_MYLOVE = db;
        TABLE_LOVES = table;

        String Group;
        if (TABLE_LOVES.equals("MySongGroup")) Group = "GroupID";
        else Group = "CustomerID";
        return new LoveCountDataBase(context, Group);
    }

    private LoveCountDataBase(Context context, String group) {
        this.context = context;

        myDataBase = context.openOrCreateDatabase(DB_MYLOVE, Context.MODE_PRIVATE, null);

        myDataBase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_LOVES +
                "(" + group + " INTEGER," +
                "Name TEXT," +
                "Total INTEGER);");
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
