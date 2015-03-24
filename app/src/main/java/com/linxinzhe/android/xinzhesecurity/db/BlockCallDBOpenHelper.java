package com.linxinzhe.android.xinzhesecurity.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BlockCallDBOpenHelper extends SQLiteOpenHelper {

    public BlockCallDBOpenHelper(Context context) {
        super(context, "blockphone.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE blockphone (_id integer primary key autoincrement,phone varchar(20),mode varchar(2))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
