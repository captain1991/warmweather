package com.xiaodong.warmweather.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yxd on 2017/3/10.
 */
public class MyDataBaseHelper extends SQLiteOpenHelper {
    private Context mContext;
    private final String CREATE_TABLE = "create table suggestion( id integer primary key autoincrement, county text, air text, comf text, cw text,sport text)";
    public MyDataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
