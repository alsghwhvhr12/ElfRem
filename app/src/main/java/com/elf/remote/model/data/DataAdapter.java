package com.elf.remote.model.data;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.elf.remote.utils.ChoSearchQuery;

import java.util.ArrayList;
import java.util.List;

public class DataAdapter {
    protected static String TABLE_NAME = "TB_Song";

    private SQLiteDatabase mDb;
    private final MySongDataBase mDbHelper;
    String order = "Idx ASC";
    String model = "0x10";

    public DataAdapter(Context context) {
        mDbHelper = new MySongDataBase(context);
    }


    public DataAdapter open() throws SQLException {
        mDbHelper.openDataBase();
        mDbHelper.close();
        mDb = mDbHelper.getReadableDatabase();
        return this;
    }

    public String searchSql(String searchStr, int country, String model, int min) {
        String sql = "SELECT * FROM " + TABLE_NAME;
        if (!TextUtils.isEmpty(searchStr)) {
            sql += " WHERE ";
            sql += ChoSearchQuery.makeQuery(searchStr, country, model, min);
        }

        return sql;
    }


    public void close() {
        mDbHelper.close();
    }

    public List<MySong> getTableData(String text, String kind, int country) {
        String sql;
        if (VerSionMachin.getName().equals("G10")) {
            model = "0x10";
        } else {
            model = "0x01";
        }
        if (kind.equals("Singer")) {
            order = "Idx ASC";

            sql = "SELECT * FROM " + TABLE_NAME +
                    " WHERE (SingerID = '" + text + "'" +
                    ") AND (Country = " + country + ") AND (Model & " + model + ") ORDER BY " + order;

        } else {
            if (kind.equals("Number")) {
                sql = "SELECT * FROM " + TABLE_NAME +
                        " WHERE (" + kind + " = " + "'" + text + "'" +
                        ") AND (Country = " + country + ") AND (Model & " + model + ")";

                Cursor mCur = mDb.rawQuery(sql, null);

                if (mCur.moveToNext()) {
                    order = "Number ASC";
                    sql = "SELECT * FROM " + TABLE_NAME +
                            " WHERE (" + kind + " >= " + "'" + text + "'" +
                            ") AND (Country = " + country + ") AND (Model & " + model + ") ORDER BY " + order + " LIMIT 0, 20";
                    mCur.close();
                }
            } else {
                sql = searchSql(text, country, model, 0);
            }
        }

        // 모델 넣을 리스트 생성
        ArrayList<MySong> MySonglist = new ArrayList<>();

        // TODO : 모델 선언
        MySong user;
        Cursor mCur = null;
        try {
            mDb.beginTransaction();
            mCur = mDb.rawQuery(sql, null);
            mDb.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            mDb.endTransaction();
        }

        if (mCur != null) {
            // 칼럼의 마지막까지
            while (mCur.moveToNext()) {
                // TODO : 커스텀 모델 생성
                user = new MySong();

                // TODO : Record 기술
                // number, country, singerId, Idx, Title, Singer, Tempo, Main, AbsMain, AbsMan, AbsWoman
                user.setNumber(mCur.getInt(0));
                user.setCountry(mCur.getInt(1));
                user.setSingerID(mCur.getString(2));
                user.setIdx(mCur.getString(3));
                user.setTitle(mCur.getString(4));
                user.setSinger(mCur.getString(5));
                user.setTempo(mCur.getInt(6));
                user.setMain(mCur.getString(7));
                user.setAbsMain(mCur.getInt(11));
                user.setAbsMan(mCur.getInt(12));
                user.setAbsWoman(mCur.getInt(13));

                // 리스트에 넣기
                MySonglist.add(user);
            }
            mCur.close();
        }
        return MySonglist;
    }

    public List<MySong> getScrollData(String text, String kind, int country, int min, List<MySong> MySongList) {
        String sql;
        if (VerSionMachin.getName().equals("G10")) {
            model = "0x10";
        } else {
            model = "0x01";
        }
        if (kind.equals("Singer")) {
            order = "Idx ASC";

            sql = "SELECT * FROM " + TABLE_NAME +
                    " WHERE (SingerID = '" + text + "'" +
                    ") AND (Country = " + country + ") AND (Model & " + model + ") ORDER BY " + order;

        } else {
            if (kind.equals("Number")) {
                order = "Number ASC";
                sql = "SELECT * FROM " + TABLE_NAME +
                        " WHERE (" + kind + " >= " + "'" + text + "'" +
                        ") AND (Country = " + country + ") AND (Model & " + model + ") ORDER BY " + order + " LIMIT " + min + ", 20";
            } else {
                sql = searchSql(text, country, model, min);
            }
        }

        // TODO : 모델 선언
        MySong user;

        Cursor mCur = mDb.rawQuery(sql, null);
        if (mCur != null) {
            // 칼럼의 마지막까지
            while (mCur.moveToNext()) {

                // TODO : 커스텀 모델 생성
                user = new MySong();

                // TODO : Record 기술
                // number, country, singerId, Idx, Title, Singer, Tempo, Main, AbsMain, AbsMan, AbsWoman
                user.setNumber(mCur.getInt(0));
                user.setCountry(mCur.getInt(1));
                user.setSingerID(mCur.getString(2));
                user.setIdx(mCur.getString(3));
                user.setTitle(mCur.getString(4));
                user.setSinger(mCur.getString(5));
                user.setTempo(mCur.getInt(6));
                user.setMain(mCur.getString(7));
                user.setAbsMain(mCur.getInt(11));
                user.setAbsMan(mCur.getInt(12));
                user.setAbsWoman(mCur.getInt(13));

                // 리스트에 넣기
                MySongList.add(user);
            }
            mCur.close();
        }
        return MySongList;
    }

    public List<mySinger> getSingerData(String text, String kind, int country) {
        String sql;

        sql = "SELECT * FROM TB_Singer WHERE (" + kind + " LIKE " + "'%" + text + "%'" +
                ") AND (Country = " + country + ")";


        // 모델 넣을 리스트 생성
        ArrayList<mySinger> MySonglist2 = new ArrayList<>();

        // TODO : 모델 선언
        mySinger user;

        Cursor mCur = mDb.rawQuery(sql, null);
        if (mCur != null) {
            // 칼럼의 마지막까지
            while (mCur.moveToNext()) {

                // TODO : 커스텀 모델 생성
                user = new mySinger();

                // TODO : Record 기술
                // singerId, Country, Idx, Singer
                user.setSingerID(mCur.getString(0));
                user.setCountry(mCur.getInt(1));
                user.setIdx(mCur.getString(2));
                user.setSinger(mCur.getString(3));

                // 리스트에 넣기
                MySonglist2.add(user);
            }
            mCur.close();
        }
        return MySonglist2;
    }

    public String getSongTitle(String text) {
        String sql;

        sql = "SELECT * FROM " + TABLE_NAME +
                " WHERE Number = '" + text + "'";

        Cursor mCur = mDb.rawQuery(sql, null);

        String title = null;
        if (mCur != null) {
            // 칼럼의 마지막까지
            while (mCur.moveToNext()) {
                title = mCur.getString(4);
            }
            mCur.close();
        }
        return title;
    }

    public List<MySong> getCustomer(int text) {
        String sql;

        sql = "SELECT * FROM " + TABLE_NAME +
                " WHERE Number = '" + text + "'";

        Cursor mCur = mDb.rawQuery(sql, null);

        // 모델 넣을 리스트 생성
        List<MySong> MySonglist = new ArrayList<>();

        // TODO : 모델 선언
        MySong user;

        if (mCur != null) {
            // 칼럼의 마지막까지
            while (mCur.moveToNext()) {
                user = new MySong();

                user.setTitle(mCur.getString(4));
                user.setSinger(mCur.getString(5));
                user.setTempo(mCur.getInt(6));
                user.setMain(mCur.getString(7));
                user.setAbsMain(mCur.getInt(11));

                // 리스트에 넣기
                MySonglist.add(user);
            }
            mCur.close();
        }
        return MySonglist;
    }
}
