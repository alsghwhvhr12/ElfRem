package com.elf.remote.model.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CustomerGroupDataBase {
    static final String DB_MYLOVE = "mylove.db";
    static final String TABLE_LOVES = "Customer";

    Context context;

    private static CustomerGroupDataBase myLoveDataBase = null;
    private final SQLiteDatabase myDataBase;

    public static CustomerGroupDataBase getInstance(Context context) {
        if (myLoveDataBase == null) {
            myLoveDataBase = new CustomerGroupDataBase(context);
        }

        return myLoveDataBase;
    }

    private CustomerGroupDataBase(Context context) {
        this.context = context;

        myDataBase = context.openOrCreateDatabase(DB_MYLOVE, Context.MODE_PRIVATE, null);

        myDataBase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_LOVES +
                "(" + "CustomerID INTEGER," +
                "Name TEXT," +
                "Memo TEXT);");
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
