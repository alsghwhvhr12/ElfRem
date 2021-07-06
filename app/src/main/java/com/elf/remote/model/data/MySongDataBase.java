package com.elf.remote.model.data;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySongDataBase extends SQLiteOpenHelper {
    static final String DB_NAME = "sqlitedb.db";
    private static String DB_PATH = "";
    static final int DB_VERSION = 1;

    Context mContext;

    private SQLiteDatabase mDataBase = null;

    public MySongDataBase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;

        DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
    }

    //데이터베이스를 열어서 쿼리를 쓸수있게만든다.
    public void openDataBase() throws SQLException {
        String mPath = DB_PATH + DB_NAME;
        //Log.v("mPath", mPath);
        mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        //mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
    }

    @Override
    public synchronized void close() {
        if (mDataBase != null)
            mDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.disableWriteAheadLogging();
    }
}
